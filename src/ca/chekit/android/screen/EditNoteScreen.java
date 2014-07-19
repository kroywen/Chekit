package ca.chekit.android.screen;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Note;
import ca.chekit.android.util.Utilities;

public class EditNoteScreen extends BaseScreen {
	
	private TextView dateView;
	private TextView timeView;
	private EditText noteView;
	
	private long taskId;
	private long noteId;
	private Note note;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note_screen);
		getIntentData();
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.notes_icon_big);
		
		if (Utilities.isConnectionAvailable(this)) {
			requestNote();
		} else {
			showConnectionErrorDialog();
		}
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
        		updateNote();
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
			noteId = intent.getLongExtra(ApiData.PARAM_ID1, 0);
		}
	}
	
	private void initializeViews() {
		dateView = (TextView) findViewById(R.id.dateView);
		timeView = (TextView) findViewById(R.id.timeView);
		noteView = (EditText) findViewById(R.id.noteView);
	}
	
	private void requestNote() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_NOTE);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
		intent.putExtra(ApiData.PARAM_ID, taskId);
		intent.putExtra(ApiData.PARAM_ID1, noteId);
		startService(intent);
		showProgressDialog(R.string.loading_note);
	}
	
	private void updateViews() {
		if (note != null) {
			dateView.setText(Utilities.convertDate(note.getDateChanged(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MM_dd));
			timeView.setText(Utilities.convertDate(note.getDateChanged(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.hh_mm_a));
			noteView.setText(note.getNote());
			if (!TextUtils.isEmpty(note.getNote())) {
				noteView.setSelection(note.getNote().length());
			}
		}
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_NOTE.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							note = (Note) apiResponse.getData();
							updateViews();
						}
					} else if (ApiData.METHOD_PUT.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_NO_CONTENT) {
							setResult(RESULT_OK);
							finish();
						}
					}
				}
			}
		}
	}
	
	private void updateNote() {
		try {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_NOTE);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			intent.putExtra(ApiData.PARAM_ID1, noteId);
			
			JSONObject obj = new JSONObject();
			obj.put(Note.NOTE, noteView.getText().toString().trim());
			intent.putExtra(ApiData.PARAM_BODY, obj.toString());
			
			startService(intent);
			showProgressDialog(R.string.updating_note);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
