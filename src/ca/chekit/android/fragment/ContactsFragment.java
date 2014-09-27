package ca.chekit.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import ca.chekit.android.R;
import ca.chekit.android.adapter.ContactsAdapter;
import ca.chekit.android.screen.ContactsScreen;
import ca.chekit.android.storage.Settings;

public class ContactsFragment extends Fragment implements OnItemClickListener {
	
	private ListView contactsList;
	private ContactsAdapter adapter;
	private Settings settings;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contacts_fragment, null);
		initializeViews(view);		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = new Settings(getActivity());
	}
	
	private void initializeViews(View view) {
		TypedValue tv = new TypedValue();
		if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
		    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		    View contactsHeader = view.findViewById(R.id.contactsHeader);
		    contactsHeader.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, actionBarHeight));
		}
		contactsList = (ListView) view.findViewById(R.id.contactsList);
		adapter = new ContactsAdapter(getActivity());
		contactsList.setAdapter(adapter);
		contactsList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		long selectedId = settings.getLong(Settings.SELECTED_CONTACT_ID);
		long pickedId = adapter.getItemId(position);
		if (pickedId != selectedId) {
			settings.setLong(Settings.SELECTED_CONTACT_ID, pickedId);
			getActivity().setResult(Activity.RESULT_OK);
		}
		((ContactsScreen) getActivity()).getSlideoutHelper().close();
	}

}
