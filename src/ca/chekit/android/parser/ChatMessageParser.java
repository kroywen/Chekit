package ca.chekit.android.parser;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.util.Utilities;

public class ChatMessageParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		ChatMessage message = null;
		try {
			String json = Utilities.streamToString(is);
			JSONObject obj = new JSONObject(json);
			message = new ChatMessage(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

}
