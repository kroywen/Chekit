package ca.chekit.android.fragment;

import ca.chekit.android.adapter.AcceptedTasksAdapter;
import ca.chekit.android.adapter.WorkTasksAdapter;
import ca.chekit.android.model.ScheduledStatus;

public class AcceptedFragment extends WorkTasksFragment {

	@Override
	protected WorkTasksAdapter getAdapter() {
		return new AcceptedTasksAdapter(getActivity(), worktasks);
	}

	@Override
	protected ScheduledStatus getScheduledStatus() {
		return ScheduledStatus.Accepted;
	}

}
