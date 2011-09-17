package aharisu.tools.SyncBookmarks.Data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.ShortBlob;

/**
 * ユーザデータを表すクラス
 * @author aharisu
 *
 */
@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class User {
	
	private static final String CryptionKey = "";
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String name;
	
	@Persistent
	private ShortBlob password;
	
	public User(String name, String password){
		this.name = name;
		this.password = new ShortBlob(Cryption.encrypt(CryptionKey, password));
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return Cryption.decrypt(CryptionKey, password.getBytes());
	}
	
}
