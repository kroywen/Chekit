package ca.chekit.android.parser;

import java.io.InputStream;

import android.graphics.BitmapFactory;

public class PhotoParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

}
