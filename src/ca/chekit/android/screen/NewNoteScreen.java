package ca.chekit.android.screen;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Note;
import ca.chekit.android.util.Utilities;

public class NewNoteScreen extends BaseScreen {
	
	private EditText noteView;
	
	private long taskId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note_screen);
		getIntentData();
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ic_menu_new_note);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.new_notes_screen_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
        case R.id.action_save:
        	hideSoftKeyborad();
        	if (Utilities.isConnectionAvailable(this)) {
        		saveNote();
        	} else {
        		showConnectionErrorDialog();
        	}
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
		}
	}
	
	private void initializeViews() {
		noteView = (EditText) findViewById(R.id.noteView);
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_NOTES.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_CREATED) {
							setResult(RESULT_OK);
							finish();
						}
					}
				}
			}
		}
	}
	
	private void saveNote() {
		try {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_NOTES);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_POST);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			
			JSONObject obj = new JSONObject();
			obj.put(Note.NOTE, noteView.getText().toString().trim());
			obj.put(Note.IS_CREATED_BY_MOBILE_APP, true);
			intent.putExtra(ApiData.PARAM_BODY, obj.toString());
			
			startService(intent);
			showProgressDialog(R.string.adding_note);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
