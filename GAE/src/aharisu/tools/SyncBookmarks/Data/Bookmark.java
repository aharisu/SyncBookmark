package aharisu.tools.SyncBookmarks.Data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.arnx.jsonic.JSONHint;

/**
 * ブックマークデータを表すクラス
 * @author aharisu
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Bookmark {
	
	@SuppressWarnings("unused") 
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String URL;
	
	@Persistent
	private String Title;
	
	@Persistent
	private long IconId;
	
	@Persistent
	private boolean IsChecked;
	
	public Bookmark(String url, String title, long iconId) {
		this.URL = url;
		this.Title = title;
		this.IconId = iconId;
		this.IsChecked = false;
	}
	
	public String getURL() {
		return this.URL;
	}
	
	public String getTitle() {
		return this.Title;
	}
	
	public long getIconId() {
		return this.IconId;
	}
	
	@JSONHint(ignore = true)
	public boolean getIsChecked() {
		return this.IsChecked;
	}
	
	public void setIsChecked(boolean isChecked) {
		this.IsChecked = isChecked;
	}
	
}
