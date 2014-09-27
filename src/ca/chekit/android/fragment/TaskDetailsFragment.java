package ca.chekit.android.fragment;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Account;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.screen.BaseScreen;
import ca.chekit.android.screen.TaskDetailsScreen;
import ca.chekit.android.screen.TaskLocationScreen;
import ca.chekit.android.screen.TaskStatusScreen;
import ca.chekit.android.util.Utilities;

public class TaskDetailsFragment extends TaskFragment implements OnClickListener, OnItemSelectedListener {
	
	private TextView taskTitle;
	private TextView addressView;
	private TextView dueView;
	private TextView durationView;
	private View clearTimeBtn;
	private Spinner daysView;
	private Spinner hoursView;
	private Spinner minutesView;
	private ImageView taskIcon;
	private TextView statusView;
	private TextView lastUpdateView;
	private View taskStatusLayout;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.task_details_fragment, null);
		initializeViews(rootView);
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (isTaskDetailsLoaded()) {
			updateViews();
		} else {
			loadTaskDetails();
		}
	}
	
	private void initializeViews(View rootView) {
		taskTitle = (TextView) rootView.findViewById(R.id.taskTitle);
		
		addressView = (TextView) rootView.findViewById(R.id.addressView);
		dueView = (TextView) rootView.findViewById(R.id.dueView);
		durationView = (TextView) rootView.findViewById(R.id.durationView);
		
		clearTimeBtn = rootView.findViewById(R.id.clearTimeBtn);
		
		daysView = (Spinner) rootView.findViewById(R.id.daysView);
		daysView.setAdapter(new ArrayAdapter<String>(getActivity(), 
			android.R.layout.simple_spinner_item, Utilities.generateRange(0, 100)));
		
		hoursView = (Spinner) rootView.findViewById(R.id.hoursView);
		hoursView.setAdapter(new ArrayAdapter<String>(getActivity(), 
			android.R.layout.simple_spinner_item, Utilities.generateRange(0, 60)));
		
		minutesView = (Spinner) rootView.findViewById(R.id.minutesView);
		minutesView.setAdapter(new ArrayAdapter<String>(getActivity(), 
			android.R.layout.simple_spinner_item, Utilities.generateRange(0, 60)));
		
		taskIcon = (ImageView) rootView.findViewById(R.id.taskIcon);
		statusView = (TextView) rootView.findViewById(R.id.statusView);
		lastUpdateView = (TextView) rootView.findViewById(R.id.lastUpdateView);
		
		taskStatusLayout = rootView.findViewById(R.id.taskStatusLayout);
		taskStatusLayout.setOnClickListener(this);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		long timeRemaining = getTimeRemaining();
		((TaskDetailsScreen) getActivity()).requestUpdateRemainingTime(timeRemaining);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
	
	private long getTimeRemaining() {
		long days = daysView.getSelectedItemPosition();
		long hours = hoursView.getSelectedItemPosition();
		long minutes = minutesView.getSelectedItemPosition();
		return days*24*60 + hours*60 + minutes;
	}
	
	public void clearTime() {
		((TaskDetailsScreen) getActivity()).clearRemainingTime();
	}
	
	private void updateViews() {
		WorkTask task = getTaskDetails();
		if (task != null) {
			taskTitle.setText(task.getDescription());
			addressView.setText(task.getAddress());
			dueView.setText(Utilities.convertDate(task.getDueDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd));
			durationView.setText(task.getHumanReadableDuration());
			
			int days = (int) task.getRemaining() / (60 * 24);
		    int remainderDays = (int) (task.getRemaining() % (60 * 24));
		    int hours = remainderDays / 60;
		    int minutes = remainderDays % 60;
		    
		    final long loggedId = Account.getCurrent(getActivity()).getId();
			final long assigneeId = task.getAssigneeId();
			
			clearTimeBtn.setOnClickListener(loggedId == assigneeId ? this : null);
		    
		    daysView.setOnItemSelectedListener(null);
		    daysView.setSelection(days);
		    daysView.setEnabled(loggedId == assigneeId);
		    
		    hoursView.setOnItemSelectedListener(null);
			hoursView.setSelection(hours);
			hoursView.setEnabled(loggedId == assigneeId);
			
			minutesView.setOnItemSelectedListener(null);
			minutesView.setSelection(minutes);
			minutesView.setEnabled(loggedId == assigneeId);			
			
			int iconResId = task.getWorkStatus().getIconWithBackground();
			if (iconResId == 0) {
				taskIcon.setVisibility(View.GONE);
			} else {
				taskIcon.setImageResource(iconResId);
				taskIcon.setVisibility(View.VISIBLE);
			}
			
			statusView.setText(task.getWorkStatus().getName());
			lastUpdateView.setText(getActivity().getString(R.string.last_update_pattern, 
					Utilities.convertDate(task.getWorkStatusChanged(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd)));
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							daysView.setOnItemSelectedListener(TaskDetailsFragment.this);
							hoursView.setOnItemSelectedListener(TaskDetailsFragment.this);
							minutesView.setOnItemSelectedListener(TaskDetailsFragment.this);
						}
					});
				}
			}, 200);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.task_details_fragment_actions, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.action_map:
        	Intent intent = new Intent(getActivity(), TaskLocationScreen.class);
        	intent.putExtra(ApiData.PARAM_ID, taskId);
        	startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clearTimeBtn:
			clearTime();
			break;
		case R.id.taskStatusLayout:
			Intent intent = new Intent(getActivity(), TaskStatusScreen.class);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			startActivityForResult(intent, WorkTasksFragment.VIEW_TASK_STATUS_REQUEST_CODE);
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == WorkTasksFragment.VIEW_TASK_STATUS_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				((BaseScreen) getActivity()).showChangeTaskStatusDialog(data);
			}
		}
	}
	
	private WorkTask getTaskDetails() {
		return ((TaskDetailsScreen) getActivity()).getTaskDetails();
	}
	
	private void setTaskDetails(WorkTask task) {
		((TaskDetailsScreen) getActivity()).setTaskDetails(task);
	}
	
	private boolean isTaskDetailsLoaded() {
		return ((TaskDetailsScreen) getActivity()).isTaskDetailsLoaded();
	}
	
	private void setTaskDetailsLoaded(boolean taskDetailsLoaded) {
		((TaskDetailsScreen) getActivity()).setTaskDetailsLoaded(taskDetailsLoaded);
	}
	
	private void loadTaskDetails() {
		((TaskDetailsScreen) getActivity()).loadTaskDetails();
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_WORKTASK.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							WorkTask task = (WorkTask) apiResponse.getData();
							setTaskDetails(task);
							setTaskDetailsLoaded(true);
							updateViews();
						}
					} else if (ApiData.METHOD_PUT.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_NO_CONTENT) {
							WorkTask task = getTaskDetails();
							task.setRemaining(getTimeRemaining());
							((BaseScreen) getActivity()).updateWorkTaskStatus(task);
						}
					}
				} else if (ApiData.COMMAND_WORKTASK_STATUS.equalsIgnoreCase(command) && ApiData.METHOD_PUT.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						getActivity().setResult(Activity.RESULT_OK);
						setTaskDetailsLoaded(false);
						loadTaskDetails();
					}
				}
			}
		}
	}

}
