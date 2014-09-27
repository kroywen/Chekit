package ca.chekit.android.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.model.ScheduledStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.screen.BaseScreen;
import ca.chekit.android.screen.TaskDetailsScreen;
import ca.chekit.android.screen.TaskStatusScreen;
import ca.chekit.android.screen.WorkTasksScreen;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public abstract class WorkTasksFragment extends Fragment implements OnCheckedChangeListener {
	
	public static final int VIEW_TASK_DETAILS_REQUEST_CODE = 0;
	public static final int VIEW_TASK_STATUS_REQUEST_CODE = 1;
	
	protected View empty;
	protected PullToRefreshListView list;
	private RadioGroup sortGroup;
	
	protected List<WorkTask> worktasks;
	protected BaseAdapter adapter;
	protected int groupOrder;
	protected Settings settings;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.worktasks_fragment, null);
		initializeViews(view);
		updateViews();
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = new Settings(getActivity());
	}
	
	protected void initializeViews(View view) {
		list = (PullToRefreshListView) view.findViewById(R.id.list);
		list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (Utilities.isConnectionAvailable(getActivity())) {
					((WorkTasksScreen) getActivity()).refreshWorkTasks(false);
				} else {
					list.onRefreshComplete();
					((WorkTasksScreen) getActivity()).showConnectionErrorDialog();
				}
			}
		});
		empty = view.findViewById(R.id.empty);
		
		sortGroup = (RadioGroup) view.findViewById(R.id.sortGroup);
		sortGroup.setOnCheckedChangeListener(this);
	}
	
	public void updateViews() {
		WorkTasksScreen screen = (WorkTasksScreen) getActivity();
		worktasks = Utilities.filterWorktasks(screen.getWorktasks(), getScheduledStatus());
		if (Utilities.isEmpty(worktasks)) {
			list.setVisibility(View.INVISIBLE);
			empty.setVisibility(View.VISIBLE);
		} else {
			list.setVisibility(View.VISIBLE);
			list.onRefreshComplete();
			empty.setVisibility(View.INVISIBLE);
			
			adapter = getAdapter();
			list.setAdapter(adapter);
		}
	}
	
	protected abstract BaseAdapter getAdapter();
	
	protected abstract ScheduledStatus[] getScheduledStatus();
	
	public void onTaskDetailsClicked(WorkTask task) {
		Intent intent = new Intent(getActivity(), TaskDetailsScreen.class);
		intent.putExtra(ApiData.PARAM_ID, task.getId());
		intent.putExtra(ApiData.PARAM_DESCRIPTION, task.getDescription());
		startActivityForResult(intent, WorkTasksFragment.VIEW_TASK_DETAILS_REQUEST_CODE);
	}
	
	public void onTaskStatusClicked(WorkTask task) {
		Intent intent = new Intent(getActivity(), TaskStatusScreen.class);
		intent.putExtra(ApiData.PARAM_ID, task.getId());
		startActivityForResult(intent, WorkTasksFragment.VIEW_TASK_STATUS_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VIEW_TASK_DETAILS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			((WorkTasksScreen) getActivity()).refreshWorkTasks(true);
		} else if (requestCode == VIEW_TASK_STATUS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			((BaseScreen) getActivity()).showChangeTaskStatusDialog(data);
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.groupByDueDateBtn:
			groupOrder = WorkTask.GROUP_BY_DUE_DATE;
			break;
		case R.id.groupByDivisionBtn:
			groupOrder = WorkTask.GROUP_ON_DIVISION;
			break;
		case R.id.groupByStatusBtn:
			groupOrder = WorkTask.GROUP_ON_STATUS;
			break;
		}
		updateViews();
	}

}
