package ca.chekit.android.screen;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiResponseReceiver;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.api.OnApiResponseListener;
import ca.chekit.android.dialog.ConfirmationDialog;
import ca.chekit.android.dialog.InfoDialog;
import ca.chekit.android.model.Account;
import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.model.WorkStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

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
			 try {
				 progressDialog.show();
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
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
	
	public void showInfoDialog(int titleResId, int messageResId) {
		showInfoDialog(getString(titleResId), getString(messageResId), null);
	}
	
	protected void showInfoDialog(int titleResId, int messageResId, OnClickListener okListener) {
		showInfoDialog(getString(titleResId), getString(messageResId), okListener);
	}
	
	protected void showInfoDialog(String title, String message, OnClickListener okListener) {
		InfoDialog dialog = new InfoDialog();
		dialog.setTitle(title);
		dialog.setText(message);
		dialog.setOkListener(okListener);
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
	
	public void showChangeTaskStatusDialog(final Intent data) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.update_status, Utilities.addSpaces(data.getStringExtra("StatusName"))));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (Utilities.isConnectionAvailable(BaseScreen.this)) {
					updateWorkTaskStatus(data);
				} else {
					showConnectionErrorDialog();
				}
				
			}
		});
		dialog.show(getFragmentManager(), "UpdateStatusDialog");
	}
	
	public void updateWorkTaskStatus(WorkTask task) {
		if (task == null) {
			return;
		}
		Intent data = new Intent();
		data.putExtra("StatusName", task.getWorkStatus().getName());
		data.putExtra(WorkTask.ID, task.getId());
		data.putExtra(WorkTask.WORK_STATUS, task.getWorkStatus().getPostName());
		data.putExtra(WorkTask.REMAINING, task.getRemaining());
		data.putExtra(WorkTask.DURATION, task.getDuration());
		data.putExtra(WorkTask.WORK_STATUS_CHANGED, task.getWorkStatusChanged());
		updateWorkTaskStatus(data);
	}
	
	public void updateWorkTaskStatus(Intent data) {
		try {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASK_STATUS);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
			intent.putExtra(ApiData.PARAM_ID, data.getLongExtra(WorkTask.ID, 0));
			
			JSONObject obj = new JSONObject();
			obj.put(WorkTask.WORK_STATUS, data.getStringExtra(WorkTask.WORK_STATUS));
			
			String statusName = data.getStringExtra("StatusName");
			String prevStatus = data.getStringExtra(WorkTask.PREVIOUS_WORK_STATUS);
			long remaining = data.getLongExtra(WorkTask.REMAINING, 0);
			if (WorkStatus.NEW.equalsIgnoreCase(prevStatus)) {
				remaining = data.getLongExtra(WorkTask.DURATION, 0);
			} else if (WorkStatus.TASK_COMPLETED.equalsIgnoreCase(statusName) ||
					WorkStatus.LEAVE_SITE.equalsIgnoreCase(statusName) ||
					WorkStatus.DELIVERY_INCOMPLETE.equalsIgnoreCase(statusName) ||
					WorkStatus.TASK_ISSUES_IRRESOLVABLE.equalsIgnoreCase(statusName))
			{
				remaining = data.getLongExtra(WorkTask.REMAINING, 0);
			} else {
				long statusChanged = Utilities.parseDate(data.getStringExtra(WorkTask.WORK_STATUS_CHANGED), Utilities.yyyy_MM_ddTHH_mm_ss);
				remaining = Utilities.calculateRemainingTime(remaining, statusChanged);
			}
			obj.put(WorkTask.REMAINING, remaining);
			
			if (data.hasExtra(WorkTask.LATITUDE) && data.hasExtra(WorkTask.LONGITUDE)) {
				obj.put(WorkTask.LATITUDE, data.getDoubleExtra(WorkTask.LATITUDE, 0));
				obj.put(WorkTask.LONGITUDE, data.getDoubleExtra(WorkTask.LONGITUDE, 0));
			}
			Log.d("UpdateStatus", obj.toString());
			
			intent.putExtra(ApiData.PARAM_BODY, obj.toString());
			startService(intent);
			showProgressDialog(R.string.updating_work_status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadContacts(boolean showDialog) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_CONTACTS);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			startService(intent);
			if (showDialog) {
				showProgressDialog(R.string.loading_contacts);
			}
		} else {
			if (showDialog) {
				showConnectionErrorDialog();	
			}
		}
	}
	
	@SuppressLint("DefaultLocale")
	public void loadChat(long lastUpdateTime, boolean showDialog) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_CHAT);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			
			long loggedId = Account.getCurrent(this).getId();
			String filter = String.format("InsertDate ge DateTime'%s' and (FromContactId eq %d or ToContactId eq %d)", 
				Utilities.parseTime(lastUpdateTime, Utilities.yyyy_MM_ddTHH_mm_ss),
				loggedId,
				loggedId
			);
			
			intent.putExtra(ApiData.PARAM_FILTER, filter);
			startService(intent);
			if (showDialog) {
				showProgressDialog(R.string.loading_messages);
			}
		} else {
			if (showDialog) {
				showConnectionErrorDialog();
			}
		}
	}
	
	public void sendMessage(long fromId, long toId, String message) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_CHAT);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_POST);
			
			try {
				JSONObject obj = new JSONObject();
				obj.put(ChatMessage.FROM_CONTACT_ID, fromId);
				obj.put(ChatMessage.TO_CONTACT_ID, toId);
				obj.put(ChatMessage.MESSAGE, message);
				obj.put(ChatMessage.SENT_DATE, Utilities.parseTime(System.currentTimeMillis(), Utilities.yyyy_MM_ddTHH_mm_ss));
				
				intent.putExtra(ApiData.PARAM_BODY, obj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			startService(intent);
			showProgressDialog(R.string.sending_message);
		}
	}
	
	public void refreshWorkTasks(boolean showProgress) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			String role = settings.getString(Settings.ROLE);
			if (role.equalsIgnoreCase(Account.ASSIGNEE)) {
				intent.setAction(ApiData.COMMAND_CONTACT_WORKTASKS);
				intent.putExtra(ApiData.PARAM_ID, Account.getCurrent(this).getId());
			} else {
				intent.setAction(ApiData.COMMAND_WORKTASKS);
			}
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			startService(intent);
			if (showProgress) {
				showProgressDialog(R.string.loading_tasks);
			}
		} else {
			showConnectionErrorDialog();
		}
	}

}
