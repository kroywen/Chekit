package ca.chekit.android.screen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.ScheduledStatus;
import ca.chekit.android.model.WorkStatus;
import ca.chekit.android.model.WorkTask;
import ca.chekit.android.util.Utilities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationScreen extends BaseScreen {
	
	private GoogleMap map;
	
	private List<WorkTask> worktasks;
	private Bitmap dotBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_screen);
		initializeViews();
		generateBitmap();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ic_menu_map);
		
		if (Utilities.isConnectionAvailable(this)) {
			refreshWorkTasks(true);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	private void generateBitmap() {
		int px = Utilities.dpToPx(this, 20);
		dotBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(dotBitmap);
		Drawable shape = getResources().getDrawable(R.drawable.map_dot_green);
		shape.setBounds(0, 0, dotBitmap.getWidth(), dotBitmap.getHeight());
		shape.draw(canvas);
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
	
	private void initializeViews() {
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
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
				
				if ((ApiData.COMMAND_WORKTASKS.equalsIgnoreCase(command) || 
						ApiData.COMMAND_CONTACT_WORKTASKS.equalsIgnoreCase(command)) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK) {
						worktasks = (List<WorkTask>) apiResponse.getData();
						updateViews();
					}
				}
			}
		}		
	}
	
	private void updateViews() {
		filterWorktasks();
		if (Utilities.isEmpty(worktasks)) {
			return;
		}
		
		map.clear();
		for (WorkTask worktask : worktasks) {
			if (worktask.hasWorkStatusCoordinates() || worktask.hasCoordinates()) {
				LatLng position = getTaskPosition(worktask);
				Bitmap icon = getWorktaskIcon(worktask);
				map.addMarker(new MarkerOptions()
					.position(position)
					.anchor(0.5f, 1.0f)
					.icon(BitmapDescriptorFactory.fromBitmap(icon))
				);
			}
		}
		
		zoomMap();
	}
	
	@SuppressLint("InflateParams")
	private Bitmap getWorktaskIcon(WorkTask worktask) {
		if (worktask.getScheduledStatus() == ScheduledStatus.Assigned || 
			WorkStatus.NEW.equalsIgnoreCase(worktask.getWorkStatus().getName()))
		{
			return dotBitmap;
		} else {
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.task_pin_layout, null);
			
			ImageView image = (ImageView) view.findViewById(R.id.image);
			image.setBackgroundResource(worktask.getWorkStatus().getPinBackground());
			image.setImageResource(worktask.getWorkStatus().getIcon());
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
			view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
			view.buildDrawingCache();
			Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		 
			Canvas canvas = new Canvas(bitmap);
			view.draw(canvas);
		 
			return bitmap;
		}
	}
	
	private LatLng getTaskPosition(WorkTask worktask) {
		return worktask == null ? null : worktask.hasWorkStatusCoordinates() ? 
			new LatLng(worktask.getWorkStatusLatitude(), worktask.getWorkStatusLongitude()) :
			worktask.hasCoordinates() ? new LatLng(worktask.getLatitude(), worktask.getLongitude()) : null;
	}
	
	private void filterWorktasks() {
		if (!Utilities.isEmpty(worktasks)) {
			List<WorkTask> list = new ArrayList<WorkTask>();
			for (WorkTask task : worktasks) {
				if (task.getScheduledStatus() != ScheduledStatus.Rejected) {
					list.add(task);
				}
			}
			worktasks = list;
		}
	}
	
	private void zoomMap() {
		if (Utilities.isEmpty(worktasks)) {
			return;
		}
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (WorkTask worktask : worktasks) {
			if (worktask.hasWorkStatusCoordinates() || worktask.hasCoordinates()) {
				LatLng position = getTaskPosition(worktask);
				builder.include(position);
			}
		}
		
		LatLngBounds bounds = builder.build();
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
	}

}
