package ca.chekit.android.model;

import java.io.Serializable;
import java.util.Comparator;

import org.json.JSONObject;

import ca.chekit.android.util.Utilities;

public class WorkTask implements Serializable {
	
	private static final long serialVersionUID = 7781940033587407383L;
	
	public static final String ID = "Id";
	public static final String DESCRIPTION = "Description";
	public static final String DURATION = "Duration";
	public static final String DUE_DATE = "DueDate";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	public static final String IS_ACCEPTED = "IsAccepted";
	public static final String WORK_STATUS = "WorkStatus";
	public static final String WORK_STATUS_LATITUDE = "WorkStatusLatitude";
	public static final String WORK_STATUS_LONGITUDE = "WorkStatusLongitude";
	public static final String SCHEDULED_STATUS = "ScheduledStatus";
	public static final String ASSIGNEE_ID = "AssigneeId";
	public static final String DIVISION_ID = "DivisionId";
	public static final String SCHEDULER_ID = "SchedulerId";
	public static final String NOTES_NUMBER = "NotesNumber";
	public static final String PHOTOS_NUMBER = "PhotosNumber";
	public static final String WORK_STATUS_ACCEPTED = "WorkStatusAccepted";
	public static final String WORK_STATUS_CHANGED = "WorkStatusChanged";
	
	private long id;
	private String description;
	private long duration;
	private String dueDate;
	private double latitude;
	private double longitude;
	private boolean isAccepted;
	private WorkStatus workStatus;
	private double workStatusLatitude;
	private double workStatusLongitude;
	private ScheduledStatus scheduledStatus;
	private long assigneeId;
	private long divisionId;
	private long schedulerId;
	private int notesNumber;
	private int photosNumber;
	private String workStatusAccepted;
	private String workStatusChanged;
	
	private long dueDateMillis;
	
	public WorkTask() {}
	
	public WorkTask(JSONObject obj) {
		this.id = obj.optLong(ID);
		this.description = obj.optString(DESCRIPTION);
		this.duration = obj.optLong(DURATION);
		this.dueDate = obj.optString(DUE_DATE);
		setDueDateMillis();
		this.latitude = "null".equals(obj.optString(LATITUDE)) ? 0 : obj.optDouble(LATITUDE);
		this.longitude = "null".equals(obj.optString(LONGITUDE)) ? 0 : obj.optDouble(LONGITUDE);
		this.isAccepted = obj.optBoolean(IS_ACCEPTED);
		this.workStatus = WorkStatus.valueOf(obj.optString(WORK_STATUS));
		this.workStatusLatitude = "null".equals(obj.optString(WORK_STATUS_LATITUDE)) ? 0 : obj.optDouble(WORK_STATUS_LATITUDE);
		this.workStatusLongitude = "null".equals(obj.optString(WORK_STATUS_LONGITUDE)) ? 0 : obj.optDouble(WORK_STATUS_LONGITUDE);
		this.scheduledStatus = ScheduledStatus.valueOf(obj.optString(SCHEDULED_STATUS));
		this.assigneeId = obj.optLong(ASSIGNEE_ID);
		this.divisionId = obj.optLong(DIVISION_ID);
		this.schedulerId = obj.optLong(SCHEDULER_ID);
		this.notesNumber = obj.optInt(NOTES_NUMBER);
		this.photosNumber = obj.optInt(PHOTOS_NUMBER);
		this.workStatusAccepted = obj.optString(WORK_STATUS_ACCEPTED);
		this.workStatusChanged = obj.optString(WORK_STATUS_CHANGED);
	}
	
