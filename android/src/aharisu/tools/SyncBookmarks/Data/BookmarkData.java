package aharisu.tools.SyncBookmarks.Data;

import android.graphics.Bitmap;

/**
 * ブックマークデータを表すクラス
 * @author aharisu
 *
 */
public final class BookmarkData {
	public final String URL;
	public final Bitmap Icon;
	public final String Title;
	
	public BookmarkData(String url, Bitmap icon, String title) {
		this.URL = url;
		this.Icon = icon;
		this.Title = title;
	}
}
