package com.company.orders.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OrderRequest {

	@NotNull(message = "Origin is a required parameter")
	@Size(min = 2, max = 2, message = "Invalid length for origin")
	private String [] origin;
	
	@NotNull(message = "Destination is a required parameter")
	@Size(min = 2, max = 2, message = "Invalid length for destination")
	private String [] destination;
	
	public String[] getOrigin() {
		return origin;
	}
	public void setOrigin(String[] origin) {
		this.origin = origin;
	}
	public String[] getDestination() {
		return destination;
	}
	public void setDestination(String[] destination) {
		this.destination = destination;
	}
	
	
}
