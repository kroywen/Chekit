package ca.chekit.android.screen;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.dialog.InputDialog;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

public class LoginScreen extends BaseScreen implements OnClickListener {
	
	private EditText usernameView;
	private EditText passwordView;
	private View loginBtn;
	private View forgetPassBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		initializeViews();
	}
	
	private void initializeViews() {
		usernameView = (EditText) findViewById(R.id.usernameView);
		passwordView = (EditText) findViewById(R.id.passwordView);
		
		loginBtn = findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		
		forgetPassBtn = findViewById(R.id.forgetPassBtn);
		forgetPassBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			if (Utilities.isConnectionAvailable(this)) {
				hideSoftKeyborad();
				login();
			} else {
				showConnectionErrorDialog();
			}
			break;
		case R.id.forgetPassBtn:
			showForgetPasswordDialog();
			break;
		}
	}

	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				
				if (ApiData.COMMAND_LOGIN.equalsIgnoreCase(command) &&
					ApiData.METHOD_POST.equalsIgnoreCase(method))
				{
					if (statusCode == HttpStatus.SC_OK) {
						String sessionId = (String) apiResponse.getData();
						if (!TextUtils.isEmpty(sessionId)) {
							if (sessionId.startsWith("\"")) {
								sessionId = sessionId.substring(1);
							}
							if (sessionId.endsWith("\"")) {
								sessionId = sessionId.substring(0, sessionId.length()-1);
							}
							settings.setString(Settings.SESSION_ID, sessionId);
						}
						String username = usernameView.getText().toString().trim();
						String password = passwordView.getText().toString().trim();
						settings.setString(Settings.USERNAME, username);
						settings.setString(Settings.PASSWORD, password);
						
						String data = username + ":" + password;
						String auth = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
						settings.setString(Settings.AUTH, auth);
						
						setResult(RESULT_OK);
						finish();
					} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
						showInfoDialog(R.string.error, R.string.incorrect_user_credentials);
					}
				} else if (ApiData.COMMAND_PASSWORD_RECOVERY.equalsIgnoreCase(command)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						showInfoDialog(R.string.notice, R.string.password_recovery_sent);
					} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
						showInfoDialog(R.string.notice, R.string.no_user_in_database);
					} else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
						showInfoDialog(R.string.error, R.string.internal_server_error);
					}
				}
			}
		}
	}
	
	private void login() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_LOGIN);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_POST);
		intent.putExtra(ApiData.PARAM_USERNAME, usernameView.getText().toString().trim());
		intent.putExtra(ApiData.PARAM_PASSWORD, passwordView.getText().toString().trim());
		startService(intent);
		showProgressDialog(R.string.logging_in);
	}
	
	private void showForgetPasswordDialog() {
		InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.password_reset_title));
		dialog.setText(getString(R.string.password_reset_text));
		dialog.setHint(getString(R.string.email));
		dialog.setButtons(getString(R.string.reset), getString(R.string.cancel), new InputDialog.OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				hideSoftKeyborad();
				if (Utilities.isConnectionAvailable(LoginScreen.this)) {
					requestPasswordRecovery(inputText);
				} else {
					showConnectionErrorDialog();
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getFragmentManager(), "PasswordRecoveryDialog");
	}
	
	private void requestPasswordRecovery(String email) {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_PASSWORD_RECOVERY);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_POST);
		
		String body = "\"" + email + "\"";
		intent.putExtra(ApiData.PARAM_BODY, body);
		startService(intent);
		showProgressDialog(R.string.sending_email);
	}
}
