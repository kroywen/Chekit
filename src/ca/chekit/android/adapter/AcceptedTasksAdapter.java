package ca.chekit.android.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.fragment.WorkTasksFragment;
import ca.chekit.android.model.Division;
import ca.chekit.android.model.WorkStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.util.Utilities;

public class AcceptedTasksAdapter extends BaseAdapter {
	
	protected Fragment fragment;
	protected List<WorkTask> worktasks;
	protected int groupOrder;
	protected List<ListItem> items;
	
	public AcceptedTasksAdapter(Fragment fragment, List<WorkTask> worktasks, int groupOrder) {
		this.fragment = fragment;
		this.worktasks = worktasks;
		this.groupOrder = groupOrder;
		prepareItems();
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public ListItem getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItem item = getItem(position);
		if (item.getType() == ListItem.TYPE_HEADER) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.header_list_item, null);

			TextView headerTitle = (TextView) convertView.findViewById(R.id.headerTitle);
			if (headerTitle != null) {
				headerTitle.setText(item.getTitle());
				if (groupOrder == WorkTask.GROUP_ON_STATUS) {
					int color = WorkStatus.getColorForName(item.getTitle());
					headerTitle.setBackgroundColor(color);
				}
			}
			return convertView;
		}
		
		LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.accepted_list_item, null);
		
		final WorkTask task = item.getWorkTask();
		
		TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);
		taskTitle.setText(task.getDescription());
		
		TextView addressView = (TextView) convertView.findViewById(R.id.addressView);
		addressView.setText(task.getAddress());
		addressView.setVisibility(task.hasAddress() ? View.VISIBLE : View.GONE);
		
		TextView dueView = (TextView) convertView.findViewById(R.id.dueView);
		dueView.setText(Utilities.convertDate(task.getDueDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd));
		
		TextView durationView = (TextView) convertView.findViewById(R.id.durationView);
		durationView.setText(task.getHumanReadableDuration());
		
		TextView remainingTimeView = (TextView) convertView.findViewById(R.id.remainingTimeView);
		remainingTimeView.setText(task.getHumanReadableRemaining());
		
		ImageView taskIcon = (ImageView) convertView.findViewById(R.id.taskIcon);
		int iconResId = task.getWorkStatus().getIconWithBackground();
		if (iconResId == 0) {
			taskIcon.setVisibility(View.GONE);
		} else {
			taskIcon.setImageResource(iconResId);
			taskIcon.setVisibility(View.VISIBLE);
		}
		
		TextView statusView = (TextView) convertView.findViewById(R.id.statusView);
		statusView.setText(task.getWorkStatus().getName());
		
		TextView lastUpdateView = (TextView) convertView.findViewById(R.id.lastUpdateView);
		if (task.hasWorkStatusChanged()) {
			lastUpdateView.setText(fragment.getActivity().getString(R.string.last_update_pattern, 
				Utilities.convertDate(task.getWorkStatusChanged(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd)));
		} else {
			lastUpdateView.setText(fragment.getActivity().getString(R.string.no_updates));
		}
		
		View taskDetailsLayout = convertView.findViewById(R.id.taskDetailsLayout);
		taskDetailsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((WorkTasksFragment) fragment).onTaskDetailsClicked(task);
			}
		});
		
		View taskStatusLayout = convertView.findViewById(R.id.taskStatusLayout);
		taskStatusLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((WorkTasksFragment) fragment).onTaskStatusClicked(task);
			}
		});
		
		return convertView;
	}
	
	private void prepareItems() {
		items = new ArrayList<ListItem>();
		if (Utilities.isEmpty(worktasks)) {
			return;
		}
		
		Map<String, List<WorkTask>> map = new TreeMap<String, List<WorkTask>>();
		for (WorkTask task : worktasks) {
			String key = getWorkTaskKey(task);
			if (map.containsKey(key)) {
				List<WorkTask> tasks = (List<WorkTask>) map.get(key);
				tasks.add(task);
				map.put(key, tasks);
			} else {
				List<WorkTask> tasks = new ArrayList<WorkTask>();
				tasks.add(task);
				map.put(key, tasks);
			}
		}
		
		map = sortItems(map);
		
		Set<String> keys = map.keySet();
		Iterator<String> i = keys.iterator();
		while (i.hasNext()) {
			String key = i.next();
			items.add(new ListItem(key));
			List<WorkTask> list = map.get(key);
			if (!Utilities.isEmpty(list)) {
				for (WorkTask task : list) {
					items.add(new ListItem(task));
				}
			}
		}
	}
	
	private String getWorkTaskKey(WorkTask worktask) {
		switch (groupOrder) {
		case WorkTask.GROUP_BY_DUE_DATE:
			long nowTime = System.currentTimeMillis();
			long todayStartTime = Utilities.getDayStart(nowTime);
			long todayEndTime = Utilities.getDayEnd(nowTime);
			long tomorrowEndTime = Utilities.getDayEnd(nowTime + 24*60*60*1000);
			
			long taskTime = worktask.getDueDateMillis();
			if (taskTime <= todayStartTime) {
				return "Prior";
			} else if (taskTime > todayStartTime && taskTime <= todayEndTime) {
				return "Today";
			} else if (taskTime > todayEndTime && taskTime <= tomorrowEndTime) {
				return "Tomorrow";
			} else {
				return "Up coming";
			}
		case WorkTask.GROUP_ON_DIVISION:
			Division division = Division.getDivisionById(worktask.getDivisionId());
			return (division == null) ? "No division" : division.getName();
		case WorkTask.GROUP_ON_STATUS:
			return worktask.getWorkStatus().getColorName();
		}
		return null;
	}
	
	private Map<String, List<WorkTask>> sortItems(Map<String, List<WorkTask>> map) {
		List<Map.Entry<String, List<WorkTask>>> list = 
			new LinkedList<Map.Entry<String, List<WorkTask>>>(map.entrySet());
 
		Comparator<Map.Entry<String, List<WorkTask>>> comparator = (groupOrder == WorkTask.GROUP_BY_DUE_DATE) ?
			WorkTask.dueDateComparator : (groupOrder == WorkTask.GROUP_ON_STATUS) ? WorkTask.statusNameComparator : 
			WorkTask.divisionNameComparator;
		Collections.sort(list, comparator);
 
		Map<String, List<WorkTask>> sortedMap = new LinkedHashMap<String, List<WorkTask>>();
		for (Iterator<Map.Entry<String, List<WorkTask>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, List<WorkTask>> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public class ListItem {
		
		public static final int TYPE_HEADER = 0;
		public static final int TYPE_WORKTASK = 1;
		
		private String title;
		private WorkTask worktask;
		private int type;
		
		public ListItem(WorkTask worktask) {
			this(null, worktask, TYPE_WORKTASK);
		}
		
		public ListItem(String title) {
			this(title, null, TYPE_HEADER);
		}
		
		public ListItem(String title, WorkTask worktask, int type) {
			this.title = title;
			this.worktask = worktask;
			this.type = type;
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public WorkTask getWorkTask() {
			return worktask;
		}
		
		public void setWorkTask(WorkTask worktask) {
			this.worktask = worktask;
		}
		
		public int getType() {
			return type;
		}
		
		public void setType(int type) {
			this.type = type;
		}
		
	}

}
