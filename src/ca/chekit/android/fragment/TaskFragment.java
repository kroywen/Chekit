package ca.chekit.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.storage.Settings;

public class TaskFragment extends Fragment {
	
	protected long taskId;
	protected Settings settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Bundle args = getArguments();
		if (args != null) {
			taskId = args.getLong(ApiData.PARAM_ID);
		}
		settings = new Settings(getActivity());
	}

	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {}
	
	protected long getTaskId() {
		return taskId;
	}

}
