package ca.chekit.android.adapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.model.Account;
import ca.chekit.android.storage.ChatStorage;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

public class ContactsAdapter extends BaseAdapter {
	
	private Context context;
	private List<Account> contacts;
	private ChatStorage chatStorage;
	private long selectedId;
	private long loggedId;
	
	public ContactsAdapter(Context context) {
		this.context = context;
		chatStorage = ChatStorage.newInstance(context);
		loggedId = Account.getCurrent(context).getId();
		
		contacts = new ArrayList<Account>();
		if (!Utilities.isEmpty(Account.getContactList())) {
			long loggedId = Account.getCurrent(context).getId();
			for (Account contact : Account.getContactList()) {
				if (contact.getId() != loggedId) {
					contacts.add(contact);
				}
			}
		}
		Settings settings = new Settings(context);
		selectedId = settings.getLong(Settings.SELECTED_CONTACT_ID); 
	}

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Account getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return contacts.get(position).getId();
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.contact_list_item, null);
		
		Account contact = getItem(position);
		
		int count = chatStorage.getUnreadMessagesCount(context, loggedId, contact.getId());
		TextView badgeIcon = (TextView) convertView.findViewById(R.id.badgeIcon);
		if (count == 0) {
			badgeIcon.setVisibility(View.GONE);
		} else {
			badgeIcon.setVisibility(View.VISIBLE);
			badgeIcon.setText(String.valueOf(count));
		}
		
		TextView name = (TextView) convertView.findViewById(R.id.name);
		name.setText(contact.getFullName());
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		Bitmap bm = Account.loadContactIcon(context, contact.getId());
		if (bm != null) {
			icon.setImageBitmap(bm);
		} else {
			new LoadContactPhotoTask(icon, contact.getId()).execute();
		}
		
		ImageView selected = (ImageView) convertView.findViewById(R.id.selected);
		selected.setVisibility(contact.getId() == selectedId ? View.VISIBLE : View.GONE);
		
		convertView.setBackgroundResource(contact.getId() == selectedId ? 
			R.drawable.list_item_selected_selector : R.drawable.list_item_selector);
		
		return convertView;
	}
	
	private class LoadContactPhotoTask extends AsyncTask<Void, Void, Void> {
		private ImageView iconView;
		private long contactId;
		private Bitmap bitmap;
		
		public LoadContactPhotoTask(ImageView iconView, long contactId) {
			this.iconView = iconView;
			this.contactId = contactId;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String url = ApiData.BASE_URL + String.format(ApiData.COMMAND_CONTACT_PHOTO, contactId);
			AndroidHttpClient client = AndroidHttpClient.newInstance(
				System.getProperty("http.agent"), context);
			HttpGet request = new HttpGet(url);
			
			Settings settings = new Settings(context);
			String auth = settings.getString(Settings.AUTH);
			if (!TextUtils.isEmpty(auth)) {
				request.addHeader("Authorization", "Basic " + auth);
			}
			
			HttpResponse response = null;
			try {
				response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						InputStream is = entity.getContent();
						
						if (iconView == null) {
							return null;
						}
						
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.outWidth = iconView.getWidth();
						options.outHeight = iconView.getHeight();
						
						try {
							bitmap = BitmapFactory.decodeStream(is, null, options);
						} catch (OutOfMemoryError error) {
							error.printStackTrace();
							bitmap = null;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				client.close();
			}
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			if (bitmap != null && iconView != null) {
				Account.saveContactIcon(context, contactId, bitmap);
				iconView.setImageBitmap(bitmap);
				notifyDataSetChanged();
			}
		}
	}

}
