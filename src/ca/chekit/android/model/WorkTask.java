package ca.chekit.android.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;
import ca.chekit.android.util.Utilities;

public class WorkTask implements Serializable {
	
	private static final long serialVersionUID = 7781940033587407383L;
	
	public static final int GROUP_BY_DUE_DATE = 0;
	public static final int GROUP_ON_DIVISION = 1;
	public static final int GROUP_ON_STATUS = 2;
	
	public static final String ID = "Id";
	public static final String DESCRIPTION = "Description";
	public static final String DURATION = "Duration";
	public static final String REMAINING = "Remaining";
	public static final String DUE_DATE = "DueDate";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	public static final String ADDRESS = "Address";
	public static final String CITY = "City";
	public static final String PROVINCE_ID = "ProvinceId";
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
	public static final String CONSTRAINING_TASK_ID = "ConstrainingTask_Id";
	public static final String GROUP = "Group";
	
	public static final String PREVIOUS_WORK_STATUS = "PreviousWorkStatus";
	
	private long id;
	private String description;
	private long duration;
	private long remaining;
	private String dueDate;
	private double latitude;
	private double longitude;
	private String address;
	private String city;
	private long provinceId;
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
	private long constrainingTaskId;
	private String group;
	
	private long dueDateMillis;
	
	public WorkTask() {}
	
	public WorkTask(JSONObject obj) {
		this.id = obj.optLong(ID);
		this.description = obj.optString(DESCRIPTION);
		this.duration = obj.optLong(DURATION);
		this.remaining = obj.optLong(REMAINING);
		this.dueDate = obj.optString(DUE_DATE);
		setDueDateMillis();
		this.latitude = "null".equals(obj.optString(LATITUDE)) ? 0 : obj.optDouble(LATITUDE);
		this.longitude = "null".equals(obj.optString(LONGITUDE)) ? 0 : obj.optDouble(LONGITUDE);
		this.address = "null".equalsIgnoreCase(obj.optString(ADDRESS)) ? null : obj.optString(ADDRESS);
		this.city = obj.optString(CITY);
		this.provinceId = obj.optLong(PROVINCE_ID);
		this.isAccepted = obj.optBoolean(IS_ACCEPTED);
		this.workStatus = WorkStatus.forName(obj.optString(WORK_STATUS));
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
		this.constrainingTaskId = obj.optLong(CONSTRAINING_TASK_ID);
		this.group = obj.optString(group);
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
		return Utilities.getHumanReadableTime(duration);
	}
	
	public long getRemaining() {
		return remaining;
	}
	
	public void setRemaining(long remaining) {
		this.remaining = remaining;
	}
	
	public String getHumanReadableRemaining() {
		return Utilities.getHumanReadableTime(remaining);
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
	
	public String getAddress() {
		return address;
	}
	
	public boolean hasAddress() {
		return !TextUtils.isEmpty(address);
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public long getProvinceId() {
		return provinceId;
	}
	
	public void setProvinceId(long provinceId) {
		this.provinceId = provinceId;
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
	
	public boolean hasWorkStatusChanged() {
		return !TextUtils.isEmpty(workStatusChanged);
	}
	
	public boolean hasCoordinates() {
		return latitude != 0 && longitude != 0;
	}
	
	public boolean hasWorkStatusCoordinates() {
		return workStatusLatitude != 0 && workStatusLongitude != 0;
	}
	
	public long getConstrainingTaskId() {
		return constrainingTaskId;
	}
	
	public void setConstrainingTaskId(long constrainingTaskId) {
		this.constrainingTaskId = constrainingTaskId;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	private void setDueDateMillis() {
		try {
			dueDateMillis = Utilities.parseDate(dueDate, Utilities.yyyy_MM_ddTHH_mm_ss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long getDueDateMillis() {
		return dueDateMillis;
	}
	
	public static Comparator<Map.Entry<String, List<WorkTask>>> dueDateComparator = new Comparator<Map.Entry<String, List<WorkTask>>>() {
		@Override
		public int compare(Map.Entry<String, List<WorkTask>> o1, Map.Entry<String, List<WorkTask>> o2) {
			String key1 = o1.getKey();
			String key2 = o2.getKey();
			if (key1.equals("Today") ||
				key1.equals("Tomorrow") && !key2.equals("Today") ||
				key1.equals("Up coming") && !key2.equals("Tomorrow") && !key2.equals("Today"))
			{
				return -1;
			} else {
				return 1; 
			}
		}
	};
	
	public static Comparator<Map.Entry<String, List<WorkTask>>> statusNameComparator = new Comparator<Map.Entry<String, List<WorkTask>>>() {
		@Override
		public int compare(Map.Entry<String, List<WorkTask>> o1, Map.Entry<String, List<WorkTask>> o2) {
			String key1 = o1.getKey();
			String key2 = o2.getKey();
			if (key1.equals(WorkStatus.GREEN) ||
				key1.equals(WorkStatus.YELLOW) && !key2.equals(WorkStatus.GREEN) ||
				key1.equals(WorkStatus.RED) && !key2.equals(WorkStatus.GREEN) && !key2.equals(WorkStatus.YELLOW))
			{
				return -1;
			} else {
				return 1; 
			}
		}
	};
	
	public static Comparator<Map.Entry<String, List<WorkTask>>> divisionNameComparator = new Comparator<Map.Entry<String, List<WorkTask>>>() {
		@Override
		public int compare(Map.Entry<String, List<WorkTask>> o1, Map.Entry<String, List<WorkTask>> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	};

}
