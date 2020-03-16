package com.medas.rewamp.reportservice.business.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Jan 8, 2020
 *
 * @param <T>
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;
	
	public ApiResponse(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}

	public ApiResponse(T data) {
		super();
		this.data = data;
		this.success = true;
	}

	public ApiResponse(boolean success) {
		super();
		this.success = success;
	}
}
