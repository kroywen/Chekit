package ca.chekit.android.screen;

import java.util.List;

import org.apache.http.HttpStatus;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.dialog.ConfirmationDialog;
import ca.chekit.android.fragment.AcceptedFragment;
import ca.chekit.android.fragment.IssuedFragment;
import ca.chekit.android.fragment.PassedFragment;
import ca.chekit.android.fragment.WorkTasksFragment;
import ca.chekit.android.model.ScheduledStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

public class WorkTasksScreen extends BaseScreen {
	
	public static final int AUTH_REQUEST_CODE = 0;
	
	private List<WorkTask> worktasks;
	private WorkTasksFragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_tasks_screen);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.work_tasks);
		actionBar.setIcon(R.drawable.work_tasks_icon);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				selectItem(tab.getPosition());
			}
			@Override
	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
			@Override
	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
	    };
	    
	    actionBar.addTab(actionBar.newTab().setText(R.string.issued).setTabListener(tabListener), true);
	    actionBar.addTab(actionBar.newTab().setText(R.string.accepted).setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab().setText(R.string.passed).setTabListener(tabListener));
	    
	    String authKey = settings.getString(Settings.AUTH);
	    if (!TextUtils.isEmpty(authKey)) {
	    	refreshWorkTasks(true);
	    } else {
	    	Intent intent = new Intent(this, LoginScreen.class);
	    	startActivityForResult(intent, AUTH_REQUEST_CODE);
	    }
	}
	
	private void selectItem(int position) {
		fragment = getWorkTasksFragment(position);
		FragmentTransaction t = getFragmentManager().beginTransaction();
		t.replace(R.id.content, fragment).commit();
	}
	
	private WorkTasksFragment getWorkTasksFragment(int i) {
		return (i == 0) ? new IssuedFragment() : 
    		(i == 1) ? new AcceptedFragment() : new PassedFragment();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.work_tasks_screen_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_logout:
	        	if (Utilities.isConnectionAvailable(this)) {
	        		showLogoutDialog();
	        	} else {
	        		showConnectionErrorDialog();
	        	}
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showLogoutDialog() {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.confirm_logout));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
				dialog.dismiss();
			}
		});
		dialog.show(getFragmentManager(), "LogOutDialog");
	}
	
	private void logout() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_LOGIN);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_DELETE);
		startService(intent);
		showProgressDialog(R.string.logging_out);
	}
	
	public void refreshWorkTasks(boolean showProgress) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASKS);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			startService(intent);
			if (showProgress) {
				showProgressDialog(R.string.loading_tasks);
			}
		} else {
			showConnectionErrorDialog();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_LOGIN.equalsIgnoreCase(command) && ApiData.METHOD_DELETE.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						settings.setString(Settings.AUTH, null);
						Intent intent = new Intent(this, LoginScreen.class);
						startActivityForResult(intent, AUTH_REQUEST_CODE);
					}
				} else if (ApiData.COMMAND_WORKTASKS.equalsIgnoreCase(command) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK) {
						worktasks = (List<WorkTask>) apiResponse.getData();
						if (fragment != null) {
							fragment.updateViews();
						}
					}
				} else if (ApiData.COMMAND_WORKTASK_SCHEDULED_STATUS.equalsIgnoreCase(command) && ApiData.METHOD_PUT.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						refreshWorkTasks(true);
					}
				}
			}
		}
	}
	
	public List<WorkTask> getWorktasks() {
		return worktasks;
	}
	
	public void showAcceptTaskDialog(final WorkTask task) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.accept_task));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				acceptTask(task);
				dialog.dismiss();
			}
		});
		dialog.show(getFragmentManager(), "AcceptTaskDialog");
	}
	
	private void acceptTask(WorkTask task) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASK_SCHEDULED_STATUS);
			intent.putExtra(ApiData.PARAM_ID, task.getId());
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
			intent.putExtra(ApiData.PARAM_BODY, "\"" + ScheduledStatus.Accepted.name() + "\"");
			startService(intent);
			showProgressDialog(R.string.accepting_task);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public void showPassTaskDialog(final WorkTask task) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.pass_task));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				passTask(task);
				dialog.dismiss();
			}
		});
		dialog.show(getFragmentManager(), "PassTaskDialog");
	}
	
	private void passTask(WorkTask task) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASK_SCHEDULED_STATUS);
			intent.putExtra(ApiData.PARAM_ID, task.getId());
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
			intent.putExtra(ApiData.PARAM_BODY, "\"" + ScheduledStatus.Rejected.name() + "\"");
			startService(intent);
			showProgressDialog(R.string.passing_task);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public void showUnpassTaskDialog(final WorkTask task) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.unpass_task));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				unpassTask(task);
				dialog.dismiss();
			}
		});
		dialog.show(getFragmentManager(), "UnpassTaskDialog");
	}
	
	private void unpassTask(WorkTask task) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASK_SCHEDULED_STATUS);
			intent.putExtra(ApiData.PARAM_ID, task.getId());
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
			intent.putExtra(ApiData.PARAM_BODY, "\"" + ScheduledStatus.Assigned.name() + "\"");
			startService(intent);
			showProgressDialog(R.string.unpassing_task);
		} else {
			showConnectionErrorDialog();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AUTH_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				refreshWorkTasks(true);
			} else {
				finish();
			}
		}
	}

}
