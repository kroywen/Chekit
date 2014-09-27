package ca.chekit.android.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.adapter.PhotoAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Attachment;
import ca.chekit.android.screen.PhotoScreen;
import ca.chekit.android.screen.TaskDetailsScreen;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

public class TaskPhotosFragment extends TaskFragment implements OnItemClickListener {
	
	public static final int CAPTURE_PHOTO_REQUEST_CODE = 0;
	public static final int SELECT_PHOTO_REQUEST_CODE = 1;
	public static final int VIEW_PHOTO_REQUEST_CODE = 2;
	
	private GridView grid;
	private TextView empty;
	
	private PhotoAdapter adapter;
	private String currentPhotoPath;
	private static int gridWidth;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.task_photos_fragment, null);
		initializeViews(rootView);
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (isTaskPhotosLoaded()) {
			updateViews();
		} else {
			loadTaskPhotos();
		}
	}
	
	private void initializeViews(View rootView) {
		empty = (TextView) rootView.findViewById(R.id.empty);
		grid = (GridView) rootView.findViewById(R.id.grid);
		grid.setOnItemClickListener(this);
	}
	
	private void updateViews() {
		List<Attachment> photos = getTaskPhotos();
		if (Utilities.isEmpty(photos)) {
			grid.setVisibility(View.INVISIBLE);
			empty.setVisibility(View.VISIBLE);
		} else {
			empty.setVisibility(View.INVISIBLE);
			grid.setVisibility(View.VISIBLE);
			gridWidth = grid.getWidth() != 0 ? grid.getWidth() : gridWidth;
			adapter = new PhotoAdapter(getActivity(), photos, gridWidth);
			grid.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Attachment photo = adapter.getItem(position);
		if (photo != null && photo.isThumbnailLoaded()) {
			Intent intent = new Intent(getActivity(), PhotoScreen.class);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			intent.putExtra(ApiData.PARAM_ID1, photo.getId());
			intent.putExtra("position", position);
			startActivityForResult(intent, VIEW_PHOTO_REQUEST_CODE);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.task_photos_fragment_actions, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.action_camera:
        	dispatchTakePictureIntent();
            return true;
        case R.id.action_gallery:
        	dispatchSelectFromGalleryIntent();
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void dispatchTakePictureIntent() {
		if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
		    	File photoFile = null;
		        try {
		            photoFile = createImageFile();
		        } catch (IOException e) {
		        	e.printStackTrace();
		        }
		        if (photoFile != null) {
		        	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		            startActivityForResult(intent, CAPTURE_PHOTO_REQUEST_CODE);
		        }
		    }
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File image = new File(storageDir, imageFileName + ".jpg");
	    currentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	
	private void dispatchSelectFromGalleryIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, SELECT_PHOTO_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (Utilities.isConnectionAvailable(getActivity())) {
				switch (requestCode) {
				case VIEW_PHOTO_REQUEST_CODE:
					boolean deleted = data.getBooleanExtra("deleted", false);
					if (deleted) {
						int position = data.getIntExtra("position", -1);
						if (position != -1) {
							adapter.removePhoto(position);
						}
					}
					break;
				case CAPTURE_PHOTO_REQUEST_CODE:
					requestAddPhoto(currentPhotoPath);
					break;
				case SELECT_PHOTO_REQUEST_CODE:
					String path = getPhotoPath(data.getData());
					requestAddPhoto(path);
					break;
				}
			} else {
				((TaskDetailsScreen) getActivity()).showConnectionErrorDialog();
			}
		}
	}
	
	private String getPhotoPath(Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
        Cursor c = getActivity().getContentResolver().query(uri, projection, null, null, null);
        c.moveToFirst();
        String path = c.getString(c.getColumnIndex(projection[0]));
        c.close();
        return path;
	}
	
	private List<Attachment> getTaskPhotos() {
		return ((TaskDetailsScreen) getActivity()).getTaskPhotos();
	}
	
	private void setTaskPhotos(List<Attachment> photos) {
		((TaskDetailsScreen) getActivity()).setTaskPhotos(photos);
	}
	
	private boolean isTaskPhotosLoaded() {
		return ((TaskDetailsScreen) getActivity()).isTaskPhotosLoaded();
	}
	
	private void setTaskPhotosLoaded(boolean taskPhotosLoaded) {
		((TaskDetailsScreen) getActivity()).setTaskPhotosLoaded(taskPhotosLoaded);
	}
	
	private void loadTaskPhotos() {
		((TaskDetailsScreen) getActivity()).loadTaskPhotos();
	}
	
	private void requestAddPhoto(String path) {
		new UploadPhotoTask(path).execute();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_ATTACHMENTS.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							List<Attachment> photos = (ArrayList<Attachment>) apiResponse.getData();
							setTaskPhotos(photos);
							setTaskPhotosLoaded(true);
							updateViews();
						}
					}
				}
			}
		}
	}
	
	class UploadPhotoTask extends AsyncTask<Void, Void, Integer> implements OnCancelListener {
		
		private ProgressDialog dialog;
		private String filepath;
		
		public UploadPhotoTask(String filepath) {
			this.filepath = filepath;
    		dialog = new ProgressDialog(getActivity());
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.uploading_photo));
    		dialog.setIndeterminate(true);
    		dialog.setOnCancelListener(this);
		}
		
		@Override
    	protected void onPreExecute() {
    		dialog.show();
    	}

		@Override
		public void onCancel(DialogInterface dialog) {
			cancel(true);
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			String url = ApiData.BASE_URL + String.format(ApiData.COMMAND_ATTACHMENTS, getTaskId());
			AndroidHttpClient client = AndroidHttpClient.newInstance(
				System.getProperty("http.agent"), getActivity());
			HttpPost request = new HttpPost(url);
			
			request.addHeader("Content-Type", "image/jpeg");
			
			Settings settings = new Settings(getActivity());
			String auth = settings.getString(Settings.AUTH);
			if (!TextUtils.isEmpty(auth)) {
				request.addHeader("Authorization", "Basic " + auth);
			}
			
			try {
				File file = new File(filepath);
				FileInputStream fis = new FileInputStream(file);
				int length = fis.available();
				byte[] data = new byte[length];
				fis.read(data);
				request.setEntity(new ByteArrayEntity(data));
				
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				fis.close();
				return statusCode;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
		
		@Override
		public void onPostExecute(Integer statusCode) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (statusCode == HttpStatus.SC_CREATED) {
				loadTaskPhotos();
			}
		}
		
	}

}
