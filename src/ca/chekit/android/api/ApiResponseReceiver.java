package ca.chekit.android.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ApiResponseReceiver extends BroadcastReceiver {
	
	private OnApiResponseListener listener;
	
	public ApiResponseReceiver(OnApiResponseListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (listener != null) {
			int apiStatus = intent.getIntExtra(ApiService.EXTRA_API_STATUS, 
				ApiService.API_STATUS_NONE);
			ApiResponse apiResponse = (ApiResponse) intent.getSerializableExtra(
				ApiService.EXTRA_API_RESPONSE);
			listener.onApiResponse(apiStatus, apiResponse);
		}
		
	}

}
