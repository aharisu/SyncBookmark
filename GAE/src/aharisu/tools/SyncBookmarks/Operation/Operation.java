package aharisu.tools.SyncBookmarks.Operation;

import javax.servlet.http.HttpServletRequest;

/**
 * 各オペレーションのためのインタフェース
 * @author aharisu
 *
 */
public interface Operation {
	String getOperationName();
	Object getContent(HttpServletRequest req);
}
