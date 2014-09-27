package ca.chekit.android.fragment;

import android.view.View;
import android.widget.BaseAdapter;
import ca.chekit.android.R;
import ca.chekit.android.adapter.AcceptedTasksAdapter;
import ca.chekit.android.model.ScheduledStatus;

public class AcceptedFragment extends WorkTasksFragment {

	@Override
	protected BaseAdapter getAdapter() {
		return new AcceptedTasksAdapter(this, worktasks, groupOrder);
	}

	@Override
	protected ScheduledStatus[] getScheduledStatus() {
		return new ScheduledStatus[] {
			ScheduledStatus.Accepted,
			ScheduledStatus.Delayed,
			ScheduledStatus.Completed,
			ScheduledStatus.Stopped,
			ScheduledStatus.Active
		};
	}
	
	@Override
	protected void initializeViews(View view) {
		super.initializeViews(view);
		View sortGroupLayout = view.findViewById(R.id.sortGroupLayout);
		sortGroupLayout.setVisibility(View.VISIBLE);
	}

}
