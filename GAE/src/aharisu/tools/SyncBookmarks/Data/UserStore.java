package aharisu.tools.SyncBookmarks.Data;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * ユーザデータが一意であるようにするためのアクセサクラス
 * @author aharisu
 *
 */
public class UserStore {
	
	public UserStore() {}
	
	public static boolean storeNewUser(User user) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		//ユーザの名前が既に登録済みかチェック
		if(isUniqueUserName(user, pm)) {
			//trueが返った場合はすでに登録済み
			return false;
		}
		
		//ユーザを登録
		User storedUser = pm.makePersistent(user);
		
		//二つほぼ同時の登録がないかチェック
		if(!isUniqueUserName(storedUser, pm)) {
			//ユーザ名がユニークではなかったので削除
			pm.deletePersistent(storedUser);
			return false;
		}
		
		try {
		} finally {
			pm.close();
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked") 
	private static boolean isUniqueUserName(User user, PersistenceManager pm) {
		 Query query = pm.newQuery(User.class);
		 query.setFilter("name == \"" + user.getName() + "\"");
		 
		 return ((List<User>)query.execute()).size() == 1;
	}
	
	@SuppressWarnings("unchecked") 
	public static boolean exists(String name, String password) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			 Query query = pm.newQuery(User.class);
			 query.setFilter("name == \"" + name + "\"");
			 
			 List<User> users = (List<User>)query.execute();
			 if(users != null && users.size() > 0) {
				 return users.get(0).getPassword().equals(password);
			 } else {
				 return false;
			 }
		} finally {
			pm.close();
		}
	}

}
