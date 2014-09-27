package ca.chekit.android.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import ca.chekit.android.model.Account;
import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.util.Utilities;

public class ChatStorage {
	
	private Context context;
	
	private ChatStorage(Context context) {
		this.context = context;
	}
	
	public static ChatStorage newInstance(Context context) {
		return new ChatStorage(context);
	}
	
	public synchronized List<ChatMessage> getChat(long fromId, long toId) {
		long firstId = Math.min(fromId, toId);
		long secondId = Math.max(fromId, toId);
		String filename = "chat_" + firstId + "_" + secondId;
		return getChat(filename);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<ChatMessage> getChat(String filename) {
		try {
			FileInputStream fis = context.openFileInput(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<ChatMessage> chat = (List<ChatMessage>) ois.readObject();
			ois.close();
			return chat;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized void addChat(long fromId, long toId, List<ChatMessage> chat) {
		List<ChatMessage> prevChat = getChat(fromId, toId);
		List<ChatMessage> newChat = new ArrayList<ChatMessage>();
		if (!Utilities.isEmpty(prevChat)) {
			newChat.addAll(prevChat);
		}
		if (!Utilities.isEmpty(chat)) { 
			if (!newChat.isEmpty()) { 
				for (ChatMessage message : chat) {
					boolean found = false;
					for (ChatMessage oldMessage : newChat) {
						if (message.getId() == oldMessage.getId()) {
							found = true;
							break;
						}
					}
					if (!found) {
						newChat.add(message);
					}
				}
			} else {
				newChat.addAll(chat);
			}
		}
		saveChat(fromId, toId, newChat);
	}
	
	public synchronized void saveChat(long fromId, long toId, List<ChatMessage> chat) {
		try {
			long firstId = Math.min(fromId, toId);
			long secondId = Math.max(fromId, toId);
			String filename = "chat_" + firstId + "_" + secondId;
			saveFilename(filename);
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(chat);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveFilename(String filename) {
		Settings settings = new Settings(context);
		String filenames = settings.getString(Settings.CHAT_FILENAMES);
		if (TextUtils.isEmpty(filenames)) {
			filenames = filename;
		} else {
			if (!filenames.contains(filename)) {
				filenames += filenames.endsWith(";") ? filename : ";" + filename; 
			}
		}
		settings.setString(Settings.CHAT_FILENAMES, filenames);
	}
	
	private String[] getFilenames() {
		Settings settings = new Settings(context);
		String filenames = settings.getString(Settings.CHAT_FILENAMES);
		return TextUtils.isEmpty(filenames) ? null : filenames.split(";");
	}
	
	public synchronized void setMessageRead(ChatMessage message) {
		if (message == null) {
			return;
		}
		long fromId = message.getFromContactId();
		long toId = message.getToContactId();
		List<ChatMessage> chat = getChat(fromId, toId);
		if (!Utilities.isEmpty(chat)) {
			for (ChatMessage msg : chat) {
				if (msg.getId() == message.getId()) {
					msg.setRead(true);
					break;
				}
			}
		}
		saveChat(fromId, toId, chat);
	}
	
	public synchronized void addMessages(List<ChatMessage> messages) {
		if (Utilities.isEmpty(messages)) {
			return;
		}
		Map<String, List<ChatMessage>> map = new HashMap<String, List<ChatMessage>>();
		for (ChatMessage message : messages) {
			String key = message.getFromContactId() + "_" + message.getToContactId();
			List<ChatMessage> list = map.get(key);
			if (list == null) {
				list = new ArrayList<ChatMessage>();
			}
			list.add(message);
			map.put(key, list);
		}
		Set<String> keys = map.keySet();
		if (!Utilities.isEmpty(keys)) {
			Iterator<String> i = keys.iterator();
			while (i.hasNext()) {
				List<ChatMessage> list = map.get(i.next());
				if (!Utilities.isEmpty(list)) {
					ChatMessage firstMsg = list.get(0);
					addChat(firstMsg.getFromContactId(), firstMsg.getToContactId(), list);
				}
			}
		}
	}
	
	public synchronized void addMessage(ChatMessage message) {
		if (message == null) {
			return;
		}
		long firstId = message.getFromContactId();
		long secondId = message.getToContactId();
		List<ChatMessage> messages = new ArrayList<ChatMessage>();
		messages.add(message);
		addChat(firstId, secondId, messages);
	}
	
	public synchronized int getUnreadMessagesCount(Context context, long fromId, long toId) {
		Account current = Account.getCurrent(context);
		if (current == null) {
			return 0;
		}
		long loggedId = Account.getCurrent(context).getId();
		int count = 0;
		List<ChatMessage> messages = getChat(fromId, toId);
		if (!Utilities.isEmpty(messages)) {
			for (ChatMessage message : messages) {
				if (message.getFromContactId() != loggedId && !message.isRead()) {
					count++;
				}
			}
		}
		return count;
	}
	
	public synchronized int getUnreadMessagesCount(Context context) {
		Account current = Account.getCurrent(context);
		if (current == null) {
			return 0;
		}
		long loggedId = Account.getCurrent(context).getId();
		int count = 0;
		String[] filenames = getFilenames();
		if (filenames != null && filenames.length != 0) {
			for (String filename : filenames) {
				long[] ids = extractContactIDs(filename);
				if (ids != null && ids.length == 2) {
					long contactId = (ids[0] == loggedId) ? ids[1] : 
						(ids[1] == loggedId) ? ids[0] : 0;
					Account contact = Account.getContactById(contactId);
					if (contact != null) {
						count += getUnreadMessagesCount(context, loggedId, contact.getId());
					}
				}
			}
		}
		return count;
	}
	
	private long[] extractContactIDs(String filename) {
		String[] array = filename.split("_");
		long[] result = new long[] {
			Long.parseLong(array[1]),
			Long.parseLong(array[2])
		};
		return result;
	}

}
