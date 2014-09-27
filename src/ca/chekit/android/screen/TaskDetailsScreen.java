package ca.chekit.android.screen;

import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.dialog.ConfirmationDialog;
import ca.chekit.android.fragment.TaskAttachmentFragment;
import ca.chekit.android.fragment.TaskChatFragment;
import ca.chekit.android.fragment.TaskDetailsFragment;
import ca.chekit.android.fragment.TaskFragment;
import ca.chekit.android.fragment.TaskNotesFragment;
import ca.chekit.android.fragment.TaskPhotosFragment;
import ca.chekit.android.model.Attachment;
import ca.chekit.android.model.Note;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.util.Utilities;

public class TaskDetailsScreen extends BaseScreen {
	
	private long taskId;
	private String taskDescription;
	
	private WorkTask task;
	private List<Attachment> photos;
	private List<Note> notes;
	private List<Attachment> attachments;
	
	private boolean taskDetailsLoaded;
	private boolean taskPhotosLoaded;
	private boolean taskNotesLoaded;
	private boolean taskAttachmentsLoaded;
	
	private TaskFragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_details_screen);
		getIntentData();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(taskDescription);
		actionBar.setIcon(R.drawable.task_icon);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				selectItem(tab.getPosition());
			}
			@Override
	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
			@Override
	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
	    };
	    
	    actionBar.addTab(actionBar.newTab().setIcon(R.drawable.details_icon).setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab().setIcon(R.drawable.photos_icon_big).setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab().setIcon(R.drawable.notes_icon_big).setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab().setIcon(R.drawable.files_icon_big).setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab().setIcon(R.drawable.chat_icon_big).setTabListener(tabListener));
	    
	    actionBar.setSelectedNavigationItem(0);
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
			taskDescription = intent.getStringExtra(ApiData.PARAM_DESCRIPTION);
		}
	}
	
	private void selectItem(int position) {
		fragment = getTaskFragment(position);
		Bundle args = new Bundle();
		args.putLong(ApiData.PARAM_ID, taskId);
		fragment.setArguments(args);
		FragmentTransaction t = getFragmentManager().beginTransaction();
		t.replace(R.id.content, fragment).commit();
	}
	
	private TaskFragment getTaskFragment(int position) {
		switch (position) {
		case 0:	return new TaskDetailsFragment();
		case 1: return new TaskPhotosFragment();
		case 2: return new TaskNotesFragment();
		case 3: return new TaskAttachmentFragment();
		case 4: return new TaskChatFragment();
		default:
			return null;
		}
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
	
	public void loadTaskDetails() {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASK);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			startService(intent);
			showProgressDialog(R.string.loading_task);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public boolean isTaskDetailsLoaded() {
		return taskDetailsLoaded;
	}
	
	public void setTaskDetailsLoaded(boolean taskDetailsLoaded) {
		this.taskDetailsLoaded = taskDetailsLoaded;
	}
	
	public WorkTask getTaskDetails() {
		return task;
	}
	
	public void setTaskDetails(WorkTask task) {
		this.task = task;
	}
	
	public void loadTaskPhotos() {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_ATTACHMENTS);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			intent.putExtra(ApiData.PARAM_FILTER, "startswith(MimeType, 'image')");
			startService(intent);
			showProgressDialog(R.string.loading_photos);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public boolean isTaskPhotosLoaded() {
		return taskPhotosLoaded;
	}
	
	public void setTaskPhotosLoaded(boolean taskPhotosLoaded) {
		this.taskPhotosLoaded = taskPhotosLoaded;
	}
	
	public List<Attachment> getTaskPhotos() {
		return photos;
	}
	
	public void setTaskPhotos(List<Attachment> photos) {
		this.photos = photos;
	}
	
	public void loadTaskNotes() {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_NOTES);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			startService(intent);
			showProgressDialog(R.string.loading_notes);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public boolean isTaskNotesLoaded() {
		return taskNotesLoaded;
	}
	
	public void setTaskNotesLoaded(boolean taskNotesLoaded) {
		this.taskNotesLoaded = taskNotesLoaded;
	}
	
	public List<Note> getTaskNotes() {
		return notes;
	}
	
	public void setTaskNotes(List<Note> notes) {
		this.notes = notes;
	}
	
	public void deleteTaskNote(final Note note) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.confirm_delete_note));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (Utilities.isConnectionAvailable(TaskDetailsScreen.this)) {
					Intent intent = new Intent(TaskDetailsScreen.this, ApiService.class);
					intent.setAction(ApiData.COMMAND_NOTE);
					intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_DELETE);
					intent.putExtra(ApiData.PARAM_ID, note.getWorkTaskId());
					intent.putExtra(ApiData.PARAM_ID1, note.getId());
					startService(intent);
					showProgressDialog(R.string.deleting_note);
				} else {
					showConnectionErrorDialog();
				}
			}
		});
		dialog.show(getFragmentManager(), "DeleteNoteDialog");
	}
	
	public void loadTaskAttachments() {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_ATTACHMENTS);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			intent.putExtra(ApiData.PARAM_FILTER, "startswith(MimeType, 'application/pdf')");
			startService(intent);
			showProgressDialog(R.string.loading_files);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public boolean isTaskAttachmentsLoaded() {
		return taskAttachmentsLoaded;
	}
	
	public void setTaskAttachmentsLoaded(boolean taskAttachmentsLoaded) {
		this.taskAttachmentsLoaded = taskAttachmentsLoaded;
	}
	
	public List<Attachment> getTaskAttachments() {
		return attachments;
	}
	
	public void setTaskAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	
	public void clearRemainingTime() {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.confirm_clear_remaining_time));
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				requestUpdateRemainingTime(0);
			}
		});
		dialog.show(getFragmentManager(), "DeleteNoteDialog");
	}
	
	public void requestUpdateRemainingTime(long remainingTime) {
		if (Utilities.isConnectionAvailable(this)) {
			try {
				Intent intent = new Intent(this, ApiService.class);
				intent.setAction(ApiData.COMMAND_WORKTASK);
				intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
				intent.putExtra(ApiData.PARAM_ID, taskId);
				
				JSONObject obj = new JSONObject();
				obj.put(WorkTask.REMAINING, remainingTime);
				intent.putExtra(ApiData.PARAM_BODY, obj.toString());
				
				startService(intent);
				showProgressDialog(R.string.updating_task);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			showConnectionErrorDialog();
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
