package ca.chekit.android.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import ca.chekit.android.model.Attachment;
import ca.chekit.android.util.Utilities;

public class AttachmentListParser extends ApiParser {
	
	@Override
	public Object readData(Context context, InputStream is) {
		List<Attachment> attachments = null;
		try {
			String json = Utilities.streamToString(is);
			Log.d("ApiResponse", json);
			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				attachments = new ArrayList<Attachment>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Attachment attachment = new Attachment(obj);
					attachments.add(attachment);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachments;
	}

}
