package com.company.orders.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistanceMetricsResponse {

	@JsonProperty("destination_addresses")
	private List<String> destinationAddresses;
	
	@JsonProperty("origin_addresses")
	private List<String> originAddresses;
	
	private List<Row> rows;
	
	private String status;
	
	public List<String> getDestinationAddresses() {
		return destinationAddresses;
	}
	public void setDestinationAddresses(List<String> destinationAddresses) {
		this.destinationAddresses = destinationAddresses;
	}
	public List<String> getOriginAddresses() {
		return originAddresses;
	}
	public void setOriginAddresses(List<String> originAddresses) {
		this.originAddresses = originAddresses;
	}
	public List<Row> getRows() {
		return rows;
	}
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
	

}
