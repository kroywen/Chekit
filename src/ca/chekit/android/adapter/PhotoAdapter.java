package ca.chekit.android.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ca.chekit.android.R;
import ca.chekit.android.model.Attachment;
import ca.chekit.android.util.Utilities;

public class PhotoAdapter extends BaseAdapter {
	
	private Context context;
	private List<Attachment> photos;
	private int size;
	
	public PhotoAdapter(Context context, List<Attachment> photos, int gridWidth) {
		this.context = context;
		this.photos = photos;
		calculatePhotoSize(gridWidth);
	}

	@Override
	public int getCount() {
		return photos.size();
	}

	@Override
	public Attachment getItem(int position) {
		return photos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return photos.get(position).getId();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.photo_list_item, null);
		}
		convertView.setLayoutParams(new AbsListView.LayoutParams(size, size));
		
		ImageView emptyView = (ImageView) convertView.findViewById(R.id.emptyView);
		ImageView photoView = (ImageView) convertView.findViewById(R.id.photoView);
		
		Attachment photo = getItem(position);
		photo.displayImage(context, photoView, emptyView);
		
		return convertView;
	}
	
	private void calculatePhotoSize(int width) {
		int padding = Utilities.dpToPx(context, 10);
		int spacing = Utilities.dpToPx(context, 5);
		size = (width - 2 * padding - 2 * spacing) / 3;
	}
	
	public void removePhoto(int position) {
		photos.remove(position);
		notifyDataSetChanged();
	}

}
