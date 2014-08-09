package ca.chekit.android.screen;

import org.apache.http.HttpStatus;

import android.app.ActionBar;
import android.content.Intent;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TaskLocationScreen extends BaseScreen {
	
	private TextView taskTitle;
	private TextView dueView;
	private TextView durationView;
	private GoogleMap map;
	
	private long taskId;
	private WorkTask worktask;
	
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
			
			if (map != null) {
				if (worktask.hasWorkStatusCoordinates() || worktask.hasCoordinates()) {
					
					LatLng position = worktask.hasWorkStatusCoordinates() ? 
							new LatLng(worktask.getWorkStatusLatitude(), worktask.getWorkStatusLongitude()) :
							new LatLng(worktask.getLatitude(), worktask.getLongitude());
							
					map.addMarker(new MarkerOptions()
						.position(position)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
						.title(worktask.getDescription())
						.snippet(worktask.getWorkStatus().name())
					);
					
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
					
					map.setInfoWindowAdapter(new InfoWindowAdapter() {
						@Override
						public View getInfoWindow(Marker marker) {
							LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
							
							View layout = inflater.inflate(R.layout.info_window_background, null);
							layout.setBackgroundResource(getBackgroundForStatus(marker.getSnippet()));
							
							TextView description = (TextView) layout.findViewById(R.id.description);
							description.setText(marker.getTitle());
							
							ImageView statusIcon = (ImageView) layout.findViewById(R.id.statusIcon);
							int icon = getIconForStatus(marker.getSnippet());
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
						
						private int getIconForStatus(String status) {
							if (WorkStatus.New.name().equalsIgnoreCase(status)) {
								return 0;
							} else if (WorkStatus.OnRoute.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_on_route;
							} else if (WorkStatus.OnSite.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_on_site;
							} else if (WorkStatus.VerifySite.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_verify_site;
							} else if (WorkStatus.Start.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_start;
							} else if (WorkStatus.Active.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_active;
							} else if (WorkStatus.Complete.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_complete;
							} else if (WorkStatus.CompleteLeaveSite.name().equalsIgnoreCase(status)) {
								return 0;
							} else if (WorkStatus.ProblemsActive.name().equalsIgnoreCase(status)) {
								return 0;
							} else if (WorkStatus.ProblemsDelayed.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_problems_delayed;
							} else if (WorkStatus.IncompleteLeaveSite.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_incomplete_leave_site;
							} else if (WorkStatus.SafetyIssue.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_safety_issue;
							} else if (WorkStatus.ProblemsStop.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_problems_stop;
							} else if (WorkStatus.SafetyDanger.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_safety_danger;
							} else if (WorkStatus.ProblemsLeaveSite.name().equalsIgnoreCase(status)) {
								return R.drawable.icon_status_problems_leave_site;
							} else if (WorkStatus.Cancelled.name().equalsIgnoreCase(status)) {
								return 0;
							} else if (WorkStatus.Closed.name().equalsIgnoreCase(status)) {
								return 0;
							} else {
								return 0;
							}
						}
						
						private int getBackgroundForStatus(String status) {
							if (WorkStatus.New.name().equalsIgnoreCase(status) ||
								WorkStatus.OnRoute.name().equalsIgnoreCase(status) ||
								WorkStatus.OnSite.name().equalsIgnoreCase(status) ||
								WorkStatus.VerifySite.name().equalsIgnoreCase(status) ||
								WorkStatus.Start.name().equalsIgnoreCase(status) ||
								WorkStatus.Active.name().equalsIgnoreCase(status) ||
								WorkStatus.Complete.name().equalsIgnoreCase(status) ||
								WorkStatus.CompleteLeaveSite.name().equalsIgnoreCase(status))
							{
								return R.drawable.info_window_background_green;
							} else if (WorkStatus.ProblemsActive.name().equalsIgnoreCase(status) ||
								WorkStatus.ProblemsDelayed.name().equalsIgnoreCase(status) ||
								WorkStatus.IncompleteLeaveSite.name().equalsIgnoreCase(status) ||
								WorkStatus.SafetyIssue.name().equalsIgnoreCase(status)) 
							{
								return R.drawable.info_window_background_yellow;
							} else if (WorkStatus.ProblemsStop.name().equalsIgnoreCase(status) ||
								WorkStatus.SafetyDanger.name().equalsIgnoreCase(status) ||
								WorkStatus.ProblemsLeaveSite.name().equalsIgnoreCase(status)) 
							{
								return R.drawable.info_window_background_red;
							} else {
								return R.drawable.info_window_background_green;
							}
						}
					});
				}
			}
		}
	}

}
