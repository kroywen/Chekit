package ca.chekit.android.screen;

import org.apache.http.HttpStatus;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.storage.Settings;

public class ChangePasswordScreen extends BaseScreen {
	
	private EditText oldPassView;
	private EditText newPassView;
	private EditText verifyNewPassView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_layout);
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_menu_settings);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void initializeViews() {
		oldPassView = (EditText) findViewById(R.id.oldPassView);
		newPassView = (EditText) findViewById(R.id.newPassView);
		verifyNewPassView = (EditText) findViewById(R.id.verifyNewPassView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.change_password_screen_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
	    case R.id.action_save:
	    	save();
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void save() {
		hideSoftKeyborad();
		
		String oldPass = getText(oldPassView);
		String newPass = getText(newPassView);
		String verifyNewPass = getText(verifyNewPassView);
		
		if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(verifyNewPass)) {
			showInfoDialog(R.string.information, R.string.please_fill_all_fields);
			return;
		}
		
		if (!newPass.equalsIgnoreCase(verifyNewPass)) {
			showInfoDialog(R.string.error, R.string.passwords_do_not_match);
			return;
		}
		
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_CHANGE_PASSWORD);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
		intent.putExtra(ApiData.PARAM_OLD_PASSWORD, oldPass);
		intent.putExtra(ApiData.PARAM_NEW_PASSWORD, newPass);
		startService(intent);
		showProgressDialog(R.string.changing_password);
	}
	
	private String getText(EditText view) {
		return view.getText().toString().trim();
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_CHANGE_PASSWORD.equalsIgnoreCase(command)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						String username = settings.getString(Settings.USERNAME);
						String password = getText(newPassView);
						settings.setString(Settings.PASSWORD, password);
						String data = username + ":" + password;
						String auth = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
						settings.setString(Settings.AUTH, auth);
						
						showInfoDialog(R.string.information, R.string.password_changed, new OnClickListener() {
							@Override
							public void onClick(View v) {
								finish();
							}
						});
					} else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
						showInfoDialog(R.string.error, R.string.old_password_is_wrong);
					}
				}
			}
		}
	}

}
