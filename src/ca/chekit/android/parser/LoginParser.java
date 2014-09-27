package ca.chekit.android.parser;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import ca.chekit.android.util.Utilities;

public class LoginParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		try {
			String json = Utilities.streamToString(is);
			Log.d("ApiResponse", json);
			JSONObject obj = new JSONObject(json);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
