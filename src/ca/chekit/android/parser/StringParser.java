package ca.chekit.android.parser;

import java.io.InputStream;

import ca.chekit.android.util.Utilities;

public class StringParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		return Utilities.streamToString(is);
	}

}
