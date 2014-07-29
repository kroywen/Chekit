package ca.chekit.android.screen;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import ca.chekit.android.R;
import ca.chekit.android.adapter.TaskStatusAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.dialog.ConfirmationDialog;
import ca.chekit.android.model.WorkStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.util.Utilities;

public class TaskScreen extends BaseScreen implements OnClickListener, OnItemClickListener, LocationListener {
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	private View photosBtn;
	private View notesBtn;
	private TextView taskTitle;
	private TextView dueView;
	private TextView durationView;
	private GridView statusList;
	
	private long taskId;
	private WorkTask worktask;
	private TaskStatusAdapter adapter;
	private WorkStatus clickedStatus;
	
	private LocationManager locationManager;
	private Location currentBestLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_screen);
		getIntentData();
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		if (Utilities.isConnectionAvailable(this)) {
			getWorkTask();
		} else {
			showConnectionErrorDialog();
		}
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		currentBestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0f, this);
		}
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0.0f, this);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(this);
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
		}
	}
	
	private void initializeViews() {
		photosBtn = findViewById(R.id.photosBtn);
		photosBtn.setOnClickListener(this);
		
		notesBtn = findViewById(R.id.notesBtn);
		notesBtn.setOnClickListener(this);
		
		taskTitle = (TextView) findViewById(R.id.taskTitle);
		dueView = (TextView) findViewById(R.id.dueView);
		durationView = (TextView) findViewById(R.id.durationView);
		
		statusList = (GridView) findViewById(R.id.statusList);
		statusList.setOnItemClickListener(this);
		
		adapter = new TaskStatusAdapter(this);
		statusList.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.task_screen_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
        case R.id.action_map:
        	Intent intent = new Intent(this, TaskLocationScreen.class);
        	intent.putExtra(ApiData.PARAM_ID, taskId);
        	startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void getWorkTask() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_WORKTASK);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
		intent.putExtra(ApiData.PARAM_ID, taskId);
		startService(intent);
		showProgressDialog(R.string.loading_task);
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		super.onApiResponse(apiStatus, apiResponse);
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_WORKTASK.equalsIgnoreCase(command) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK) {
						worktask = (WorkTask) apiResponse.getData();
						updateViews();
					}
				} else if (ApiData.COMMAND_WORKTASK_STATUS.equalsIgnoreCase(command) && ApiData.METHOD_PUT.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_NO_CONTENT) {
						worktask.setWorkStatus(clickedStatus);
						adapter.setCurrentTaskStatus(clickedStatus);
						clickedStatus = null;
						setResult(RESULT_OK);
					} else {
						Toast.makeText(this, "Error. Status code: " + statusCode, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}
	
	private void updateViews() {
		if (worktask != null) {
			taskTitle.setText(worktask.getDescription());
			dueView.setText(getString(R.string.due_pattern, Utilities.convertDate(worktask.getDueDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd)));
			durationView.setText(getString(R.string.duration_pattern, worktask.getHumanReadableDuration()));
			
			adapter.setCurrentTaskStatus(worktask.getWorkStatus());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		clickedStatus = adapter.getWorkStatus(position);
		if (clickedStatus == adapter.getSelectedWorkStatus()) {
			return;
		}
		if (Utilities.isConnectionAvailable(this)) {
			final ConfirmationDialog dialog = new ConfirmationDialog();
			dialog.setText(getString(R.string.update_status, Utilities.addSpaces(clickedStatus.name())));
			dialog.setOkListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateWorkTaskStatus(clickedStatus);
					dialog.dismiss();
				}
			});
			dialog.show(getFragmentManager(), "UpdateStatusDialog");
		} else {
			showConnectionErrorDialog();
		}
	}
	
	private void updateWorkTaskStatus(WorkStatus status) {
		try {
			Intent intent = new Intent(this, ApiService.class);
			intent.setAction(ApiData.COMMAND_WORKTASK_STATUS);
			intent.putExtra(ApiData.PARAM_ID, taskId);
			intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_PUT);
			
			JSONObject obj = new JSONObject();
			obj.put("WorkStatus", status.name());
			if (currentBestLocation != null) {
				obj.put("Latitude", currentBestLocation.getLatitude());
				obj.put("Longitude", currentBestLocation.getLongitude());
			}
			
			intent.putExtra(ApiData.PARAM_BODY, obj.toString());
			startService(intent);
			showProgressDialog(R.string.updating_work_status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean isBetterLocation(Location location) {
	    if (currentBestLocation == null) {
	        return true;
	    }

	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    if (isSignificantlyNewer) {
	        return true;
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location)) {
			currentBestLocation = location;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.photosBtn:
			Intent intent = new Intent(this, PhotosScreen.class);
			intent.putExtra(ApiData.PARAM_ID, worktask.getId());
			startActivity(intent);
			break;
		case R.id.notesBtn:
			intent = new Intent(this, NotesScreen.class);
			intent.putExtra(ApiData.PARAM_ID, worktask.getId());
			startActivity(intent);
			break;
		}
	}

}
