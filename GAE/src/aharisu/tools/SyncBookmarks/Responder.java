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
		NotUniqueName,
		AuthFailure,
		IllegalParameter;
		
		public String getStatusString() {
			if(this == ResponseStauts.Success) {
				return "success";
			} else if(this == ResponseStauts.NotUniqueName) {
				return "already_used";
			} else {
				return "failure";
			}
		}
		
		public String getFailureMessage() {
			if(this == ResponseStauts.IllegalParameter) {
				return "get parameter";
			} else if(this == ResponseStauts.IllegalDataFormat) {
				return "illegal data format";
			} else if(this == ResponseStauts.NotUniqueName) {
				return "name is already used";
			} else if(this == ResponseStauts.AuthFailure) {
				return "authentication failure";
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
