package ca.chekit.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.screen.WorkTasksScreen;
import ca.chekit.android.util.Utilities;

public class PassedTasksAdapter extends WorkTasksAdapter {
	
	public PassedTasksAdapter(Context context, List<WorkTask> worktasks) {
		super(context, worktasks);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.passed_list_item, null);
		}
		
		final WorkTask task = getItem(position);
		
		TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);
		taskTitle.setText(task.getDescription());
		
		TextView dueView = (TextView) convertView.findViewById(R.id.dueView);
		dueView.setText(context.getString(R.string.due_pattern, Utilities.convertDate(task.getDueDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd)));
		
		TextView durationView = (TextView) convertView.findViewById(R.id.durationView);
		durationView.setText(context.getString(R.string.duration_pattern, task.getHumanReadableDuration()));
		
		View acceptBtn = convertView.findViewById(R.id.acceptBtn);
		acceptBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((WorkTasksScreen) context).showAcceptTaskDialog(task);
			}
		});
		
		View unpassBtn = convertView.findViewById(R.id.unpassBtn);
		unpassBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((WorkTasksScreen) context).showUnpassTaskDialog(task);
			}
		});
		
		return convertView;
	}

}
