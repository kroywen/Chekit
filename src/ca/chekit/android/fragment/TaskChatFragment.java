package ca.chekit.android.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.adapter.ChatAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Account;
import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.screen.BaseScreen;
import ca.chekit.android.screen.ContactsScreen;
import ca.chekit.android.slideout.SlideoutActivity;
import ca.chekit.android.storage.ChatStorage;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

public class TaskChatFragment extends TaskFragment implements OnClickListener {
	
	public static final int SELECT_CONTACT_REQUEST_CODE = 0;
	
	private TextView empty;
	private View chatLayout;
	private ListView messagesList;
	private ImageView selectedIcon;
	private TextView selectedName;
	private EditText newMessage;
	private View sendBtn;
	
	private ChatStorage chatStorage;
	private ChatAdapter adapter;
	private LoadContactPhotoTask photoTask;
	
	private TextView unreadCountView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		chatStorage = ChatStorage.newInstance(getActivity());
		loadContacts();
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.task_chat_fragment, null);
		initializeViews(rootView);
		updateViews();
		return rootView;
	}
	
	private void initializeViews(View view) {
		empty = (TextView) view.findViewById(R.id.empty);
		chatLayout = view.findViewById(R.id.chatLayout);
		messagesList = (ListView) view.findViewById(R.id.messagesList);
		selectedIcon = (ImageView) view.findViewById(R.id.selectedIcon);
		selectedName = (TextView) view.findViewById(R.id.selectedName);
		newMessage = (EditText) view.findViewById(R.id.newMessage);
		sendBtn = view.findViewById(R.id.sendBtn);
		sendBtn.setOnClickListener(this);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.task_chat_fragment_actions, menu);
	    
	    FrameLayout badgeLayout = (FrameLayout) menu.findItem(R.id.action_contacts).getActionView();
	    badgeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
				SlideoutActivity.prepare(getActivity(), R.id.content, width);
				startActivityForResult(new Intent(getActivity(), ContactsScreen.class), SELECT_CONTACT_REQUEST_CODE);
				getActivity().overridePendingTransition(0, 0);
			}
		});
	    
		unreadCountView = (TextView) badgeLayout.findViewById(R.id.unreadCountView);
		int unread = chatStorage.getUnreadMessagesCount(getActivity());
		if (unread == 0) {
			unreadCountView.setVisibility(View.GONE);
		} else {
			unreadCountView.setText(String.valueOf(unread));
			unreadCountView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sendBtn:
			((BaseScreen) getActivity()).hideSoftKeyborad();
			sendMessage();
			break;
		}
	}
	
	private void updateViews() {
		long loggedId = Account.getCurrent(getActivity()).getId();
		long selectedId = settings.getLong(Settings.SELECTED_CONTACT_ID);
		if (selectedId == 0) {
			chatLayout.setVisibility(View.INVISIBLE);
			empty.setVisibility(View.VISIBLE);
		} else {
			chatLayout.setVisibility(View.VISIBLE);
			empty.setVisibility(View.INVISIBLE);
			
			Bitmap contactIcon = Account.loadContactIcon(getActivity(), selectedId); 
			if (contactIcon == null) {
				photoTask = new LoadContactPhotoTask(selectedId);
				photoTask.execute();
			} else {
				selectedIcon.setImageBitmap(contactIcon);
			}
			
			Account contact = Account.getContactById(selectedId);
			if (contact != null) {
				String selectedText = "<font color=\"#1abf75\">" + contact.getFullName() + "</font> " +
					"<font color=\"#878b8e\">selected</font>"; 
				selectedName.setText(Html.fromHtml(selectedText));
			}
			
			List<ChatMessage> chat = chatStorage.getChat(loggedId, selectedId);
			if (!Utilities.isEmpty(chat)) {
				Collections.sort(chat, ChatMessage.InsertDateComparator);
				adapter = new ChatAdapter(getActivity(), chat);
				messagesList.setAdapter(adapter);
				messagesList.setSelection(adapter.getCount() - 1);
				
				(new ReadMessagesTask(chat)).execute();
			}
		}
	}
	
	private void loadContacts() {
		((BaseScreen) getActivity()).loadContacts(true);
	}
	
	private void loadChat() {
		long loggedId = Account.getCurrent(getActivity()).getId();
		long lastUpdateTime = settings.getLong(Settings.CHAT_LAST_UPDATE_TIME + "_" + loggedId);
		((BaseScreen) getActivity()).loadChat(lastUpdateTime, true);
	}
	
	private void sendMessage() {
		String message = newMessage.getText().toString().trim();
		if (TextUtils.isEmpty(message)) {
			return;
		}
		newMessage.setText(null);
		
		long loggedId = Account.getCurrent(getActivity()).getId();
		long selectedId = settings.getLong(Settings.SELECTED_CONTACT_ID);
		sendMessage(loggedId, selectedId, message);
	}
	
	private void sendMessage(long fromId, long toId, String message) {
		((BaseScreen) getActivity()).sendMessage(fromId, toId, message);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_CONTACTS.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							List<Account> contacts = (ArrayList<Account>) apiResponse.getData();
							Account.setContactList(contacts);
							loadChat();
						}
					}
				} else if (ApiData.COMMAND_CHAT.equalsIgnoreCase(command)) {
					long loggedId = Account.getCurrent(getActivity()).getId();
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							List<ChatMessage> messages = (List<ChatMessage>) apiResponse.getData();
							long lastUpdateTime = Utilities.findLastUpdateTime(messages);
							settings.setLong(Settings.CHAT_LAST_UPDATE_TIME + "_" + loggedId, lastUpdateTime);
							chatStorage.addMessages(messages);
							updateViews();
						}
					} else if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_CREATED) {
							loadChat();
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_CONTACT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (photoTask != null) {
				photoTask.cancel(true);
			}
			loadChat();
		}
	}
	
	private class LoadContactPhotoTask extends AsyncTask<Void, Void, Void> {
		
		private long contactId;
		private Bitmap bitmap;
		
		public LoadContactPhotoTask(long contactId) {
			this.contactId = contactId;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			AndroidHttpClient client = null;
			try {
				String url = ApiData.BASE_URL + String.format(ApiData.COMMAND_CONTACT_PHOTO, contactId);
				client = AndroidHttpClient.newInstance(
					System.getProperty("http.agent"), getActivity());
				HttpGet request = new HttpGet(url);
				
				Settings settings = new Settings(getActivity());
				String auth = settings.getString(Settings.AUTH);
				if (!TextUtils.isEmpty(auth)) {
					request.addHeader("Authorization", "Basic " + auth);
				}
			
				HttpResponse response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						InputStream is = entity.getContent();
						
						if (selectedIcon == null) {
							return null;
						}
						
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.outWidth = selectedIcon.getWidth();
						options.outHeight = selectedIcon.getHeight();
						
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
				if (client != null) {
					client.close();
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (!isCancelled()) { 
				if (bitmap != null) {
					Account.saveContactIcon(getActivity(), contactId, bitmap);
					selectedIcon.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	class ReadMessagesTask extends AsyncTask<Void, Void, Void> {
		
		private List<ChatMessage> messages;
		private long loggedId;
		
		public ReadMessagesTask(List<ChatMessage> messages) {
			this.messages = messages;
			loggedId = Account.getCurrent(getActivity()).getId();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			if (Utilities.isEmpty(messages)) {
				return null;
			}
			for (ChatMessage message : messages) {
				if (message.getFromContactId() != loggedId && !message.isRead()) {
					try {
						String url = ApiData.BASE_URL + String.format(ApiData.COMMAND_CHAT_MESSAGE_READ, message.getId());
						AndroidHttpClient client = AndroidHttpClient.newInstance(
							System.getProperty("http.agent"), getActivity());
						HttpPut request = new HttpPut(url);
						request.setEntity(new StringEntity("true", "UTF-8"));
						
						request.addHeader("Content-Type", "application/json");
						request.addHeader("Accept", "application/json");
						
						Settings settings = new Settings(getActivity());
						String auth = settings.getString(Settings.AUTH);
						if (!TextUtils.isEmpty(auth)) {
							request.addHeader("Authorization", "Basic " + auth);
						}
						
						HttpResponse response = client.execute(request);
						int statusCode = response.getStatusLine().getStatusCode();
						
						if (statusCode == HttpStatus.SC_NO_CONTENT) {
							chatStorage.setMessageRead(message);
							Log.d("UNREAD", "read message: " + message.getMessage());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			try {
				getActivity().invalidateOptionsMenu();
			} catch (Exception e) {}
		}
		
	}

}
