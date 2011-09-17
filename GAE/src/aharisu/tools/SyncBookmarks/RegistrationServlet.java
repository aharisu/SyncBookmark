package aharisu.tools.SyncBookmarks;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aharisu.tools.SyncBookmarks.Data.User;
import aharisu.tools.SyncBookmarks.Data.UserStore;

/**
 * ユーザの認証と登録を行うサーブレット
 * @author aharisu
 *
 */
@SuppressWarnings("serial")
public class RegistrationServlet extends HttpServlet{
	
	@Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = resp.getWriter();
		
		String name =req.getParameter("name");
		String passwd = req.getParameter("password");
		if(name == null || passwd == null) {
			writer.println(Responder.genResponse(Responder.ResponseStauts.IllegalParameter));
			return;
		}
		
		String isAuth = req.getParameter("auth");
		if(isAuth != null && isAuth.equals("true")) {
			//ユーザ名とパスワードがあっているか確かめる
			if(!UserStore.exists(name, passwd)) {
				writer.println(Responder.genResponse(Responder.ResponseStauts.AuthFailure));
				return;
			}
		} else {
			//ユーザ名とパスワードを登録する
			User user = new User(name, passwd);
			if(!UserStore.storeNewUser(user)) {
				writer.println(Responder.genResponse(Responder.ResponseStauts.NotUniqueName));
				return;
			}
		}
		
		writer.println(Responder.genResponse(Responder.ResponseStauts.Success));
	}

}
