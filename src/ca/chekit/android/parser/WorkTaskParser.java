package ca.chekit.android.parser;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.util.Utilities;

public class WorkTaskParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		WorkTask task = null;
		try {
			String json = Utilities.streamToString(is);
			JSONObject obj = new JSONObject(json);
			task = new WorkTask(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return task;
	}

}
