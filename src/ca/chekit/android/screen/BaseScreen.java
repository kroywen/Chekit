package ca.chekit.android.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiResponseReceiver;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.api.OnApiResponseListener;
import ca.chekit.android.dialog.InfoDialog;
import ca.chekit.android.storage.Settings;

public class BaseScreen extends FragmentActivity implements OnApiResponseListener {
	
	protected ApiResponseReceiver responseReceiver;
	protected ProgressDialog progressDialog;
	protected Settings settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = new Settings(this);
	}
	
	protected void showProgressDialog(int messageResId) {
		showProgressDialog(getString(messageResId));
	}
	
	protected void showProgressDialog(String message) {
		if (progressDialog == null) {
			 progressDialog = new ProgressDialog(BaseScreen.this);
			 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 }
		 progressDialog.setMessage(message);
		 if (!progressDialog.isShowing()) {
			 progressDialog.show();
		 }
	}
	
	protected void hideProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing())
			 progressDialog.dismiss();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter(ApiService.ACTION_API_RESULT);
		responseReceiver = new ApiResponseReceiver(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(
			responseReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
	}
	
	public void showConnectionErrorDialog() {
		showInfoDialog(R.string.information, R.string.no_connection);
	}
	
	protected void showInfoDialog(int titleResId, int messageResId) {
		showInfoDialog(getString(titleResId), getString(messageResId));
	}
	
	protected void showInfoDialog(String title, String message) {
		InfoDialog dialog = new InfoDialog();
		dialog.setTitle(title);
		dialog.setText(message);
		dialog.show(getFragmentManager(), "InfoDialog");
	}

	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {}
	
	public void hideSoftKeyborad() {
		View view = getCurrentFocus();
		if (view == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

}
