package ca.chekit.android.fragment;

import android.view.View;
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
	
	@Override
	protected void initializeViews(View view) {
		super.initializeViews(view);
		list.setOnItemClickListener(null);
	}

}
