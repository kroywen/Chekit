package ca.chekit.android.fragment;

import ca.chekit.android.adapter.IssuedTasksAdapter;
import ca.chekit.android.adapter.WorkTasksAdapter;
import ca.chekit.android.model.ScheduledStatus;

public class IssuedFragment extends WorkTasksFragment {

	@Override
	protected WorkTasksAdapter getAdapter() {
		return new IssuedTasksAdapter(getActivity(), worktasks);
	}

	@Override
	protected ScheduledStatus getScheduledStatus() {
		return ScheduledStatus.Assigned;
	}

}
