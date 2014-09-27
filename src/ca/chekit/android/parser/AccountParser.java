package ca.chekit.android.parser;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import ca.chekit.android.model.Account;
import ca.chekit.android.util.Utilities;

public class AccountParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		Account account = null;
		try {
			String json = Utilities.streamToString(is);
			JSONObject obj = new JSONObject(json);
			account = new Account(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return account;
	}

}
