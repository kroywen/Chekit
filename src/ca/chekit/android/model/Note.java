package ca.chekit.android.model;

import org.json.JSONObject;

public class Note {
	
	public static final String ID = "Id";
	public static final String WORKTASK_ID = "WorkTaskId";
	public static final String NOTE = "Note";
	public static final String DATE_CREATED = "DateCreated";
	public static final String DATE_CHANGED = "DateChanged";
	public static final String IS_CREATED_BY_MOBILE_APP = "IsCreatedByMobileApp";
	
	private long id;
	private long workTaskId;
	private String note;
	private String dateCreated;
	private String dateChanged;
	private boolean isCreatedByMobileApp;
	
	public Note() {}
	
	public Note(JSONObject obj) {
		this.id = obj.optLong(ID);
		this.workTaskId = obj.optLong(WORKTASK_ID);
		this.note = obj.optString(NOTE);
		this.dateCreated = obj.optString(DATE_CREATED);
		this.dateChanged = obj.optString(DATE_CHANGED);
		this.isCreatedByMobileApp = obj.optBoolean(IS_CREATED_BY_MOBILE_APP);
	}

	public Note(long id, long workTaskId, String note, String dateCreated,
			String dateChanged, boolean isCreatedByMobileApp) 
	{
		this.id = id;
		this.workTaskId = workTaskId;
		this.note = note;
		this.dateCreated = dateCreated;
		this.dateChanged = dateChanged;
		this.isCreatedByMobileApp = isCreatedByMobileApp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getWorkTaskId() {
		return workTaskId;
	}

	public void setWorkTaskId(long workTaskId) {
		this.workTaskId = workTaskId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(String dateChanged) {
		this.dateChanged = dateChanged;
	}

	public boolean isCreatedByMobileApp() {
		return isCreatedByMobileApp;
	}

	public void setCreatedByMobileApp(boolean isCreatedByMobileApp) {
		this.isCreatedByMobileApp = isCreatedByMobileApp;
	}

}
