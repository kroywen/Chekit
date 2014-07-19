package ca.chekit.android.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.chekit.android.model.Photo;
import ca.chekit.android.util.Utilities;

public class PhotoListParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		List<Photo> photos = null;
		try {
			String json = Utilities.streamToString(is);
			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				photos = new ArrayList<Photo>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Photo photo = new Photo(obj);
					photos.add(photo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return photos;
	}

}
