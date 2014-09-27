package ca.chekit.android.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.model.WorkStatus;

public class TaskStatusAdapter extends BaseAdapter {
	
	private Context context;
	private int selected = -1;
	
	private List<WorkStatus> statuses;
	
	public TaskStatusAdapter(Context context) {
		this.context = context;
		statuses = WorkStatus.getStatusList().subList(1, WorkStatus.getStatusList().size());
	}

	@Override
	public int getCount() {
		return statuses.size();
	}

	@Override
	public WorkStatus getItem(int position) {
		return statuses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	convertView = inflater.inflate(R.layout.task_status_list_item, null);
        }
        
        WorkStatus status = statuses.get(position);

        ImageView statusIcon = (ImageView) convertView.findViewById(R.id.statusIcon);
        statusIcon.setImageResource(status.getIconWithBackground());
       	statusIcon.setBackgroundResource(position == selected ? R.drawable.task_status_selected_background :
       		R.drawable.task_status_normal_background);
        
        TextView statusName = (TextView) convertView.findViewById(R.id.statusName);
        statusName.setText(status.getName());
        statusName.setTextColor(position == selected ? 0xff303030 : 0xff878b8e);
        
        ImageView checkmarkIcon = (ImageView) convertView.findViewById(R.id.checkmarkIcon);
        checkmarkIcon.setVisibility(position == selected ? View.VISIBLE : View.INVISIBLE); 
        
        return convertView;
	}
	
	public void setCurrentTaskStatus(WorkStatus status) {
		selected = status.getOrder();
		notifyDataSetChanged();
	}
	
	public WorkStatus getWorkStatus(int position) {
		return statuses.get(position);
	}
	
	public WorkStatus getSelectedWorkStatus() {
		return (selected == -1) ? null : getWorkStatus(selected);
	}

}
