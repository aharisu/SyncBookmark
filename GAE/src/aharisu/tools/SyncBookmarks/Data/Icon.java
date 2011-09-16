package aharisu.tools.SyncBookmarks.Data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

/**
 * アイコンデータを表すクラス
 * @author aharisu
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Icon {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private Blob IconBlob;
	
	public Icon(Blob iconBlob) {
		this.IconBlob = iconBlob;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public Blob getIconBlob() {
		return this.IconBlob;
	}

}
