package ca.chekit.android.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import ca.chekit.android.R;
import ca.chekit.android.adapter.WorkTasksAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.model.ScheduledStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.screen.TaskScreen;
import ca.chekit.android.screen.WorkTasksScreen;
import ca.chekit.android.util.Utilities;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public abstract class WorkTasksFragment extends Fragment implements OnItemClickListener, OnCheckedChangeListener {
	
	public static final int VIEW_TASK_REQUEST_CODE = 0;
	
	public static final int SORT_BY_DUE_DATE = 0;
	public static final int GROUP_ON_DIVISION = 1;
	public static final int GROUP_ON_STATUS = 2;
	
	protected View empty;
	protected PullToRefreshListView list;
	private RadioGroup sortGroup;
	protected List<WorkTask> worktasks;
	protected WorkTasksAdapter adapter;
	protected int sortOrder;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.worktasks_fragment, null);
		initializeViews(view);
		updateViews();
		return view;
	}
	
	private void initializeViews(View view) {
		list = (PullToRefreshListView) view.findViewById(R.id.list);
		list.setOnItemClickListener(this);
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
		worktasks = filterWorktasks(screen.getWorktasks(), getScheduledStatus());
		sortWorktasks();
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
	
	private List<WorkTask> filterWorktasks(List<WorkTask> worktasks, ScheduledStatus status) {
		if (Utilities.isEmpty(worktasks)) {
			return null;
		}
		List<WorkTask> filtered = new ArrayList<WorkTask>();
		for (WorkTask task : worktasks) {
			if (task.getScheduledStatus() == status) {
				filtered.add(task);
			}
		}
		return filtered;
	}
	
	protected abstract WorkTasksAdapter getAdapter();
	
	protected abstract ScheduledStatus getScheduledStatus();
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WorkTask task = adapter.getItem(position - 1);
		
		Intent intent = new Intent(getActivity(), TaskScreen.class);
		intent.putExtra(ApiData.PARAM_ID, task.getId());
		startActivityForResult(intent, VIEW_TASK_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VIEW_TASK_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				if (Utilities.isConnectionAvailable(getActivity())) {
					((WorkTasksScreen) getActivity()).refreshWorkTasks(true);
				}
			}
		}
	}
	
	protected void sortWorktasks() {
		if (Utilities.isEmpty(worktasks)) {
			return;
		}
		Collections.sort(worktasks, getSortComparator());
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.groupByDueDateBtn:
			sortOrder = SORT_BY_DUE_DATE;
			break;
		case R.id.groupByDivisionBtn:
			sortOrder = GROUP_ON_DIVISION;
			break;
		case R.id.groupByStatusBtn:
			sortOrder = GROUP_ON_STATUS;
			break;
		}
		updateViews();
	}
	
	private Comparator<WorkTask> getSortComparator() {
		switch (sortOrder) {
		case 0:
			return WorkTask.DueDateComparator;
		case 1:
			return WorkTask.DivisionComparator;
		case 2:
			return WorkTask.StatusComparator;
		default:
			return null;
		}
	}

}
