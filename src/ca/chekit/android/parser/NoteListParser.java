package ca.chekit.android.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.chekit.android.model.Note;
import ca.chekit.android.util.Utilities;

public class NoteListParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		List<Note> notes = null;
		try {
			String json = Utilities.streamToString(is);
			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				notes = new ArrayList<Note>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Note note = new Note(obj);
					notes.add(note);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notes;
	}

}
