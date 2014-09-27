package ca.chekit.android.parser;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AttachmentParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		Bitmap bitmap = null; // assume that we call this method only for photos
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		}
		return bitmap;
	}

}
