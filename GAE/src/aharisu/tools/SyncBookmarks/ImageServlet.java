package aharisu.tools.SyncBookmarks;

import java.io.IOException;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aharisu.tools.SyncBookmarks.Data.Icon;
import aharisu.tools.SyncBookmarks.Data.PMF;

/**
 * アイコン画像を取得するためのサーブレット
 * @author aharisu
 *
 */
@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet{
	
	@Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		
		//GetパラメータからIDを取得
		String idStr = req.getParameter("id");
		if(idStr == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		long id;
		try {
			id = Long.parseLong(idStr);
		} catch (NumberFormatException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		
		//データストアから画像を取得
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Icon icon;
		try {
			icon = pm.getObjectById(Icon.class, id);
		} catch(JDOObjectNotFoundException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} finally {
			pm.close();
		}
		
		
		//レスポンスに画像データを書きだす
		resp.setContentType("image/png");
		byte[] blob = icon.getIconBlob().getBytes();
		resp.setContentLength(blob.length);
		ServletOutputStream os = resp.getOutputStream();
		os.write(blob);
		os.flush();
		os.close();
	}

}
