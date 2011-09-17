package aharisu.tools.SyncBookmarks;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import aharisu.tooks.SyncBookmarks.R;
import aharisu.tools.SyncBookmarks.Data.User;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ユーザ認証やユーザ設定を行うアクティビティ
 * @author aharisu
 *
 */
public class UserAuthAndRegisterActivity extends Activity{
	
	boolean _isUserAuthMode;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_and_register);
		
		User user = Preferences.getUser();
		if(user != null) {
			((EditText)findViewById(R.id_settings.name)).setText(user.name);
		}
		
		
		_isUserAuthMode = true;
		setAuthAndRegister(_isUserAuthMode);
		findViewById(R.id_settings.change_view).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				_isUserAuthMode = !_isUserAuthMode;
				setAuthAndRegister(_isUserAuthMode);
			}
		});
		
		
		findViewById(R.id_settings.ok).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				okOnClick();
			}
		});
	}
	
	
	private void okOnClick() {
		final String name = ((TextView)findViewById(R.id_settings.name)).getText().toString();
		final String password = ((TextView)findViewById(R.id_settings.password)).getText().toString();
		
		try {
			User user = new User(name, password);
			String res = ServerCommunicator.sendUserAuthOrRegister(user, _isUserAuthMode);
			if(res != null) {
				JSONObject obj = new JSONObject(res);
				String status = obj.getString("status");
				if(status != null && status.equals("success")) {
					successUserAuthOrRegister(user);
				} else {
					failureResonAuthOrRegister(obj);
				}
			}
		} catch(IOException e) {
			failureResonIOException(e);
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void successUserAuthOrRegister(User user) {
		Preferences.storeUser(user);
		
		Toast.makeText(this,
				_isUserAuthMode ? R.string.settings_success_user_auth : R.string.settings_success_user_register,
				Toast.LENGTH_SHORT).show();
		
		setResult(Activity.RESULT_OK);
		finish();
	}
	
	private void failureResonIOException(IOException e) {
		e.printStackTrace();
		
		Toast.makeText(this,
				R.string.settings_failure_reson_io,
				Toast.LENGTH_LONG).show();
	}
	
	private void failureResonAuthOrRegister(JSONObject obj) {
		Toast.makeText(this,
				_isUserAuthMode ? R.string.settings_failure_reson_auth : R.string.settings_failure_reson_register,
				Toast.LENGTH_SHORT).show();
	}
	
	private void setAuthAndRegister(boolean isUserAuthMode) {
		if(isUserAuthMode) {
			((Button)findViewById(R.id_settings.ok)).setText(R.string.settings_auth);
			((TextView)findViewById(R.id_settings.change_view)).setText(R.string.settings_to_register);
		} else {
			((Button)findViewById(R.id_settings.ok)).setText(R.string.settings_regster);
			((TextView)findViewById(R.id_settings.change_view)).setText(R.string.settings_to_auth);
		}
	}

}
