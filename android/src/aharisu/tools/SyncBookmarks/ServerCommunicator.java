package aharisu.tools.SyncBookmarks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;


import aharisu.tools.SyncBookmarks.Data.BookmarkData;
import aharisu.tools.SyncBookmarks.Data.User;
import android.graphics.Bitmap.CompressFormat;

/**
 * サーバに対してデータを送信するクラス
 * @author aharisu
 *
 */
public class ServerCommunicator {
	
	private static final String EncodeCharset="UTF-8";
	private static final String ServerURL = "https://aharisu-syncbookmarks.appspot.com";
	
	private static final class HttpClient {
		private DefaultHttpClient _client;
		private HttpResponse _response;
		
		public HttpClient() {
			this(null, null);
		}
		
		public HttpClient(String user, String password) {
			_client = new DefaultHttpClient();
			
			if(user != null && password != null) {
				_client.getCredentialsProvider().setCredentials(
						AuthScope.ANY,
						new UsernamePasswordCredentials(user, password));
			}
		}
		
		public StatusLine doPost(String url, Map<String, Object> parts) throws IOException {
			final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			Charset charset = Charset.forName(EncodeCharset);
			
			try {
				for(Entry<String, Object> part : parts.entrySet()) {
					Object value = part.getValue();
					
					if(value instanceof String) {
						entity.addPart(part.getKey(), new StringBody((String)value, charset));
					} else if(value instanceof byte[]) {
						entity.addPart(part.getKey(), new ByteArrayBody((byte[])value, null));
					} else {
						throw new RuntimeException("illegal part value type");
					}
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			
			HttpPost post = new HttpPost(url);
			post.setEntity(entity);
			
			_response = _client.execute(post);
			return _response.getStatusLine();
		}
		
		public StatusLine doGet(String url, Map<String, String> params) throws IOException {
			
			if(params != null && params.size() > 0) {
				StringBuilder builder = new StringBuilder(url);
				boolean isFirst = true;
				for(Entry<String, String> param : params.entrySet()) {
					if(isFirst) {
						builder.append("?");
						isFirst = false;
					} else {
						builder.append("&");
					}
					builder.append(param.getKey()).append("=").append(param.getValue());
				}
				
				url = builder.toString();
			}
			
			HttpGet get = new HttpGet(url);
			_response = _client.execute(get);
			return _response.getStatusLine();
		}
		
		public byte[] getContentData() throws IOException {
			if(_response == null) {
				throw new RuntimeException("not setting data yet");
			}
			
			InputStream is = null;
			ByteArrayOutputStream out = null;
			try {
				HttpEntity entity = _response.getEntity();
				is = entity.getContent();
				
				out = new ByteArrayOutputStream();
				byte[] line = new byte[1024];
				while(true) {
					int size = is.read(line);
					if(size <= 0) {
						break;
					}
					out.write(line, 0, size);
				}
				
				return out.toByteArray();
			} finally {
				if(is != null) {
					try {
						is.close();
					}catch(Exception e){}
				}
				if(out != null) {
					try {
						out.close();
					}catch(Exception e){}
				}
			}
		}
		
	}
	
	
	
	private ServerCommunicator() {
	}
	
	/**
	 * ブックマークデータをmultipart/form-data形式でパックしてサーバにPOSTする
	 * @param bookmark
	 * @return
	 */
	public static String sendBookmarkToServer(User user, BookmarkData bookmark) throws IOException{
		if(user == null)
			throw new NullPointerException("user");
		if(bookmark == null)
			throw new NullPointerException("bookmark");
			
		
		HashMap<String, Object> parts = new HashMap<String, Object>();
		parts.put("title", bookmark.Title);
		parts.put("url", bookmark.URL);
		
		ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
		//アイコンのバイト配列を書き込む
		bookmark.Icon.compress(CompressFormat.PNG, 100, imageOut);
		parts.put("icon", imageOut.toByteArray());
		
		HttpClient client = new HttpClient(user.name, user.password);
		StatusLine status = client.doPost(getServerUrl("/store"), parts);
		if(status.getStatusCode() != HttpStatus.SC_OK) {
			if(status.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
				return "{\"status\": \"unauthorized\"}";
			else
				return null;
		}
		
		return new String(client.getContentData(), EncodeCharset);
	}
	
	/**
	 * ユーザ認証もしくはユーザ登録を行う
	 * @param user
	 * @param isUserAuth
	 * @return
	 * @throws IOException
	 */
	public static String sendUserAuthOrRegister(User user, boolean isUserAuth) throws IOException {
		if(user == null)
			throw new NullPointerException("user");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("name", URLEncoder.encode(user.name));
		params.put("password", user.password);
		if(isUserAuth) {
			params.put("auth", "true");
		}
		
		HttpClient client = new HttpClient();
		StatusLine status = client.doGet(getServerUrl("/reg"), params);
		if(status.getStatusCode() != HttpStatus.SC_OK) {
			return null;
		}
		
		return new String(client.getContentData(), EncodeCharset);
	}
	
	private static String getServerUrl(String path) {
		return ServerURL + path;
	}
	
}
