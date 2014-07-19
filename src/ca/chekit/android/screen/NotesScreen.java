package ca.chekit.android.screen;

import java.util.List;

import org.apache.http.HttpStatus;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.adapter.NotesAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.dialog.ConfirmationDialog;
import ca.chekit.android.model.Note;
import ca.chekit.android.util.Utilities;

public class NotesScreen extends BaseScreen {
	
	public static final int EDIT_NOTE_REQUEST_CODE = 0;
	public static final int NEW_NOTE_REQUEST_CODE = 1;
	
	private long taskId;
	
	private ListView list;
	private TextView empty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_screen);
		getIntentData();
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.notes_icon_big);
		
		if (Utilities.isConnectionAvailable(this)) {
			requestNotes();
		} else {
			showConnectionErrorDialog();
		}
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
		}
	}
	
	private void initializeViews() {
		list = (ListView) findViewById(R.id.list);
		empty = (TextView) findViewById(R.id.empty);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.notes_screen_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
        case R.id.action_new_note:
        	Intent intent = new Intent(this, NewNoteScreen.class);
        	intent.putExtra(ApiData.PARAM_ID, taskId);
        	startActivityForResult(intent, NEW_NOTE_REQUEST_CODE);
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void requestNotes() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_NOTES);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
		intent.putExtra(ApiData.PARAM_ID, taskId);
		startService(intent);
		showProgressDialog(R.string.loading_notes);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_NOTES.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							List<Note> notes = (List<Note>) apiResponse.getData();
							updateViews(notes);
						}
					}
				} else if (ApiData.COMMAND_NOTE.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_DELETE.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_NO_CONTENT) {
							requestNotes();
						}
					}
				}
			}
		}
	}
	
	private void updateViews(List<Note> notes) {
		if (Utilities.isEmpty(notes)) {
			list.setVisibility(View.INVISIBLE);
			empty.setVisibility(View.VISIBLE);
		} else {
			NotesAdapter adapter = new NotesAdapter(this, notes);
			list.setAdapter(adapter);
			empty.setVisibility(View.INVISIBLE);
			list.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_NOTE_REQUEST_CODE || requestCode == NEW_NOTE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				requestNotes();
			}
		}
	}
	
	public void deleteNote(final Note note) {
		if (Utilities.isConnectionAvailable(this)) {
			final ConfirmationDialog dialog = new ConfirmationDialog();
			dialog.setText(getString(R.string.confirm_delete_note));
			dialog.setOkListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
					Intent intent = new Intent(NotesScreen.this, ApiService.class);
					intent.setAction(ApiData.COMMAND_NOTE);
					intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_DELETE);
					intent.putExtra(ApiData.PARAM_ID, note.getWorkTaskId());
					intent.putExtra(ApiData.PARAM_ID1, note.getId());
					startService(intent);
					showProgressDialog(R.string.deleting_note);
				}
			});
			dialog.show(getFragmentManager(), "DeleteNoteDialog");
		} else {
			showConnectionErrorDialog();
		}
	}

}
