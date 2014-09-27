package ca.chekit.android.parser;

import android.text.TextUtils;
import ca.chekit.android.api.ApiData;

public class ParserFactory {
	
	public static ApiParser getParser(String command, String method) {
		if (TextUtils.isEmpty(command)) {
			return null;
		} else if (ApiData.COMMAND_PUSH_SERVICE.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
				return new StringParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_LOGIN.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
				return new LoginParser();
			} else if (method.equalsIgnoreCase(ApiData.METHOD_DELETE)) {
				return new SimpleParser();
			} else {
				return null;
			}
		} else if (ApiData.COMMAND_ACCOUNT.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new AccountParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_PASSWORD_RECOVERY.equalsIgnoreCase(command)) {
			return new SimpleParser();
		} else if (ApiData.COMMAND_CHANGE_PASSWORD.equalsIgnoreCase(command)) {
			return new SimpleParser();
		} else if (ApiData.COMMAND_WORKTASKS.equalsIgnoreCase(command) ||
			ApiData.COMMAND_CONTACT_WORKTASKS.equalsIgnoreCase(command)) 
		{
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new WorkTaskListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_DIVISIONS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new DivisionListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_WORKTASK.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new WorkTaskParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_WORKTASK_STATUS.equalsIgnoreCase(command)) {
			return new SimpleParser();
		} else if (ApiData.COMMAND_NOTES.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new NoteListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_NOTE.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new NoteParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_ATTACHMENTS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new AttachmentListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_ATTACHMENT.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new AttachmentParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_CHAT.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new ChatMessageListParser();
			} else if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
				return new ChatMessageParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_CONTACTS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new ContactListParser();
			} else {
				return new SimpleParser();
			}
		} else {
			return null;
		}
	}

}
