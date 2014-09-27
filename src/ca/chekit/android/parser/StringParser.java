package ca.chekit.android.parser;

import java.io.InputStream;

import android.content.Context;
import ca.chekit.android.util.Utilities;

public class StringParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		return Utilities.streamToString(is);
	}

}
