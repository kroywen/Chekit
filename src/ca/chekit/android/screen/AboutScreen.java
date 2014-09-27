package ca.chekit.android.screen;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.util.Utilities;

public class AboutScreen extends BaseScreen {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_screen);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		TextView aboutText = (TextView) findViewById(R.id.aboutText);
		aboutText.setText(getString(R.string.about_text, Utilities.getAppVersionName(this)));
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

}
