package ca.chekit.android.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import ca.chekit.android.model.Division;
import ca.chekit.android.util.Utilities;

public class DivisionListParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		List<Division> divisions = null;
		try {
			String json = Utilities.streamToString(is);
			Log.d("ApiResponse", json);
			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				divisions = new ArrayList<Division>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Division division = new Division(obj);
					divisions.add(division);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return divisions;
	}

}
