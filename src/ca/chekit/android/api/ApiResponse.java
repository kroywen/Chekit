package ca.chekit.android.api;

import java.io.Serializable;

public class ApiResponse implements Serializable {
	
	private static final long serialVersionUID = 2298834586169426687L;

	private int status;
	private String requestName;
	private String method;
	private Object data;
	
	public ApiResponse() {}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getRequestName() {
		return requestName;
	}
	
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
}
