package com.company.orders.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class TakeOrderRequest {

	@NotBlank
	@Pattern(regexp="^TAKEN$", message="Invalid status provided")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
