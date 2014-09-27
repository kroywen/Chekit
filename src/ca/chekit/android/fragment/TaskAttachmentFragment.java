package ca.chekit.android.fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.adapter.AttachmentAdapter;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.api.ApiResponse;
import ca.chekit.android.api.ApiService;
import ca.chekit.android.model.Attachment;
import ca.chekit.android.screen.BaseScreen;
import ca.chekit.android.screen.TaskDetailsScreen;
import ca.chekit.android.storage.Settings;
import ca.chekit.android.util.Utilities;

public class TaskAttachmentFragment extends TaskFragment implements OnItemClickListener {

	private ListView list;
	private TextView empty;
	
	private AttachmentAdapter adapter;
	private String filename;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.task_attachments_fragment, null);
		initializeViews(rootView);
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (isTaskAttachmentsLoaded()) {
			updateViews();
		} else {
			loadTaskAttachments();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Attachment attachment = adapter.getItem(position);
		File docDir = new File(Environment.getExternalStorageDirectory() + "/chekit/");
		docDir.mkdirs();
		File file = new File(docDir, attachment.getFilename());
		if (file.exists()) {
			try {
				openFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			filename = attachment.getFilename();
			new DownloadFileTask(attachment).execute();
		}
	}
	
	private void initializeViews(View rootView) {
		list = (ListView) rootView.findViewById(R.id.list);
		list.setOnItemClickListener(this);
		empty = (TextView) rootView.findViewById(R.id.empty);
	}
	
	private void updateViews() {
		List<Attachment> attachments = getTaskAttachments();
		if (Utilities.isEmpty(attachments)) {
			list.setVisibility(View.INVISIBLE);
			empty.setVisibility(View.VISIBLE);
		} else {
			adapter = new AttachmentAdapter(this, attachments);
			list.setAdapter(adapter);
			empty.setVisibility(View.INVISIBLE);
			list.setVisibility(View.VISIBLE);
		}
	}
	
	private List<Attachment> getTaskAttachments() {
		return ((TaskDetailsScreen) getActivity()).getTaskAttachments();
	}
	
	private void setTaskAttachments(List<Attachment> attachments) {
		((TaskDetailsScreen) getActivity()).setTaskAttachments(attachments);
	}
	
	private boolean isTaskAttachmentsLoaded() {
		return ((TaskDetailsScreen) getActivity()).isTaskAttachmentsLoaded();
	}
	
	private void setTaskAttachmentsLoaded(boolean taskAttachmentsLoaded) {
		((TaskDetailsScreen) getActivity()).setTaskAttachmentsLoaded(taskAttachmentsLoaded);
	}
	
	private void loadTaskAttachments() {
		((TaskDetailsScreen) getActivity()).loadTaskAttachments();
	}
	
	@SuppressWarnings("unchecked")
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_ATTACHMENTS.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK) {
							List<Attachment> attachments = (ArrayList<Attachment>) apiResponse.getData();
							setTaskAttachments(attachments);
							setTaskAttachmentsLoaded(true);
							updateViews();
						}
					}
				}
			}
		}
	}
	
	private void openFile(File url) throws IOException {
        File file = url;
        Uri uri = Uri.fromFile(file);
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            intent.setDataAndType(uri, "application/zip");
        } else if(url.toString().contains(".rtf")) {
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        PackageManager manager = getActivity().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
        	startActivity(intent);
        } else {
        	((BaseScreen) getActivity()).showInfoDialog(R.string.error, R.string.no_activity_found);
        }
    }
	
	class DownloadFileTask extends AsyncTask<Void, Void, Void> implements OnCancelListener {
		
		private ProgressDialog dialog;
		private Attachment attachment;
		private File file;
		
		public DownloadFileTask(Attachment attachment) {
			this.attachment = attachment;
    		dialog = new ProgressDialog(getActivity());
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.downloading));
    		dialog.setIndeterminate(true);
    		dialog.setOnCancelListener(this);
		}
		
		@Override
    	protected void onPreExecute() {
    		dialog.show();
    	}

		@Override
		public void onCancel(DialogInterface dialog) {
			cancel(true);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String url = ApiData.BASE_URL + String.format(ApiData.COMMAND_ATTACHMENT, getTaskId(), attachment.getId());
			AndroidHttpClient client = AndroidHttpClient.newInstance(
				System.getProperty("http.agent"), getActivity());
			HttpGet request = new HttpGet(url);
			
			Settings settings = new Settings(getActivity());
			String auth = settings.getString(Settings.AUTH);
			if (!TextUtils.isEmpty(auth)) {
				request.addHeader("Authorization", "Basic " + auth);
			}
			
			HttpResponse response = null;
			try {
				response = client.execute(request);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream is = entity.getContent();
					
					File docDir = new File(Environment.getExternalStorageDirectory() + "/chekit/");
					docDir.mkdirs();
					
					file = new File(docDir, filename);
					OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
					int bufferSize = 1024;
					byte[] buffer = new byte[bufferSize];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
					    os.write(buffer, 0, len);
					}
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public void onPostExecute(Void result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (file != null && file.exists()) {
				try {
					openFile(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
