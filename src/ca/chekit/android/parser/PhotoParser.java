package ca.chekit.android.parser;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PhotoParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		}
		return bitmap;
	}

}
