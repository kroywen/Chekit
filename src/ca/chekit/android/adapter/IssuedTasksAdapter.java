package ca.chekit.android.adapter;

import java.util.List;

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
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.screen.WorkTasksScreen;
import ca.chekit.android.util.Utilities;

public class IssuedTasksAdapter extends BaseAdapter {
	
	private Fragment fragment;
	private List<WorkTask> worktasks;
	
	public IssuedTasksAdapter(Fragment fragment, List<WorkTask> worktasks) {
		this.fragment = fragment;
		this.worktasks = worktasks;
	}
	
	@Override
	public int getCount() {
		return worktasks.size();
	}
	
	@Override
	public WorkTask getItem(int position) {
		return worktasks.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.issued_list_item, null);
		}
		
		final WorkTask task = getItem(position);
		
		ImageView taskIcon = (ImageView) convertView.findViewById(R.id.taskIcon);
		int iconResId = task.getWorkStatus().getIconWithBackground();
		if (iconResId == 0) {
			taskIcon.setVisibility(View.GONE);
		} else {
			taskIcon.setImageResource(iconResId);
			taskIcon.setVisibility(View.VISIBLE);
		}
		
		TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);
		taskTitle.setText(task.getDescription());
		
		TextView addressView = (TextView) convertView.findViewById(R.id.addressView);
		addressView.setText(task.getAddress());
		addressView.setVisibility(task.hasAddress() ? View.VISIBLE : View.GONE);
		
		TextView dueView = (TextView) convertView.findViewById(R.id.dueView);
		dueView.setText(fragment.getActivity().getString(R.string.due_pattern, 
				Utilities.convertDate(task.getDueDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd)));
		
		TextView durationView = (TextView) convertView.findViewById(R.id.durationView);
		durationView.setText(fragment.getActivity().getString(R.string.duration_pattern, task.getHumanReadableDuration()));
		
		View acceptBtn = convertView.findViewById(R.id.acceptBtn);
		acceptBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((WorkTasksScreen) fragment.getActivity()).showAcceptTaskDialog(task);
			}
		});
		
		View passBtn = convertView.findViewById(R.id.passBtn);
		passBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((WorkTasksScreen) fragment.getActivity()).showPassTaskDialog(task);
			}
		});
		
		return convertView;
	}

}
