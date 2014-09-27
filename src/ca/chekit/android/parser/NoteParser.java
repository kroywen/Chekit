package ca.chekit.android.parser;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import ca.chekit.android.model.Note;
import ca.chekit.android.util.Utilities;

public class NoteParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		Note note = null;
		try {
			String json = Utilities.streamToString(is);
			JSONObject obj = new JSONObject(json);
			note = new Note(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return note;
	}

}
