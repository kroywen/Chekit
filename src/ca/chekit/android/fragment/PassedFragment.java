package ca.chekit.android.fragment;

import ca.chekit.android.adapter.PassedTasksAdapter;
import ca.chekit.android.adapter.WorkTasksAdapter;
import ca.chekit.android.model.ScheduledStatus;

public class PassedFragment extends WorkTasksFragment {

	@Override
	protected WorkTasksAdapter getAdapter() {
		return new PassedTasksAdapter(getActivity(), worktasks);
	}

	@Override
	protected ScheduledStatus getScheduledStatus() {
		return ScheduledStatus.Rejected;
	}

}
