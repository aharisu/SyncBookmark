package aharisu.tools.SyncBookmarks.Operation;

import java.util.ArrayList;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;

import aharisu.tools.SyncBookmarks.Data.Bookmark;
import aharisu.tools.SyncBookmarks.Data.PMF;

/**
 * 全てのブックマークを返すオペレーション
 * @author aharisu
 *
 */
public class ListOperation implements Operation{
	
	@Override public String getOperationName() {
		return "list";
	}
	
	@Override public Object getContent(HttpServletRequest req) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Extent<Bookmark> extent = pm.getExtent(Bookmark.class, false);
			ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
			for(Bookmark e : extent) {
				bookmarks.add(e);
			}
			extent.closeAll();
			
			return bookmarks;
		} finally {
			pm.close();
		}
	}

}
