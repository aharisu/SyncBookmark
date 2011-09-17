package aharisu.tools.SyncBookmarks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.mail.internet.MimeUtility;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aharisu.tools.SyncBookmarks.Data.UserStore;


/**
 * Basic認証を行うためのフィルター
 * 設定ファイルでBasic認証をしなくてもアクセスできるパスを指定できる
 * @author aharisu
 *
 */
public class BasicAuthenticationFilter implements Filter{
	
	private final String realmName = "authentication";
	private String[] ignoreUrls;

	@Override public void init(FilterConfig config) throws ServletException {
		String urls = config.getInitParameter("ignore_url");
		ignoreUrls = urls.split(":");
	}

	@Override public void destroy() {
	}

	
	@Override public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest)req;
		String url = httpReq.getRequestURI();
		for(String ignoreUrl : ignoreUrls) {
			//無視するurlであれば何もしない
			if(ignoreUrl.equals(url)) {
				chain.doFilter(req, res);
				return;
			}
		}
		
		
		ByteArrayInputStream bin = null;
		BufferedReader br = null;
		try {
			String basicAuthData = httpReq.getHeader("authorization");
			if(basicAuthData == null) {
				setAuthFailueResponse((HttpServletResponse)res);
				return;
			}
			
			String basicAuthBody = basicAuthData.substring(6);
			bin = new ByteArrayInputStream(basicAuthBody.getBytes());
			br = new BufferedReader(new InputStreamReader(MimeUtility.decode(bin, "base64")));
			
			StringBuilder buf = new StringBuilder();
			String line = null;
			while((line = br.readLine()) != null) {
				buf.append(line);
			}
			String[] loginInfo = buf.toString().split(":");
			String username = safeArrayElement(loginInfo, 0, "");
			String password = safeArrayElement(loginInfo, 1, "");
			
			if(UserStore.exists(username, password)) {
				chain.doFilter(req, res);
			} else {
				setAuthFailueResponse((HttpServletResponse)res);
				return;
			}
			
		} catch(Exception e) {
			throw new ServletException(e);
		} finally {
			try {
				if(bin != null)
					bin.close();
				if(br != null)
					br.close();
			}catch(Exception e) {
			}
		}
		
	}
	
	private void setAuthFailueResponse(HttpServletResponse res) throws IOException {
		//認証失敗401エラーコードを返す
		res = (HttpServletResponse)res;
		res.setHeader("WWW-Authenticate", "Basic realm=" + this.realmName);
		res.setContentType("text/html");
		res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	private <T> T safeArrayElement(T[] ary, int index, T defaultValue) {
		if(ary.length >= index) {
			return ary[index];
		} else {
			return defaultValue;
		}
	}
		
}
