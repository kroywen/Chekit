package ca.chekit.android.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import ca.chekit.android.R;
import ca.chekit.android.adapter.PhotoAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Photo;
import ca.chekit.android.util.Utilities;

public class PhotosScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	public static final int CAPTURE_PHOTO_REQUEST_CODE = 0;
	public static final int SELECT_PHOTO_REQUEST_CODE = 1;
	public static final int VIEW_PHOTO_REQUEST_CODE = 2;
	
	private View cameraBtn;
	private View galleryBtn;
	private GridView grid;
	private TextView empty;
	
	private long taskId;
	private List<Photo> photos;
	private PhotoAdapter adapter;
	private String currentPhotoPath;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photos_screen);
		getIntentData();
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.photos_icon_big);
		
		if (Utilities.isConnectionAvailable(this)) {
			requestPhotos();
		} else {
			showConnectionErrorDialog();
		}
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
		}
	}
	
	private void initializeViews() {
		empty = (TextView) findViewById(R.id.empty);
		grid = (GridView) findViewById(R.id.grid);
		grid.setOnItemClickListener(this);
		cameraBtn = findViewById(R.id.cameraBtn);
		cameraBtn.setOnClickListener(this);
		galleryBtn = findViewById(R.id.galleryBtn);
		galleryBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cameraBtn:
			dispatchTakePictureIntent();
			break;
		case R.id.galleryBtn:
			dispatchSelectFromGalleryIntent();
			break;
		}
	}
	
	private void dispatchTakePictureIntent() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (intent.resolveActivity(getPackageManager()) != null) {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (Utilities.isConnectionAvailable(this)) {
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
				showConnectionErrorDialog();
			}
		}
	}
	
	private String getPhotoPath(Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, projection, null, null, null);
        c.moveToFirst();
        String path = c.getString(c.getColumnIndex(projection[0]));
        c.close();
        return path;
	}
	
	protected void requestAddPhoto(String path) {
		File file = new File(path);
		if (file.exists()) {
			try {
				InputStream is = new FileInputStream(file);
				int length = is.available();
				byte[] data = new byte[length];
				is.read(data);
				
				Intent intent = new Intent(this, ApiService.class);
				intent.setAction(ApiData.COMMAND_PHOTOS);
				intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_POST);
				intent.putExtra(ApiData.PARAM_ID, taskId);
				intent.putExtra(ApiData.PARAM_BODY, data);
				startService(intent);
				showProgressDialog(R.string.uploading_photo);
				
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void requestPhotos() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_PHOTOS);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
		intent.putExtra(ApiData.PARAM_ID, taskId);
		startService(intent);
		showProgressDialog(R.string.loading_photos);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_PHOTOS.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							photos = (ArrayList<Photo>) apiResponse.getData();
							updateViews();
						}
					} else if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_NO_CONTENT) {
							requestPhotos();
						}
					}
				}
			}
		}
	}
	
	private void updateViews() {
		if (Utilities.isEmpty(photos)) {
			grid.setVisibility(View.INVISIBLE);
			empty.setVisibility(View.VISIBLE);
		} else {
			empty.setVisibility(View.INVISIBLE);
			grid.setVisibility(View.VISIBLE);
			adapter = new PhotoAdapter(this, photos, grid.getWidth());
			grid.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Photo photo = adapter.getItem(position);
		if (photo != null && photo.isThumbnailLoaded()) {
			Intent intent = new Intent(this, PhotoScreen.class);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			intent.putExtra(ApiData.PARAM_ID1, photo.getId());
			intent.putExtra("position", position);
			startActivityForResult(intent, VIEW_PHOTO_REQUEST_CODE);
		}
	}

}