package ca.chekit.android.fragment;

import android.widget.BaseAdapter;
import ca.chekit.android.adapter.IssuedTasksAdapter;
import ca.chekit.android.model.ScheduledStatus;

public class IssuedFragment extends WorkTasksFragment {

	@Override
	protected BaseAdapter getAdapter() {
		return new IssuedTasksAdapter(this, worktasks);
	}

	@Override
	protected ScheduledStatus[] getScheduledStatus() {
		return new ScheduledStatus[] {
			ScheduledStatus.Assigned
		};
	}

}
