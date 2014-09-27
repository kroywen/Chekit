package ca.chekit.android.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.util.Utilities;

public class ChatMessageListParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		List<ChatMessage> messages = null;
		try {
			String json = Utilities.streamToString(is);
			Log.d("ApiResponse", json);
			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				messages = new ArrayList<ChatMessage>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					ChatMessage message = new ChatMessage(obj);
					messages.add(message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages;
	}

}
