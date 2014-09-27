package ca.chekit.android.screen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.dialog.ConfirmationDialog;
import ca.chekit.android.fragment.AcceptedFragment;
import ca.chekit.android.fragment.IssuedFragment;
import ca.chekit.android.fragment.PassedFragment;
import ca.chekit.android.fragment.WorkTasksFragment;
import ca.chekit.android.model.Account;
import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.model.Division;
import ca.chekit.android.model.ScheduledStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.storage.ChatStorage;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class WorkTasksScreen extends BaseScreen {
	
	public static final int AUTH_REQUEST_CODE = 0;
	
	private String SENDER_ID = "210238643582";
	private static String CONNECTION_STRING = "Endpoint=sb://chekithubpush-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=oMty3isKiukkBtOJ323T2AO7T1k5/h1ljn2YkLEetx8=";
	private GoogleCloudMessaging gcm;
	private NotificationHub hub;
	
	private List<WorkTask> worktasks;
	private WorkTasksFragment fragment;
	private ChatStorage chatStorage;
	
	private TextView unreadCountView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_tasks_screen);
		
		chatStorage = ChatStorage.newInstance(this);
		
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
	    
	    actionBar.setSelectedNavigationItem(1);
	    
	    String authKey = settings.getString(Settings.AUTH);
	    if (!TextUtils.isEmpty(authKey)) {
	    	requestDivisionList();
	    } else {
	    	Intent intent = new Intent(this, LoginScreen.class);
	    	startActivityForResult(intent, AUTH_REQUEST_CODE);
	    }
	    
		registerWithNotificationHubs();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerWithNotificationHubs() {
		NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
		gcm = GoogleCloudMessaging.getInstance(this);
		hub = new NotificationHub("chekithubpush", CONNECTION_STRING, this);
		
		new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				try {
					String regid = gcm.register(SENDER_ID);
					hub.register(regid);
				} catch (final Exception e) {
					e.printStackTrace();
					return e;
				}
				return null;
			}
		}.execute(null, null, null);
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
	    
	    FrameLayout badgeLayout = (FrameLayout) menu.findItem(R.id.action_chat).getActionView();
	    badgeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WorkTasksScreen.this, ChatScreen.class);
	        	startActivity(intent);
			}
		});
	    
		unreadCountView = (TextView) badgeLayout.findViewById(R.id.unreadCountView);
		int unread = chatStorage.getUnreadMessagesCount(this);
		if (unread == 0) {
			unreadCountView.setVisibility(View.GONE);
		} else {
			unreadCountView.setText(String.valueOf(unread));
			unreadCountView.setVisibility(View.VISIBLE);
		}
		
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
	        case R.id.action_settings:
	        	Intent intent = new Intent(this, SettingsScreen.class);
	        	startActivity(intent);
	        	return true;
	        case R.id.action_map:
	        	intent = new Intent(this, LocationScreen.class);
	        	startActivity(intent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();
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
	
	private void requestDivisionList() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_DIVISIONS);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
		intent.putExtra(ApiData.PARAM_ID, Account.getCurrent(this).getId());
		startService(intent);
		showProgressDialog(R.string.loading_divisions);
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
				} else if (ApiData.COMMAND_DIVISIONS.equalsIgnoreCase(command) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK) {
						List<Division> divisions = (List<Division>) apiResponse.getData();
						Division.setDivisionList(divisions);
					}
					refreshWorkTasks(true);
				} else if ((ApiData.COMMAND_WORKTASKS.equalsIgnoreCase(command) || 
						ApiData.COMMAND_CONTACT_WORKTASKS.equalsIgnoreCase(command)) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK) {
						worktasks = (List<WorkTask>) apiResponse.getData();
						updateTabs();
						if (fragment != null) {
							fragment.updateViews();
						}
						loadContacts(false);
						sendPushNotificationData();
					}
				} else if (ApiData.COMMAND_CONTACTS.equalsIgnoreCase(command) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK) {
						List<Account> contacts = (ArrayList<Account>) apiResponse.getData();
						Account.setContactList(contacts);
						
						long loggedId = Account.getCurrent(this).getId();
						long lastUpdateTime = settings.getLong(Settings.CHAT_LAST_UPDATE_TIME + "_" + loggedId);
						loadChat(lastUpdateTime, false);
					}
				} else if (ApiData.COMMAND_WORKTASK_SCHEDULED_STATUS.equalsIgnoreCase(command) && ApiData.METHOD_PUT.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						refreshWorkTasks(true);
					}
				} else if (ApiData.COMMAND_WORKTASK_STATUS.equalsIgnoreCase(command) && ApiData.METHOD_PUT.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						refreshWorkTasks(true);
					}
				} else if (ApiData.COMMAND_PUSH_SERVICE.equalsIgnoreCase(command) && ApiData.METHOD_POST.equalsIgnoreCase(method)) {
				} else if (ApiData.COMMAND_CHAT.equalsIgnoreCase(command)) {
					long loggedId = Account.getCurrent(this).getId();
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							List<ChatMessage> messages = (List<ChatMessage>) apiResponse.getData();
							long lastUpdateTime = Utilities.findLastUpdateTime(messages);
							settings.setLong(Settings.CHAT_LAST_UPDATE_TIME + "_" + loggedId, lastUpdateTime);
							chatStorage.addMessages(messages);
							invalidateOptionsMenu();
						}
					}
				}
			}
		}
	}
	
	private void updateTabs() {
		ActionBar actionBar = getActionBar();
				
		String issuedText = getString(R.string.issued);
		List<WorkTask> issuedTasks = Utilities.filterWorktasks(worktasks, new ScheduledStatus[] {ScheduledStatus.Assigned});
		if (!Utilities.isEmpty(issuedTasks)) {
			issuedText += " " + issuedTasks.size();
		}
		Tab issuedTab = actionBar.getTabAt(0);
		issuedTab.setText(issuedText);
		
		
		String acceptedText = getString(R.string.accepted);
		List<WorkTask> acceptedTasks = Utilities.filterWorktasks(worktasks, new ScheduledStatus[] {
			ScheduledStatus.Accepted,
			ScheduledStatus.Delayed,
			ScheduledStatus.Completed,
			ScheduledStatus.Stopped,
			ScheduledStatus.Active
		});
		if (!Utilities.isEmpty(acceptedTasks)) {
			acceptedText += " " + acceptedTasks.size();
		}
		Tab acceptedTab = actionBar.getTabAt(1);
		acceptedTab.setText(acceptedText);
		
		String passedText = getString(R.string.passed);
		List<WorkTask> passedTasks = Utilities.filterWorktasks(worktasks, new ScheduledStatus[] {ScheduledStatus.Rejected});
		if (!Utilities.isEmpty(passedTasks)) {
			passedText += " " + passedTasks.size();
		}
		Tab passedTab = actionBar.getTabAt(2);
		passedTab.setText(passedText);
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
	
	public void showRemoveTaskDialog(final WorkTask task) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.remove_task_pattern, task.getDescription()));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeTask(task);
				dialog.dismiss();
			}
		});
		dialog.show(getFragmentManager(), "RemoveTaskDialog");
	}
	
	private void removeTask(WorkTask task) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASK_SCHEDULED_STATUS);
			intent.putExtra(ApiData.PARAM_ID, task.getId());
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
			intent.putExtra(ApiData.PARAM_BODY, "\"" + ScheduledStatus.Created.name() + "\"");
			startService(intent);
			showProgressDialog(R.string.removing_task);
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
	
	private void sendPushNotificationData() {
		if (Utilities.isConnectionAvailable(this)) {
			try {
				Intent intent = new Intent(this, ApiService.class);
				intent.setAction(ApiData.COMMAND_PUSH_SERVICE);
				intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_POST);
				
				JSONObject obj = new JSONObject();
				obj.put(ApiData.PARAM_CONTACT_ID, Account.getCurrent(this).getId());
				obj.put(ApiData.PARAM_DEVICE_ID, "2");
				String androidID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
				obj.put(ApiData.PARAM_PUSH_TOKEN, androidID);
				
				intent.putExtra(ApiData.PARAM_BODY, obj.toString());
				startService(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}