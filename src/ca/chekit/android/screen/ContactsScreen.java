package ca.chekit.android.screen;

import android.os.Bundle;
import android.view.KeyEvent;
import ca.chekit.android.R;
import ca.chekit.android.fragment.ContactsFragment;
import ca.chekit.android.slideout.SlideoutHelper;

public class ContactsScreen extends BaseScreen {
	
	private SlideoutHelper mSlideoutHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mSlideoutHelper = new SlideoutHelper(this, true);
	    mSlideoutHelper.activate();
	    getFragmentManager().beginTransaction().add(R.id.slideout_placeholder, new ContactsFragment(), "contacts").commit();
	    mSlideoutHelper.open();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			mSlideoutHelper.close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public SlideoutHelper getSlideoutHelper(){
		return mSlideoutHelper;
	}

}
