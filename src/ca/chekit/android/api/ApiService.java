package ca.chekit.android.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import ca.chekit.android.parser.ApiParser;
import ca.chekit.android.parser.ParserFactory;
import ca.chekit.android.storage.Settings;

public class ApiService extends IntentService {
	
	public static final String TAG = ApiService.class.getSimpleName();
	
	public static final String ACTION_API_RESULT = "action_api_result";
	public static final String EXTRA_API_STATUS = "extra_api_status";
	public static final String EXTRA_API_RESPONSE = "extra_api_response";
	
	public static final int API_STATUS_NONE = -1;
	public static final int API_STATUS_SUCCESS = 0;
	public static final int API_STATUS_ERROR = 1;
	
	public ApiService() {
		this(ApiService.class.getSimpleName());
	}

	public ApiService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String command = intent.getAction();
		String method = extras.getString(ApiData.PARAM_METHOD);
		
		AndroidHttpClient client = AndroidHttpClient.newInstance(
			System.getProperty("http.agent"), this);
		HttpRequestBase request = getHttpRequest(method);
		
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		
		if (!(command.equalsIgnoreCase(ApiData.COMMAND_LOGIN) && method.equalsIgnoreCase(ApiData.METHOD_POST))) {
			Settings settings = new Settings(this);
			String auth = settings.getString(Settings.AUTH);
			if (!TextUtils.isEmpty(auth)) {
				request.addHeader("Authorization", "Basic " + auth);
			}
		}
		
		String url = createURL(command, extras);
		Log.d(TAG, method + ": " + url);
		request.setURI(URI.create(url));
		
		Object body = extras.get(ApiData.PARAM_BODY);
		if (body != null && request instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase r = (HttpEntityEnclosingRequestBase) request;
			if (ApiData.COMMAND_WORKTASK_SCHEDULED_STATUS.equalsIgnoreCase(command) ||
				ApiData.COMMAND_WORKTASK_STATUS.equalsIgnoreCase(command) ||
				ApiData.COMMAND_NOTES.equalsIgnoreCase(command) ||
				ApiData.COMMAND_NOTE.equalsIgnoreCase(command) ||
				ApiData.COMMAND_WORKTASK.equalsIgnoreCase(command) ||
				ApiData.COMMAND_PASSWORD_RECOVERY.equalsIgnoreCase(command) ||
				ApiData.COMMAND_CHANGE_PASSWORD.equalsIgnoreCase(command) ||
				ApiData.COMMAND_CHAT.equalsIgnoreCase(command) || 
				ApiData.COMMAND_PUSH_SERVICE.equalsIgnoreCase(command)) 
			{
				try {
					r.setEntity(new StringEntity((String) body, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (ApiData.COMMAND_ATTACHMENTS.equalsIgnoreCase(command)) {
				r.setEntity(new ByteArrayEntity((byte[]) body));
			}
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				ApiParser parser = ParserFactory.getParser(command, method);
				if (parser != null) {
					parser.parse(this, is);
					ApiResponse apiResponse = parser.getApiResponse();
					apiResponse.setStatus(response.getStatusLine().getStatusCode());
					apiResponse.setMethod(method);
					apiResponse.setRequestName(command);
					sendResult(API_STATUS_SUCCESS, apiResponse);
				}
				
				is.close();
			} else {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setStatus(response.getStatusLine().getStatusCode());
				apiResponse.setMethod(method);
				apiResponse.setRequestName(command);
				sendResult(API_STATUS_SUCCESS, apiResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			sendResult(API_STATUS_ERROR, null);
		} finally {
			client.close();
		}
	}
	
	private HttpRequestBase getHttpRequest(String method) {
		if (method.equalsIgnoreCase(ApiData.METHOD_GET)) {
			return new HttpGet();
		} else if (method.equalsIgnoreCase(ApiData.METHOD_POST)) {
			return new HttpPost();
		} else if (method.equalsIgnoreCase(ApiData.METHOD_PUT)) {
			return new HttpPut();
		} else if (method.equalsIgnoreCase(ApiData.METHOD_DELETE)) {
			return new HttpDelete();
		} else {
			return null;
		}
	}
	
	private void sendResult(int apiStatus, ApiResponse apiResponse) {
		Intent resultIntent = new Intent(ACTION_API_RESULT);
		resultIntent.putExtra(EXTRA_API_STATUS, apiStatus);
		resultIntent.putExtra(EXTRA_API_RESPONSE, apiResponse);
		LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
	}
	
	private String createURL(String command, Bundle params) {
		String url = ApiData.BASE_URL + command;
		if (params.containsKey(ApiData.PARAM_ID)) {
			long id = params.getLong(ApiData.PARAM_ID);
			if (params.containsKey(ApiData.PARAM_ID1)) {
				long id1 = params.getLong(ApiData.PARAM_ID1);
				url = String.format(url, id, id1);
			} else {
				url = String.format(url, id);
			}			
		}
		
		Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
		if (params != null) {
			Set<String> keys = params.keySet();
			if (keys != null && !keys.isEmpty()) {
				Iterator<String> i = keys.iterator();
				while (i.hasNext()) {
					String key = i.next();
					if (key.equalsIgnoreCase(ApiData.PARAM_METHOD) ||
						key.equalsIgnoreCase(ApiData.PARAM_BODY) ||
						key.equalsIgnoreCase(ApiData.PARAM_ID) ||
						key.equalsIgnoreCase(ApiData.PARAM_ID1))
					{
						continue;
					}
					String value = String.valueOf(params.get(key));
					uriBuilder.appendQueryParameter(key, value);
				}
			}
		}
		String result = uriBuilder.build().toString();
		return result;
	}

}
