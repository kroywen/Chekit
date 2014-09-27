package ca.chekit.android.screen;

import org.apache.http.HttpStatus;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.dialog.ConfirmationDialog;
import ca.chekit.android.util.Utilities;
import ca.chekit.android.view.TouchImageView;

public class PhotoScreen extends BaseScreen {
	
	private TouchImageView photoView;
	
	private long taskId;
	private long photoId;
	private int position;
	private boolean loaded;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_screen);
		initializeViews();
		getIntentData();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.photos_icon_title);
		
		if (Utilities.isConnectionAvailable(this)) {
			loaded = false;
			requestPhoto();
		} else {
			showConnectionErrorDialog();
		}
	}
	
	private void initializeViews() {
		photoView = (TouchImageView) findViewById(R.id.photoView); 
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
			photoId = intent.getLongExtra(ApiData.PARAM_ID1, 0);
			position = intent.getIntExtra("position", -1);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.photo_screen_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
        case R.id.action_delete:
        	if (Utilities.isConnectionAvailable(this)) {
        		if (loaded) {
        			showDeletePhotoDialog();
        		}
        	} else {
        		showConnectionErrorDialog();
        	}
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showDeletePhotoDialog() {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setText(getString(R.string.confirm_delete_photo));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				requestDeletePhoto();
			}
		});
		dialog.show(getFragmentManager(), "DeletePhotoDialog");
	}
	
	private void requestPhoto() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_ATTACHMENT);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
		intent.putExtra(ApiData.PARAM_ID, taskId);
		intent.putExtra(ApiData.PARAM_ID1, photoId);
		startService(intent);
		showProgressDialog(R.string.loading_photo);
	}
	
	private void requestDeletePhoto() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_ATTACHMENT);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_DELETE);
		intent.putExtra(ApiData.PARAM_ID, taskId);
		intent.putExtra(ApiData.PARAM_ID1, photoId);
		startService(intent);
		showProgressDialog(R.string.deleting_photo);
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_ATTACHMENT.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							Bitmap bitmap = (Bitmap) apiResponse.getData();
							if (bitmap != null) {
								photoView.setImageBitmap(bitmap);
								loaded = true;
							}
						}
					} else if (ApiData.METHOD_DELETE.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_NO_CONTENT) {
							Intent result = new Intent();
							result.putExtra("deleted", true);
							result.putExtra("position", position);
							setResult(RESULT_OK, result);
							finish();
						}
					}
				}
			}
		}
	}

}
