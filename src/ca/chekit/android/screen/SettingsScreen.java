package ca.chekit.android.screen;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ca.chekit.android.R;

public class SettingsScreen extends BaseScreen implements OnItemClickListener {
	
	private ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_screen);
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_menu_settings);
		actionBar.setDisplayHomeAsUpEnabled(true);
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
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			Intent intent = new Intent(this, ChangePasswordScreen.class);
			startActivity(intent);
		} else if (position == 1) {
			Intent intent = new Intent(this, AboutScreen.class);
			startActivity(intent);
		}
	}

}
