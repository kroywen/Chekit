package ca.chekit.android.parser;

import java.io.InputStream;

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
	
	public void parse(InputStream is) {
		Object data = readData(is);
		apiResponse.setData(data);
	}
	
	public abstract Object readData(InputStream is);

}
