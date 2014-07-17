/**
 * 
 */
package com.findly.common;

/**
 * General purpose Response object that describes the process result and carry a typed pay-load.
 * @author Dhafir Moussa
 *
 */
public class Response<T> {

	/**
	 * Types pay-load data.
	 */
	private T data;
	
	//- TODO replace with enum SUCCESS,ERROR,WARNING,INFO
	private boolean success = true;
	
	//- TODO replace with proper reporting messages.
	private String message;

	
	public Response(){//- default}
		
	}
	
	public Response(T payload){//- default}
		this.data = payload;
	}
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
