package aharisu.tools.SyncBookmarks;

import aharisu.tools.SyncBookmarks.Data.User;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * アプリケーションに関する設定の保存と取得を行うクラス
 * @author aharisu
 *
 */
public final class Preferences {
	private static final String PreferenceName = "SyncBookmarksPrefs";
	
	private static Context _Context;
	
	public static void setContext(Context context) {
		_Context = context;
	}
	
	private static final String PrefUserName = "user_name";
	private static final String PrefUserPassowrd = "user_password";
	
	public static User getUser() {
		SharedPreferences pref = _Context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
		
		String name = pref.getString(PrefUserName, null);
		String password = pref.getString(PrefUserPassowrd, null);
		if(name == null || password == null) {
			return null;
		} else {
			return new User(name, password);
		}
	}
	
	public static void storeUser(User user) {
		if(user == null)
			throw new NullPointerException("user");
		
		SharedPreferences pref = _Context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(PrefUserName, user.name);
		editor.putString(PrefUserPassowrd, user.password);
		editor.commit();
	}
	
	public static void removeUser() {
		SharedPreferences pref = _Context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(PrefUserName);
		editor.remove(PrefUserPassowrd);
		editor.commit();
	}

}
