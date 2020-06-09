package com.company.orders.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.orders.model.OrderData;
import com.company.orders.model.OrderRequest;
import com.company.orders.model.SuccessResponse;
import com.company.orders.model.TakeOrderRequest;
import com.company.orders.service.OrdersService;
import com.company.orders.utils.Constants;

@RestController
@Validated
@RequestMapping("/orders")
public class OrdersController {

	private OrdersService ordersService;
	
	public OrdersController (OrdersService ordersService)
	{
		this.ordersService = ordersService;
	}
	
	@PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrderData createOrder(@Valid @RequestBody OrderRequest orderRequest) {
		return ordersService.createOrder(orderRequest);
	}
	
	@PatchMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public SuccessResponse takeOrder(@PathVariable("id") @NotNull Long id, @Valid @RequestBody TakeOrderRequest request)
    {
        ordersService.updateOrder(id, request);
        return new SuccessResponse(Constants.STATUS_SUCCESS);
    }
	
	@GetMapping
    public List<OrderData> getOrders(@RequestParam @NotNull @Min(1) Integer page,
    		 @RequestParam @NotNull @Min(1) Integer limit)
    {
        return ordersService.getOrders(page, limit);
    }
}
