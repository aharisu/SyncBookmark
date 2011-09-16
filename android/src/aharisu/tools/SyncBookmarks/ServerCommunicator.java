package aharisu.tools.SyncBookmarks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import android.graphics.Bitmap.CompressFormat;

/**
 * サーバに対してデータを送信するクラス
 * @author aharisu
 *
 */
public class ServerCommunicator {
	
	private static final String LineEnd = "\r\n";
	private static final String TwoHyphens = "--";
	private static final String EncodeCharset="UTF-8";
	
	private static final String ServerURL = "http://aharisu-syncbookmarks.appspot.com/store";
	private static final String AppToken = "";
	
	private ServerCommunicator() {
	}
	
	/**
	 * ブックマークデータをmultipart/form-data形式でパックしてサーバにPOSTする
	 * @param bookmark
	 * @return
	 */
	public static String sendBookmarkToServer(BookmarkData bookmark) throws IOException{
		final String boundary = Long.toString(System.currentTimeMillis());
		final String separator = new StringBuilder()
			.append(TwoHyphens)
			.append(boundary)
			.append(LineEnd).toString();
		
		URL url = getServerURL();
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		try {
			conn.setRequestMethod("POST");
		} catch(ProtocolException e) {
			//このエラーは来ない
			throw new RuntimeException();
		}
		conn.setRequestProperty("Charset", EncodeCharset);
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		
		DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
		
		//タイトル書き込み
		ds.writeBytes(separator);
		ds.write(new StringBuilder()
				.append("Content-Disposition: form-data; name=\"title\"").append(LineEnd)
				.append(LineEnd)
				.append(bookmark.Title).append(LineEnd)
				.toString().getBytes(EncodeCharset));
		
		//url書き込み
		ds.writeBytes(separator);
		ds.write(new StringBuilder()
				.append("Content-Disposition: form-data; name=\"url\"").append(LineEnd)
				.append(LineEnd)
				.append(bookmark.URL).append(LineEnd)
				.toString().getBytes(EncodeCharset));
		
		//アイコン書き込み
		ds.writeBytes(separator);
		ds.write(new StringBuilder()
				.append("Content-Disposition: form-data; name=\"icon\"").append(LineEnd)
				.toString().getBytes(EncodeCharset));
		ds.write(new StringBuilder()
				.append("Content-Type: application/octet-stream").append(LineEnd)
				.append(LineEnd)
				.toString().getBytes(EncodeCharset));
		
		//アイコンのバイト配列を書き込む
		bookmark.Icon.compress(CompressFormat.PNG, 100, ds);
		
		ds.writeBytes(LineEnd);
		ds.writeBytes(new StringBuilder()
				.append(TwoHyphens).append(boundary).append(TwoHyphens).append(LineEnd)
				.toString());
		
		//書き込み終了
		ds.flush();
		ds.close();
		
		//レスポンスのテキストを読み込む
		String ret = null;
		if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder builder = new StringBuilder();
			for(String line = null; (line = reader.readLine()) != null;) {
				builder.append(line);
			}
			
			ret = builder.toString();
		}
		
		return ret;
	}
	

	private static URL getServerURL() {
		try {
			return new URL(new StringBuilder()
				.append(ServerURL)
				.append("?")
				.append("token=").append(AppToken).toString());
		} catch (MalformedURLException e) {
			//ここは来ない
			e.printStackTrace();
			return null;
		}
	}
	
}
