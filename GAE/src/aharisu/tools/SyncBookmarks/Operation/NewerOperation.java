package aharisu.tools.SyncBookmarks.Operation;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import aharisu.tools.SyncBookmarks.Data.Bookmark;
import aharisu.tools.SyncBookmarks.Data.PMF;

/**
 * まだ取得されてない新規追加のブックマークを返すオペレーション
 * @author aharisu
 *
 */
public class NewerOperation implements Operation{
	
	@Override public String getOperationName() {
		return "newer";
	}
	
	@SuppressWarnings("unchecked") 
	@Override public Object getContent(HttpServletRequest req) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(Bookmark.class);
			//まだチェックされていないデータだけを取得
			query.setFilter("IsChecked == false");
			List<Bookmark> bookmarks = (List<Bookmark>)query.execute();
			
			//一度この操作で読み込んだデータはチェックず見マークをする
			for(Bookmark b : bookmarks) {
				b.setIsChecked(true);
			}
			
			return new ArrayList<Bookmark>(bookmarks);
		} finally {
			pm.close();
		}
	}

}
