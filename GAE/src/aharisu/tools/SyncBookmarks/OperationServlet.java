package aharisu.tools.SyncBookmarks;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aharisu.tools.SyncBookmarks.Operation.ListOperation;
import aharisu.tools.SyncBookmarks.Operation.NewerOperation;
import aharisu.tools.SyncBookmarks.Operation.Operation;

/**
 * 各オペレーションのための共通インタフェースを提供するサーブレット
 * @author aharisu
 *
 */
@SuppressWarnings("serial")
public class OperationServlet extends HttpServlet{
	private static Operation[] operations = new Operation[] {
		new ListOperation(),
		new NewerOperation(),
	};
	
	@Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = resp.getWriter();
		
		String[] paths = req.getRequestURI().split("/");
		if(paths.length != 3) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String op = paths[2].toLowerCase();
		
		
		boolean handled = false;
		Object content = null;
		for(Operation handler : operations) {
			if(handler.getOperationName().equals(op)) {
				content = handler.getContent(req);
				handled = true;
				break;
			}
		}
		
		if(!handled) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		HashMap<String, Object> contentMap = null;
		if(content != null) {
			contentMap = new HashMap<String, Object>();
			contentMap.put("content", content);
		}
		
		writer.println(Responder.genResponse(Responder.ResponseStauts.Success, contentMap));
	}

}
