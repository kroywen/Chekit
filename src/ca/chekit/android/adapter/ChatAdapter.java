package ca.chekit.android.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.model.Account;
import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.util.Utilities;

public class ChatAdapter extends BaseAdapter {
	
	private Context context;
	private List<ChatMessage> messages;
	private long loggedId;
	
	public ChatAdapter(Context context, List<ChatMessage> messages) {
		this.context = context;
		this.messages = messages;
		loggedId = Account.getCurrent(context).getId();
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public ChatMessage getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage message = getItem(position);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int layout = message.getFromContactId() == loggedId ? R.layout.outgoing_chat_list_item : R.layout.incoming_chat_list_item;
		convertView = inflater.inflate(layout, null);
		
		TextView messageView = (TextView) convertView.findViewById(R.id.messageView);
		messageView.setText(message.getMessage());
		
		TextView dateView = (TextView) convertView.findViewById(R.id.dateView);
		dateView.setText(Utilities.convertDate(message.getInsertDate(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.dd_MM_yyyy_hh_mm_a));
		
		ImageView contactIconView = (ImageView) convertView.findViewById(R.id.contactIconView);
		if (contactIconView != null && message.isIncoming(context)) {
			Bitmap contactIcon = Account.loadContactIcon(context, message.getFromContactId()); 
			if (contactIcon != null) {
				contactIconView.setImageBitmap(contactIcon);
			}
		}
		
		return convertView;
	}

}
