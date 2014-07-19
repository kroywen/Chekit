package ca.chekit.android.api;

public class ApiData {
	
	public static final String BASE_URL = "http://chekit.cloudapp.net:8080/api/v1/";
	
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";
	
	public static final String COMMAND_LOGIN = "account/login";
	public static final String COMMAND_PASSWORD_RECOVERY = "account/passwordrecovery";
	public static final String COMMAND_WORKTASKS = "worktasks";
	public static final String COMMAND_WORKTASK_SCHEDULED_STATUS = "worktasks/%d/scheduledstatus";
	public static final String COMMAND_WORKTASK = "worktasks/%d";
	public static final String COMMAND_WORKTASK_STATUS = "worktasks/%d/workstatus";
	public static final String COMMAND_NOTES = "worktasks/%d/notes";
	public static final String COMMAND_NOTE = "worktasks/%d/notes/%d";
	public static final String COMMAND_PHOTOS = "worktasks/%d/photos";
	public static final String COMMAND_PHOTO = "worktasks/%d/photos/%d";
	
	public static final String PARAM_ID = "id";
	public static final String PARAM_ID1 = "id1";
	public static final String PARAM_METHOD = "method";
	public static final String PARAM_BODY = "body";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";

}
