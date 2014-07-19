package ca.chekit.android.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.screen.BaseScreen;
import ca.chekit.android.screen.TaskLocationScreen;
import ca.chekit.android.util.Utilities;

public class AcceptedTasksAdapter extends WorkTasksAdapter {
	
	public AcceptedTasksAdapter(Context context, List<WorkTask> worktasks) {
		super(context, worktasks);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.accepted_list_item, null);
		}
		
		final WorkTask task = getItem(position);
		
		ImageView taskIcon = (ImageView) convertView.findViewById(R.id.taskIcon);
		int iconResId = Utilities.getTaskIconResId(context, task.getWorkStatus());
		if (iconResId == 0) {
			taskIcon.setVisibility(View.GONE);
		} else {
			taskIcon.setImageResource(iconResId);
			taskIcon.setVisibility(View.VISIBLE);
		}
		
		TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);
		taskTitle.setText(task.getDescription());
		
		TextView dueView = (TextView) convertView.findViewById(R.id.dueView);
		dueView.setText(context.getString(R.string.due_pattern, Utilities.convertDate(task.getDueDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd)));
		
		TextView durationView = (TextView) convertView.findViewById(R.id.durationView);
		durationView.setText(context.getString(R.string.duration_pattern, task.getHumanReadableDuration()));
		
		TextView photosView = (TextView) convertView.findViewById(R.id.photosView);
		photosView.setText(String.valueOf(task.getPhotosNumber()));
		
		TextView notesView = (TextView) convertView.findViewById(R.id.notesView);
		notesView.setText(String.valueOf(task.getNotesNumber()));
		
		View mapBtn = convertView.findViewById(R.id.mapBtn);
		mapBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, TaskLocationScreen.class);
				intent.putExtra(ApiData.PARAM_ID, task.getId());
				((BaseScreen) context).startActivity(intent);
			}
		});
		
		return convertView;
	}

}
