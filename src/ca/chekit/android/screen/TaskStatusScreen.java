package ca.chekit.android.screen;

import org.apache.http.HttpStatus;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import ca.chekit.android.R;
import ca.chekit.android.adapter.TaskStatusAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Account;
import ca.chekit.android.model.WorkStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.util.Utilities;

public class TaskStatusScreen extends BaseScreen implements OnItemClickListener, LocationListener {
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
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
		setContentView(R.layout.task_status_screen);
		getIntentData();
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.task_status_2);
		actionBar.setIcon(R.drawable.task_icon);
		
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	setResult(RESULT_CANCELED);
	    	finish();
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
		}
	}
	
	private void initializeViews() {
		statusList = (GridView) findViewById(R.id.statusList);
		statusList.setOnItemClickListener(this);
		
		adapter = new TaskStatusAdapter(this);
		statusList.setAdapter(adapter);
	}
	
	private void getWorkTask() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setAction(ApiData.COMMAND_WORKTASK);
		intent.putExtra(ApiData.PARAM_METHOD, ApiData.METHOD_GET);
		intent.putExtra(ApiData.PARAM_ID, taskId);
		startService(intent);
		showProgressDialog(R.string.loading_status);
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
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
				}
			}
		}
	}
	
	private void updateViews() {
		if (worktask != null) {
			adapter.setCurrentTaskStatus(worktask.getWorkStatus());
			
			long loggedId = Account.getCurrent(this).getId();
			long assigneeId = worktask.getAssigneeId();
			statusList.setOnItemClickListener(loggedId == assigneeId ? this : null);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		
		clickedStatus = adapter.getWorkStatus(position);
		if (clickedStatus == adapter.getSelectedWorkStatus()) {
			return;
		}
		
		Intent data = new Intent();
		data.putExtra(WorkTask.ID, taskId);
		data.putExtra("StatusName", clickedStatus.getName());
		data.putExtra(WorkTask.WORK_STATUS, clickedStatus.getPostName());
		data.putExtra(WorkTask.PREVIOUS_WORK_STATUS, worktask.getWorkStatus().getName());
		data.putExtra(WorkTask.DURATION, worktask.getDuration());
		if (currentBestLocation != null) {
			data.putExtra(WorkTask.LATITUDE, currentBestLocation.getLatitude());
			data.putExtra(WorkTask.LONGITUDE, currentBestLocation.getLongitude());
		}
		setResult(RESULT_OK, data);
		finish();	
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

}
