package ca.chekit.android.adapter;

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
	
	private int[] statusIcons = {
		R.drawable.status_on_route,
		R.drawable.status_on_site,
		R.drawable.status_verify_site,
		R.drawable.status_start,
		R.drawable.status_active,
		R.drawable.status_complete,
		R.drawable.status_problems_delayed,
		R.drawable.status_incomplete_leave_site,
		R.drawable.status_safety_issue,
		R.drawable.status_problems_stop,
		R.drawable.status_safety_danger,
		R.drawable.status_problems_leave_site
	};
	private String[] statusNames;
	
	public TaskStatusAdapter(Context context) {
		this.context = context;
		statusNames = context.getResources().getStringArray(R.array.status_names);
	}

	@Override
	public int getCount() {
		return statusIcons.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	convertView = inflater.inflate(R.layout.task_status_list_item, null);
        }

        ImageView statusIcon = (ImageView) convertView.findViewById(R.id.statusIcon);
        statusIcon.setImageResource(statusIcons[position]);
       	statusIcon.setBackgroundResource(position == selected ? R.drawable.task_status_selected_background :
       		R.drawable.task_status_normal_background);
        
        TextView statusName = (TextView) convertView.findViewById(R.id.statusName);
        statusName.setText(statusNames[position]);
        statusName.setTextColor(position == selected ? 0xff303030 : 0xff878b8e);
        
        ImageView checkmarkIcon = (ImageView) convertView.findViewById(R.id.checkmarkIcon);
        checkmarkIcon.setVisibility(position == selected ? View.VISIBLE : View.INVISIBLE); 
        
        return convertView;
	}
	
	public void setCurrentTaskStatus(WorkStatus status) {
		switch (status) {
		case Active:				selected = 4;	break;
		case Cancelled:				selected = -1;	break;
		case Closed:				selected = -1;	break;
		case CompleteLeaveSite:		selected = -1;	break;
		case Complete:				selected = 5;	break;
		case IncompleteLeaveSite:	selected = 7;	break;
		case New:					selected = -1;	break;
		case OnRoute:				selected = 0;	break;
		case OnSite:				selected = 1;	break;
		case ProblemsActive:		selected = -1;	break;
		case ProblemsDelayed:		selected = 6;	break;
		case ProblemsLeaveSite:		selected = 11;	break;
		case ProblemsStop:			selected = 9;	break;
		case SafetyDanger:			selected = 10;	break;
		case SafetyIssue:			selected = 8;	break;
		case Start:					selected = 3;	break;
		case VerifySite:			selected = 2;	break;
		default:					selected = -1;	break;
		}
		notifyDataSetChanged();
	}
	
	public WorkStatus getWorkStatus(int position) {
		switch (position) {
		case 0: 	return WorkStatus.OnRoute;
		case 1: 	return WorkStatus.OnSite;
		case 2: 	return WorkStatus.VerifySite;
		case 3: 	return WorkStatus.Start;
		case 4: 	return WorkStatus.Active;
		case 5: 	return WorkStatus.Complete;
		case 6: 	return WorkStatus.ProblemsDelayed;
		case 7: 	return WorkStatus.IncompleteLeaveSite;
		case 8:		return WorkStatus.SafetyIssue;
		case 9:		return WorkStatus.ProblemsStop;
		case 10:	return WorkStatus.SafetyDanger;
		case 11:	return WorkStatus.ProblemsLeaveSite;
		default:	return null;
		}
	}
	
	public WorkStatus getSelectedWorkStatus() {
		return getWorkStatus(selected);
	}

}
