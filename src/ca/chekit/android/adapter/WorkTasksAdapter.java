package ca.chekit.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ca.chekit.android.model.WorkTask;

public class WorkTasksAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<WorkTask> worktasks;
	
	public WorkTasksAdapter(Context context, List<WorkTask> worktasks) {
		this.context = context;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
