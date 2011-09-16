package aharisu.tools.SyncBookmarks;

import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

/**
 * JSONのレスポンスデータを生成するクラス
 * @author aharisu
 *
 */
public class Responder {
	public enum ResponseStauts {
		Success,
		IllegalDataFormat,
		TokenUnauthorize;
		
		public String getStatusString() {
			return this == ResponseStauts.Success ? "success" : "failure";
		}
		
		public String getFailureMessage() {
			if(this == ResponseStauts.TokenUnauthorize) {
				return "illegal application token";
			} else if(this == ResponseStauts.IllegalDataFormat) {
				return "illegal data format";
			} else {
				return "unkown";
			}
		}
		
	};
	
	private Responder() {
	}
	
	public static String genResponse(ResponseStauts status) {
		return genResponse(status, null);
	}
	
	public static String genResponse(ResponseStauts status, Map< String, Object> content) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("status", status.getStatusString());
		
		if(status != ResponseStauts.Success) {
			map.put("reason", status.getFailureMessage());
		}
		
		if(content != null) {
			map.putAll(content);
		}
		
		return JSON.encode(map);
	}

}
