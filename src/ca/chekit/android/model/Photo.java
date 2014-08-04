package ca.chekit.android.model;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.storage.Settings;

public class Photo {
	
	public static final String ID = "Id";
	public static final String WORKTASK_ID = "WorkTaskId";
	public static final String DATE_CREATED = "DateCreated";
	
	private long id;
	private long workTaskId;
	private String dateCreated;
	
	public Photo() {}
	
	public Photo(JSONObject obj) {
		this.id = obj.optLong(ID);
		this.workTaskId = obj.optLong(WORKTASK_ID);
		this.dateCreated = obj.optString(DATE_CREATED);
	}
	
	public Photo(long id, long workTaskId, String dateCreated) {
		this.id = id;
		this.workTaskId = workTaskId;
		this.dateCreated = dateCreated;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getWorkTaskId() {
		return workTaskId;
	}

	public void setWorkTaskId(long workTaskId) {
		this.workTaskId = workTaskId;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	private Context context;
	private ImageView imageView;
	private ImageView emptyView;
	private Bitmap bitmap = null;
	private boolean loading;
	
	public void displayImage(Context context, ImageView imageView, ImageView emptyView) {
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			if (loading) {
				return;
			}
			loadImage(context, imageView, emptyView);
		}
	}
	
	public void loadImage(Context context, ImageView imageView, ImageView emptyView) {
		this.context = context;
		this.imageView = imageView;
		this.emptyView = emptyView;
		bitmap = null;
		loading = true;
		new LoadImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
	}
	
	public boolean isThumbnailLoaded() {
		return bitmap != null;
	}
	
	class LoadImageTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			emptyView.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			String url = ApiData.BASE_URL + String.format(ApiData.COMMAND_PHOTO, workTaskId, id);
			AndroidHttpClient client = AndroidHttpClient.newInstance(
				System.getProperty("http.agent"), context);
			HttpGet request = new HttpGet(url);
			
			Settings settings = new Settings(context);
			String auth = settings.getString(Settings.AUTH);
			if (!TextUtils.isEmpty(auth)) {
				request.addHeader("Authorization", "Basic " + auth);
			}
			
			HttpResponse response = null;
			try {
				response = client.execute(request);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream is = entity.getContent();
					
					if (imageView == null) {
						return null;
					}
					
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.outWidth = imageView.getWidth();
					options.outHeight = imageView.getHeight();
					
					// Memory may be not enough on old devices, so should handle OutOfMemoryError
					// in order to prevent stopping application
					try {
						bitmap = BitmapFactory.decodeStream(is, null, options);
					} catch (OutOfMemoryError error) {
						error.printStackTrace();
						bitmap = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				client.close();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (imageView == null || emptyView == null) {
				return;
			}
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				emptyView.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.VISIBLE);
			} else {
				emptyView.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.INVISIBLE);
			}
			loading = false;
		}
		
	}

}
