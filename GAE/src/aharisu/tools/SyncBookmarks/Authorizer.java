package aharisu.tools.SyncBookmarks;

/**
 * サーブレットにデータを保存するための認証を行うクラス
 * @author aharisu
 *
 */
public class Authorizer {
	private static final String AppToken = "";
	
	private Authorizer(){
	}
	
	public static final boolean AuthorizeAppToken(String token) {
		return token.equals(AppToken);
	}
	
}
