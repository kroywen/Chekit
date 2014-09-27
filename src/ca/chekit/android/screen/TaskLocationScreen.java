package ca.chekit.android.screen;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.WorkStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.util.Utilities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TaskLocationScreen extends BaseScreen implements LocationListener {
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	private TextView taskTitle;
	private TextView dueView;
	private TextView durationView;
	private GoogleMap map;
	
	private long taskId;
	private WorkTask worktask;
	
	private LocationManager locationManager;
	private Location currentBestLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_location_screen);
		getIntentData();
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ic_menu_map);
		
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
			updateLocations();
		}
	}
	
	private void updateLocations() {
		map.clear();
		updateTaskLocation();
		updateMyLocation();
		zoomMap();
	}
	
	private void zoomMap() {
		LatLng myPosition = getMyPosition();
		LatLng taskPosition = getTaskPosition();
		if (myPosition != null && taskPosition != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(myPosition).include(taskPosition);
			LatLngBounds bounds = builder.build();
			map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
		} else if (myPosition != null) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10));
		} else if (taskPosition != null ){
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(taskPosition, 10));
		}
	}
	
	private LatLng getMyPosition() {
		return (currentBestLocation != null) ? new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude()) : null;
	}
	
	private LatLng getTaskPosition() {
		return worktask == null ? null : worktask.hasWorkStatusCoordinates() ? 
			new LatLng(worktask.getWorkStatusLatitude(), worktask.getWorkStatusLongitude()) :
			worktask.hasCoordinates() ? new LatLng(worktask.getLatitude(), worktask.getLongitude()) : null;
	}
	
	private void updateMyLocation() {
		if (map != null && currentBestLocation != null) {
			map.addMarker(new MarkerOptions()
				.position(new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude()))
				.anchor(0.5f, 1.0f)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon))
				.title("MyLocation")
			);
		}
	}
	
	private void updateTaskLocation() {
		if (map != null && worktask != null) {
			if (worktask.hasWorkStatusCoordinates() || worktask.hasCoordinates()) {
				LatLng position = getTaskPosition();
				map.addMarker(new MarkerOptions()
					.position(position)
					.anchor(0.5f, 1.0f)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
					.title(worktask.getDescription())
					.snippet(worktask.getWorkStatus().getName())
				);
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
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
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			taskId = intent.getLongExtra(ApiData.PARAM_ID, 0);
		}
	}
	
	private void initializeViews() {
		taskTitle = (TextView) findViewById(R.id.taskTitle);
		dueView = (TextView) findViewById(R.id.dueView);
		durationView = (TextView) findViewById(R.id.durationView);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if (map != null) {
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					WorkStatus status = WorkStatus.forName(marker.getSnippet());
					return 
						status == null ||
						WorkStatus.NEW.equalsIgnoreCase(status.getName()) ||
						"MyLocation".equals(marker.getTitle());
				}
			});
			
			map.setInfoWindowAdapter(new InfoWindowAdapter() {
				@SuppressLint("InflateParams")
				@Override
				public View getInfoWindow(Marker marker) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					WorkStatus status = WorkStatus.forName(marker.getSnippet());
					
					View layout = inflater.inflate(R.layout.info_window_background, null);
					layout.setBackgroundResource(status.getBackground());
					
					TextView description = (TextView) layout.findViewById(R.id.description);
					description.setText(marker.getTitle());
					
					ImageView statusIcon = (ImageView) layout.findViewById(R.id.statusIcon);
					int icon = status.getIcon();
					if (icon != 0) {
						statusIcon.setImageResource(icon);
						statusIcon.setVisibility(View.VISIBLE);
					} else {
						statusIcon.setVisibility(View.GONE);
					}
					
					TextView statusName = (TextView) layout.findViewById(R.id.statusName);
					statusName.setText(Utilities.addSpaces(marker.getSnippet()));
					
					return layout;
				}
				
				@Override
				public View getInfoContents(Marker marker) {
					return null;
				}
				
			});
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
				}
			}
		}
	}
	
	private void updateViews() {
		if (worktask != null) {
			taskTitle.setText(worktask.getDescription());
			dueView.setText(getString(R.string.due_pattern, Utilities.convertDate(worktask.getDueDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MMMM_dd)));
			durationView.setText(getString(R.string.duration_pattern, worktask.getHumanReadableDuration()));
			updateLocations();
		}
	}

}
