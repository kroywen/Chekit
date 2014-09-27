package ca.chekit.android.fragment;

import android.widget.BaseAdapter;
import ca.chekit.android.adapter.PassedTasksAdapter;
import ca.chekit.android.model.ScheduledStatus;

public class PassedFragment extends WorkTasksFragment {

	@Override
	protected BaseAdapter getAdapter() {
		return new PassedTasksAdapter(this, worktasks);
	}

	@Override
	protected ScheduledStatus[] getScheduledStatus() {
		return new ScheduledStatus[] {
			ScheduledStatus.Rejected
		};
	}

}
