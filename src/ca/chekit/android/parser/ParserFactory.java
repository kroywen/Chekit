package ca.chekit.android.parser;

import android.text.TextUtils;
import ca.chekit.android.api.ApiData;

public class ParserFactory {
	
	public static ApiParser getParser(String command, String method) {
		if (TextUtils.isEmpty(command)) {
			return null;
		} else if (ApiData.COMMAND_LOGIN.equalsIgnoreCase(command)) {
			if (method.equalsIgnoreCase(ApiData.METHOD_POST)) {
				return new StringParser();
			} else if (method.equalsIgnoreCase(ApiData.METHOD_DELETE)) {
				return new SimpleParser();
			} else {
				return null;
			}
		} else if (ApiData.COMMAND_PASSWORD_RECOVERY.equalsIgnoreCase(command)) {
			return new SimpleParser();
		} else if (ApiData.COMMAND_WORKTASKS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new WorkTaskListParser();
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
		} else if (ApiData.COMMAND_PHOTOS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new PhotoListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_PHOTO.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new PhotoParser();
			} else {
				return new SimpleParser();
			}
		} else {
			return null;
		}
	}

}
