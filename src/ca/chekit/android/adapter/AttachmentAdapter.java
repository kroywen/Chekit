package ca.chekit.android.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.fragment.TaskAttachmentFragment;
import ca.chekit.android.model.Attachment;
import ca.chekit.android.util.Utilities;

public class AttachmentAdapter extends BaseAdapter {
	
	private TaskAttachmentFragment fragment;
	private List<Attachment> attachments;
	
	public AttachmentAdapter(TaskAttachmentFragment fragment, List<Attachment> attachments) {
		this.fragment = fragment;
		this.attachments = attachments;
	}

	@Override
	public int getCount() {
		return attachments.size();
	}

	@Override
	public Attachment getItem(int position) {
		return attachments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return attachments.get(position).getId();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.attachment_list_item, null);
		}
		
		final Attachment attachment = getItem(position);
		
		TextView fileName = (TextView) convertView.findViewById(R.id.fileName);
		fileName.setText(attachment.getFilename());
		
		TextView createdDate = (TextView) convertView.findViewById(R.id.createdDateView);
		createdDate.setText(Utilities.convertDate(attachment.getDateCreated(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MM_dd));
		
		TextView createdTime = (TextView) convertView.findViewById(R.id.createdTimeView);
		createdTime.setText(Utilities.convertDate(attachment.getDateCreated(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.hh_mm_a));
		
		return convertView;
	}

}
