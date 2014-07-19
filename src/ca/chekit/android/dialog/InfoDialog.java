package ca.chekit.android.dialog;

import ca.chekit.android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InfoDialog extends DialogFragment {
	
	private TextView titleView;
	private TextView textView;
	private Button okBtn;
	
	private String title;
	private String text;
	private OnClickListener okListener;
	
	private void initializeViews(View view) {
		titleView = (TextView) view.findViewById(R.id.titleView);
		titleView.setText(title);
		textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(text);
		okBtn = (Button) view.findViewById(R.id.okBtn);
		okBtn.setOnClickListener(okListener != null ? okListener : stdListener);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setOkListener(OnClickListener okListener) {
		this.okListener = okListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.info_dialog, null);
	    initializeViews(view);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setInverseBackgroundForced(true);
	    AlertDialog dialog = builder.create();
	    dialog.setView(view, 0, 0, 0, 0);
	    
	    return dialog;
	}
	
	View.OnClickListener stdListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

}
