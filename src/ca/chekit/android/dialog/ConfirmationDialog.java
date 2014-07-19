package ca.chekit.android.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ca.chekit.android.R;

public class ConfirmationDialog extends DialogFragment {
	
	private TextView textView;
	private Button okBtn;
	private Button cancelBtn;
	
	private String text;
	private String okText;
	private OnClickListener okListener;
	private OnClickListener cancelListener;
	
	private void initializeViews(View view) {
		textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(text);
		okBtn = (Button) view.findViewById(R.id.okBtn);
		if (!TextUtils.isEmpty(okText)) {
			okBtn.setText(okText);
		}
		okBtn.setOnClickListener(okListener != null ? okListener : stdListener);
		cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(cancelListener != null ? cancelListener : stdListener);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setOkListener(OnClickListener okListener) {
		this.okListener = okListener;
	}
	
	public void setOkListener(String okText, OnClickListener okListener) {
		this.okText = okText; 
		this.okListener = okListener;
	}
	
	public void setCancelListener(OnClickListener cancelListener) {
		this.cancelListener = cancelListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.confirmation_dialog, null);
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
