package ca.chekit.android.parser;

import java.io.InputStream;

import android.content.Context;
import ca.chekit.android.api.ApiResponse;

public abstract class ApiParser {
	
	protected ApiResponse apiResponse;
	
	public ApiParser() {
		apiResponse = new ApiResponse();
	}
	
	public ApiResponse getApiResponse() {
		return apiResponse;
	}
	
	public void setApiResponse(ApiResponse apiResponse) {
		this.apiResponse = apiResponse;
	}
	
	public void parse(Context context, InputStream is) {
		Object data = readData(context, is);
		apiResponse.setData(data);
	}
	
	public abstract Object readData(Context context, InputStream is);

}