	public WorkTask(long id, String description, long duration,
			String dueDate, double latitude, double longitude,
			boolean isAccepted, WorkStatus workStatus,
			double workStatusLatitude, double workStatusLongitude,
			ScheduledStatus scheduledStatus, long assigneeId, long divisionId,
			long schedulerId, int notesNumber, int photosNumber,
			String workStatusAccepted, String workStatusChanged) 
	{
		this.id = id;
		this.description = description;
		this.duration = duration;
		this.dueDate = dueDate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isAccepted = isAccepted;
		this.workStatus = workStatus;
		this.workStatusLatitude = workStatusLatitude;
		this.workStatusLongitude = workStatusLongitude;
		this.scheduledStatus = scheduledStatus;
		this.assigneeId = assigneeId;
		this.divisionId = divisionId;
		this.schedulerId = schedulerId;
		this.notesNumber = notesNumber;
		this.photosNumber = photosNumber;
		this.workStatusAccepted = workStatusAccepted;
		this.workStatusChanged = workStatusChanged;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String getHumanReadableDuration() {
		if (duration == 0) {
			return "0 m";
		}
		
		int d = (int) duration /(60 * 24);
	    int remainderDays = (int) (duration % (60 * 24));
	    int h = remainderDays / 60;
	    int m = remainderDays % 60;
	    
	    String text = "";
	    if (m > 0) {
	    	text = m + " m" + text; 
	    }
	    if (h > 0) {
	    	text = h + " h " + text;
	    }
	    if (d > 0) {
	    	text = d + " d " + text;
	    }
	    return text;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}

	public double getWorkStatusLatitude() {
		return workStatusLatitude;
	}

	public void setWorkStatusLatitude(double workStatusLatitude) {
		this.workStatusLatitude = workStatusLatitude;
	}

	public double getWorkStatusLongitude() {
		return workStatusLongitude;
	}

	public void setWorkStatusLongitude(double workStatusLongitude) {
		this.workStatusLongitude = workStatusLongitude;
	}

	public ScheduledStatus getScheduledStatus() {
		return scheduledStatus;
	}

	public void setScheduledStatus(ScheduledStatus scheduledStatus) {
		this.scheduledStatus = scheduledStatus;
	}

	public long getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(long assigneeId) {
		this.assigneeId = assigneeId;
	}

	public long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(long divisionId) {
		this.divisionId = divisionId;
	}

	public long getSchedulerId() {
		return schedulerId;
	}

	public void setSchedulerId(long schedulerId) {
		this.schedulerId = schedulerId;
	}

	public int getNotesNumber() {
		return notesNumber;
	}

	public void setNotesNumber(int notesNumber) {
		this.notesNumber = notesNumber;
	}

	public int getPhotosNumber() {
		return photosNumber;
	}

	public void setPhotosNumber(int photosNumber) {
		this.photosNumber = photosNumber;
	}

	public String getWorkStatusAccepted() {
		return workStatusAccepted;
	}

	public void setWorkStatusAccepted(String workStatusAccepted) {
		this.workStatusAccepted = workStatusAccepted;
	}

	public String getWorkStatusChanged() {
		return workStatusChanged;
	}

	public void setWorkStatusChanged(String workStatusChanged) {
		this.workStatusChanged = workStatusChanged;
	}
	
	public boolean hasCoordinates() {
		return latitude != 0 && longitude != 0;
	}
	
	public boolean hasWorkStatusCoordinates() {
		return workStatusLatitude != 0 && workStatusLongitude != 0;
	}
	
	private void setDueDateMillis() {
		try {
			dueDateMillis = Utilities.parseDate(dueDate, Utilities.yyyy_MM_ddTHH_mm_ss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Comparator<WorkTask> DueDateComparator = new Comparator<WorkTask>() {
		@Override
		public int compare(WorkTask lhs, WorkTask rhs) {
			if (lhs.dueDateMillis < rhs.dueDateMillis) {
				return -1;
			} else if (lhs.dueDateMillis > rhs.dueDateMillis) {
				return 1;
			} else {
				return 0;
			}
		};
	};
	
	public static Comparator<WorkTask> DivisionComparator = new Comparator<WorkTask>() {
		@Override
		public int compare(WorkTask lhs, WorkTask rhs) {
			if (lhs.divisionId < rhs.divisionId) {
				return -1;
			} else if (lhs.divisionId > rhs.divisionId) {
				return 1;
			} else {
				return 0;
			}
		};
	};
	
	public static Comparator<WorkTask> StatusComparator = new Comparator<WorkTask>() {
		@Override
		public int compare(WorkTask lhs, WorkTask rhs) {
			if (lhs.getWorkStatus().ordinal() < rhs.getWorkStatus().ordinal()) {
				return -1;
			} else if (lhs.getWorkStatus().ordinal() > rhs.getWorkStatus().ordinal()) {
				return 1;
			} else {
				return 0;
			}
		};
	};

}
