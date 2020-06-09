package com.company.orders.model;

public class SuccessResponse {

	private String status;

	public SuccessResponse (String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
