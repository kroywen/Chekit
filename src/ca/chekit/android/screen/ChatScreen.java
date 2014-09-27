package ca.chekit.android.screen;

import ca.chekit.android.R;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.fragment.TaskChatFragment;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

public class ChatScreen extends BaseScreen {
	
	private TaskChatFragment fragment; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_screen);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.work_tasks_icon);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		fragment = new TaskChatFragment();
		ft.add(R.id.content, fragment).commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (fragment != null && fragment.isAdded()) {
			fragment.onApiResponse(apiStatus, apiResponse);
		}
	}

}
