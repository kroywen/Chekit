package ca.chekit.android.model;

import java.io.Serializable;
import java.util.Comparator;

import org.json.JSONObject;

import android.content.Context;
import ca.chekit.android.util.Utilities;

public class ChatMessage implements Serializable {
	
	private static final long serialVersionUID = 5092126743752449206L;
	
	public static final String ID = "Id";
	public static final String FROM_CONTACT_ID = "FromContactId";
	public static final String TO_CONTACT_ID = "ToContactId";
	public static final String WORKTASK_ID = "WorkTaskId";
	public static final String MESSAGE = "Message";
	public static final String READ = "Read";
	public static final String SENT_DATE = "SentDate";
	public static final String INSERT_DATE = "InsertDate";
	
	private long id;
	private long fromContactId;
	private long toContactId;
	private long worktaskId;
	private String message;
	private boolean read;
	private String sentDate;
	private String insertDate;
	private long insertDateMillis;
	
	public ChatMessage() {}
	
	public ChatMessage(JSONObject obj) {
		this.id = obj.optLong(ID);
		this.fromContactId = obj.optLong(FROM_CONTACT_ID);
		this.toContactId = obj.optLong(TO_CONTACT_ID);
		this.worktaskId = obj.optLong(WORKTASK_ID);
		this.message = obj.optString(MESSAGE);
		this.read = obj.optBoolean(READ);
		this.sentDate = obj.optString(SENT_DATE);
		this.insertDate = obj.optString(INSERT_DATE);
		setInsertDateMillis();
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getFromContactId() {
		return fromContactId;
	}
	
	public void setFromContactId(long fromContactId) {
		this.fromContactId = fromContactId;
	}
	
	public long getToContactId() {
		return toContactId;
	}
	
	public void setToContactId(long toContactId) {
		this.toContactId = toContactId;
	}
	
	public long getWorktaskId() {
		return worktaskId;
	}
	
	public void setWorktaskId(long worktaskId) {
		this.worktaskId = worktaskId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isRead() {
		return read;
	}
	
	public void setRead(boolean read) {
		this.read = read;
	}
	
	public String getSentDate() {
		return sentDate;
	}
	
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	
	public String getInsertDate() {
		return insertDate;
	}
	
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}
	
	public long getInsertDateMillis() {
		return insertDateMillis;
	}
	
	private void setInsertDateMillis() {
		try {
			insertDateMillis = Utilities.parseDate(insertDate, Utilities.yyyy_MM_ddTHH_mm_ss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isIncoming(Context context)  {
		return Account.getCurrent(context).getId() != fromContactId;
	}
	
	public static Comparator<ChatMessage> InsertDateComparator = new Comparator<ChatMessage>() {
		@Override
		public int compare(ChatMessage o1, ChatMessage o2) {
			long t1 = o1.getInsertDateMillis();
			long t2 = o2.getInsertDateMillis();
			return (t1 < t2) ? -1 : (t1 > t2) ? 1 : 0;
		}
	};

}
