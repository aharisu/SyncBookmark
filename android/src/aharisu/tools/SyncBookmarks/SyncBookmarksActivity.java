package aharisu.tools.SyncBookmarks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import aharisu.tooks.SyncBookmarks.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.Browser.BookmarkColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * アプリケーションのメインになるアクティビティ
 * @author aharisu
 *
 */
public class SyncBookmarksActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.syncbookmarks);
        
        GridView grid = (GridView)findViewById(R.id_syncbookmarks.layout);
        grid.setNumColumns(3);
        grid.setAdapter(createBookmarkListAdapter(getBookmarkData()));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        		onBookmarkItemClick(parent, pos);
        	}
		});
        
    }
    
    List<BookmarkData> getBookmarkData() {
        final String[] projection = new String[]{
        		BookmarkColumns.BOOKMARK,
				BookmarkColumns.TITLE,
				BookmarkColumns.URL,
				"thumbnail",
        	};
        
    	ArrayList<BookmarkData> list = new ArrayList<BookmarkData>();
    	
        Cursor c = managedQuery(Browser.BOOKMARKS_URI, null, 
        		projection[0] + " == 1", null, null);
        if(c.moveToFirst()) {
        	do {
        		byte[] iconBlob = c.getBlob(c.getColumnIndex("thumbnail"));
        		
        		list.add(new BookmarkData(
        				c.getString(c.getColumnIndex(Browser.BookmarkColumns.URL)),
        				iconBlob != null ?
        						BitmapFactory.decodeByteArray(iconBlob, 0, iconBlob.length) :
        						BitmapFactory.decodeResource(getResources(), R.drawable.icon),
        				c.getString(c.getColumnIndex(Browser.BookmarkColumns.TITLE))));
        	}while(c.moveToNext());
        }
        c.close();
        
        //reverse
        ArrayList<BookmarkData> reverse = new ArrayList<BookmarkData>(list.size());
        for(int i = list.size() - 1;i >= 0;--i) {
        	reverse.add(list.get(i));
        }
        
        return reverse;
    }
    
    BaseAdapter createBookmarkListAdapter(final List<BookmarkData> bookmarkList) {
    	return new BaseAdapter() {
			
			@Override public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					Context context = SyncBookmarksActivity.this;
					
					convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.bookmark_item, null);
				}
				
				((ImageView)convertView.findViewById(R.id_bookmarkitem.thumbnail)).setImageBitmap(bookmarkList.get(position).Icon);
				((TextView)convertView.findViewById(R.id_bookmarkitem.title)).setText(bookmarkList.get(position).Title);
				
				return convertView;
			}
			
			@Override public long getItemId(int position) {
				return position;
			}
			
			@Override public Object getItem(int position) {
				return bookmarkList.get(position);
			}
			
			@Override public int getCount() {
				return bookmarkList.size();
			}
		};
    }
    
    private void onBookmarkItemClick(AdapterView<?> parent, int pos) {
    	final Context context = this;
    	final BookmarkData bookmark = (BookmarkData)parent.getItemAtPosition(pos);
    	
    	View layout = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
    		.inflate(R.layout.bookmark_dialog, null);
    	
		((ImageView)layout.findViewById(R.id_bookmarkdialog.thumbnail)).setImageBitmap(bookmark.Icon);
		((TextView)layout.findViewById(R.id_bookmarkdialog.title)).setText(bookmark.Title);
		((TextView)layout.findViewById(R.id_bookmarkdialog.url)).setText(bookmark.URL);
    	
    	new AlertDialog.Builder(context)
    		.setTitle(R.string.send_bookmak)
    		.setView(layout)
    		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					sendToSernver(bookmark);
				}
			})
    		.setNegativeButton(R.string.cansel, null)
    		.show();
    }
    
    private void sendToSernver(BookmarkData bookmark) {
    	boolean success = false;
    	try {
    		String res = ServerCommunicator.sendBookmarkToServer(bookmark);
    		if(res != null) {
    			JSONObject obj = new JSONObject(res);
    			String status = obj.getString("status");
    			if(status != null && status.equals("success")) {
    				success = true;
    			}
    		}
    	} catch(IOException e) {
    		e.printStackTrace();
    	} catch(JSONException e) {
    		e.printStackTrace();
    	}
    	
    	Toast.makeText(this, 
    			success ? R.string.send_success : R.string.send_failure,
    			success ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }
    
}