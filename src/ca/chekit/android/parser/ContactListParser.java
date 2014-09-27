package ca.chekit.android.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import ca.chekit.android.model.Account;
import ca.chekit.android.util.Utilities;

public class ContactListParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		List<Account> contacts = null;
		try {
			String json = Utilities.streamToString(is);
			Log.d("ApiResponse", json);
			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				contacts = new ArrayList<Account>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Account contact = new Account(obj);
					contacts.add(contact);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contacts;
	}

}
