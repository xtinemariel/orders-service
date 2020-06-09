package com.company.orders.exception;

public class OrderTakenException extends RuntimeException {

	private static final long serialVersionUID = -4366658043436759583L;

	public OrderTakenException(String message) {
		super(message);
	}

}
