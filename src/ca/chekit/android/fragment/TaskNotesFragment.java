package ca.chekit.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import ca.chekit.android.R;
import ca.chekit.android.adapter.NotesAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Note;
import ca.chekit.android.screen.EditNoteScreen;
import ca.chekit.android.screen.NewNoteScreen;
import ca.chekit.android.screen.TaskDetailsScreen;
import ca.chekit.android.util.Utilities;

public class TaskNotesFragment extends TaskFragment implements OnItemClickListener {
	
	public static final int EDIT_NOTE_REQUEST_CODE = 0;
	public static final int NEW_NOTE_REQUEST_CODE = 1;
	
	private ListView list;
	private TextView empty;
	
	private NotesAdapter adapter;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.task_notes_fragment, null);
		initializeViews(rootView);
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (isTaskNotesLoaded()) {
			updateViews();
		} else {
			loadTaskNotes();
		}
	}
	
	private void initializeViews(View rootView) {
		list = (ListView) rootView.findViewById(R.id.list);
		list.setOnItemClickListener(this);
		empty = (TextView) rootView.findViewById(R.id.empty);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Note note = adapter.getItem(position);
		Intent intent = new Intent(getActivity(), EditNoteScreen.class);
		intent.putExtra(ApiData.PARAM_ID, note.getWorkTaskId());
		intent.putExtra(ApiData.PARAM_ID1, note.getId());				
		intent.putExtra("mode", EditNoteScreen.MODE_VIEW);
		startActivity(intent);
	}
	
	private void updateViews() {
		List<Note> notes = getTaskNotes();
		if (Utilities.isEmpty(notes)) {
			list.setVisibility(View.INVISIBLE);
			empty.setVisibility(View.VISIBLE);
		} else {
			adapter = new NotesAdapter(this, notes);
			list.setAdapter(adapter);
			empty.setVisibility(View.INVISIBLE);
			list.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.task_notes_fragment_actions, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.action_new_note:
        	Intent intent = new Intent(getActivity(), NewNoteScreen.class);
        	intent.putExtra(ApiData.PARAM_ID, taskId);
        	startActivityForResult(intent, NEW_NOTE_REQUEST_CODE);
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private List<Note> getTaskNotes() {
		return ((TaskDetailsScreen) getActivity()).getTaskNotes();
	}
	
	private void setTaskNotes(List<Note> notes) {
		((TaskDetailsScreen) getActivity()).setTaskNotes(notes);
	}
	
	private boolean isTaskNotesLoaded() {
		return ((TaskDetailsScreen) getActivity()).isTaskNotesLoaded();
	}
	
	private void setTaskNotesLoaded(boolean taskNotesLoaded) {
		((TaskDetailsScreen) getActivity()).setTaskNotesLoaded(taskNotesLoaded);
	}
	
	private void loadTaskNotes() {
		((TaskDetailsScreen) getActivity()).loadTaskNotes();
	}
	
	public void deleteTaskNote(Note note) {
		((TaskDetailsScreen) getActivity()).deleteTaskNote(note);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_NOTES.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							List<Note> notes = (ArrayList<Note>) apiResponse.getData();
							setTaskNotes(notes);
							setTaskNotesLoaded(true);
							updateViews();
						}
					}
				} else if (ApiData.COMMAND_NOTE.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_DELETE.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_NO_CONTENT) {
							loadTaskNotes();
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_NOTE_REQUEST_CODE || requestCode == NEW_NOTE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				loadTaskNotes();
			}
		}
	}

}
