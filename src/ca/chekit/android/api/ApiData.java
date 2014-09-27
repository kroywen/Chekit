package ca.chekit.android.api;

public class ApiData {
	
	public static final String BASE_URL = "http://chekitdev.cloudapp.net:8080/api/v1/";
	
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";
	
	public static final String COMMAND_PUSH_SERVICE = "pushservice";
	public static final String COMMAND_LOGIN = "account/login";
	public static final String COMMAND_ACCOUNT = "account";
	public static final String COMMAND_PASSWORD_RECOVERY = "account/passwordrecovery";
	public static final String COMMAND_CHANGE_PASSWORD = "account/password";
	public static final String COMMAND_DIVISIONS = "contacts/%d/divisions";
	public static final String COMMAND_WORKTASKS = "worktasks";
	public static final String COMMAND_CONTACT_WORKTASKS = "contacts/%d/worktasks";
	public static final String COMMAND_WORKTASK_SCHEDULED_STATUS = "worktasks/%d/scheduledstatus";
	public static final String COMMAND_WORKTASK = "worktasks/%d";
	public static final String COMMAND_WORKTASK_STATUS = "worktasks/%d/workstatus";
	public static final String COMMAND_NOTES = "worktasks/%d/notes";
	public static final String COMMAND_ATTACHMENTS = "worktasks/%d/files";
	public static final String COMMAND_ATTACHMENT = "worktasks/%d/files/%d";
	public static final String COMMAND_NOTE = "worktasks/%d/notes/%d";
	public static final String COMMAND_CHAT = "chat";
	public static final String COMMAND_CHAT_MESSAGE_READ = "chat/%d/read";
	public static final String COMMAND_CONTACTS = "contacts";
	public static final String COMMAND_CONTACT_PHOTO = "contacts/%d/photo";
	
	public static final String PARAM_ID = "id";
	public static final String PARAM_ID1 = "id1";
	public static final String PARAM_METHOD = "method";
	public static final String PARAM_BODY = "body";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_DESCRIPTION = "description";
	public static final String PARAM_FILTER = "$filter";
	public static final String PARAM_OLD_PASSWORD = "oldpassword";
	public static final String PARAM_NEW_PASSWORD = "newpassword";
	public static final String PARAM_CONTACT_ID = "ContactId";
	public static final String PARAM_DEVICE_ID = "DeviceId";
	public static final String PARAM_PUSH_TOKEN = "PushToken";

}
