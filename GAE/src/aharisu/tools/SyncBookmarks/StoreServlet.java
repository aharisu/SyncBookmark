package aharisu.tools.SyncBookmarks;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.*;


import aharisu.tools.SyncBookmarks.Data.Bookmark;
import aharisu.tools.SyncBookmarks.Data.Icon;
import aharisu.tools.SyncBookmarks.Data.PMF;

import com.google.appengine.api.datastore.Blob;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

/**
 * クライアントからブックマークデータを受信して保存するサーブレット
 * @author aharisu
 *
 */
@SuppressWarnings("serial")
public class StoreServlet extends HttpServlet {
	
	
	@Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		
		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = resp.getWriter();
		
		String title = null;
		String url = null;
		Blob icon = null;
		try {
			MultipartParser parser = new MultipartParser(req, req.getContentLength());
			parser.setEncoding("UTF-8");
			
			Part part = null;
			while((part = parser.readNextPart()) != null) {
				String name = part.getName();
				if(name.equals("title")) {
					if(part.isParam()) {
						title = ((ParamPart)part).getStringValue();
					}
				} else if(name.equals("url")) {
					if(part.isParam()) {
						url = ((ParamPart)part).getStringValue();
					}
				} else if(name.equals("icon")) {
					if(part.isParam()) {
						icon = new Blob(((ParamPart)part).getValue());
					}
				}
			}
		} catch(Exception e) {
			log("exception");
			//データのフォーマットがあっていなければ失敗ステータスを返して終了
			writer.println(Responder.genResponse(Responder.ResponseStauts.IllegalDataFormat));
			return;
		}
		if(title == null || url == null || icon == null) {
			log("null");
			//データが一つでも未設定なら失敗ステータスを返して終了
			writer.println(Responder.genResponse(Responder.ResponseStauts.IllegalDataFormat));
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Icon iconData = pm.makePersistent(new Icon(icon));
			pm.makePersistent(new Bookmark(url, title, iconData.getId()));
		} finally {
			pm.close();
		}
		
		writer.println(Responder.genResponse(Responder.ResponseStauts.Success));
	}
	
}
