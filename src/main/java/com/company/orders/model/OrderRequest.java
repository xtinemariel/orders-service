package com.company.orders.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OrderRequest {

	@NotNull
	@Size(min = 2, max = 2)
	private String [] origin;
	
	@NotNull
	@Size(min = 2, max = 2)
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
