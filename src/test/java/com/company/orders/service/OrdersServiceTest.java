package com.company.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.company.orders.api.model.DistanceMetricsResponse;
import com.company.orders.api.service.GoogleApiClientService;
import com.company.orders.exception.GoogleApiClientException;
import com.company.orders.exception.OrderTakenException;
import com.company.orders.model.OrderData;
import com.company.orders.model.OrderRequest;
import com.company.orders.model.OrderStatus;
import com.company.orders.model.TakeOrderRequest;
import com.company.orders.repository.OrdersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class OrdersServiceTest {

	@InjectMocks
	private OrdersService service;

	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private GoogleApiClientService googleApiClientService;
	
	@Mock
	private Page<OrderData> orderPage;

	private OrderRequest request;

	private DistanceMetricsResponse response;
	
	private OrderData order;

	private ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		request = new OrderRequest();
		request.setOrigin(new String[] { "14.492447", "121.014916" });
		request.setDestination(new String[] { "14.493829", "121.014562" });
		
		order = new OrderData();
        order.setDistance(152);
        order.setStatus(OrderStatus.UNASSIGNED);
	}

	@Test
	public void shouldCreateOrder() {
		response = setupResponse("{ \"destination_addresses\": [ \"25 E Rodriguez Ave\" ], \"origin_addresses\": [ \"15415 E Rodriguez Ave\" ], \"rows\": [ { \"elements\": [ { \"distance\": { \"text\": \"0.2 km\", \"value\": 173 }, \"duration\": { \"text\": \"1 min\", \"value\": 69 }, \"status\": \"OK\" } ] } ], \"status\": \"OK\" }");
		when(googleApiClientService.getDistanceMetrics(anyString(), anyString())).thenReturn(response);
		when(ordersRepository.saveAndFlush(any())).thenReturn(order);

		OrderData result = service.createOrder(request);
		assertEquals(OrderStatus.UNASSIGNED, result.getStatus());
		assertEquals(Integer.valueOf(152), result.getDistance());
	}

	@Test
	public void shouldNotCreateOrderWhenDistancesIsEmpty() {
		response = setupResponse("{ \"destination_addresses\": [ \"25 E Rodriguez Ave\" ], \"origin_addresses\": [ \"15415 E Rodriguez Ave\" ], \"rows\": [ { \"elements\": [ ] } ], \"status\": \"OK\" }");;
		when(googleApiClientService.getDistanceMetrics(anyString(), anyString())).thenReturn(response);
		verify(ordersRepository, never()).saveAndFlush(any());
		assertThrows(GoogleApiClientException.class, () -> {
			service.createOrder(request);
		});
	}
	
	@Test
	public void shouldNotCreateOrderWhenStatusIsNotOk() {
		response = setupResponse("{ \"status\": \"DENIED\" }");
		when(googleApiClientService.getDistanceMetrics(anyString(), anyString())).thenReturn(response);
		verify(ordersRepository, never()).saveAndFlush(any());
		assertThrows(GoogleApiClientException.class, () -> {
			service.createOrder(request);
		});
	}
	
	@Test
	public void shouldUpdateOrder() {
		when(ordersRepository.getOne(any())).thenReturn(order);
		OrderData updatedOrder = new OrderData();
		updatedOrder.setDistance(152);
        updatedOrder.setStatus(OrderStatus.TAKEN);
		when(ordersRepository.saveAndFlush(any())).thenReturn(updatedOrder);
		
		OrderData result = service.updateOrder(1L, new TakeOrderRequest());
		assertEquals(OrderStatus.TAKEN, result.getStatus());
	}
	
	@Test
	public void shouldNotUpdateWhenOrderIsTaken() {
		order.setStatus(OrderStatus.TAKEN);
		when(ordersRepository.getOne(any())).thenReturn(order);
		assertThrows(OrderTakenException.class, () -> {
			service.updateOrder(1L, new TakeOrderRequest());
		});
	}
	
	@Test
	public void shouldNotUpdateWhenOrderIdNotFound() {
		when(ordersRepository.getOne(any())).thenThrow(EntityNotFoundException.class);
		assertThrows(EntityNotFoundException.class, () -> {
			service.updateOrder(1L, new TakeOrderRequest());
		});
	}
	
	@Test
	public void shouldGetOrders() {
		List<OrderData> orders = getSampleOrders();
		
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(orderPage.getContent()).thenReturn(orders);
        when(ordersRepository.findAll(pageableCaptor.capture())).thenReturn(orderPage);
        
        List<OrderData> result = service.getOrders(1, 3);

        PageRequest pageable = (PageRequest) pageableCaptor.getValue();
        verify(ordersRepository).findAll(pageableCaptor.capture());

        assertEquals(0, pageable.getPageNumber());
        assertEquals(3, pageable.getPageSize());
        assertEquals(3, result.size());
	}
	
	@Test
	public void shouldReturnEmptyListIfNoOrders() {
        
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(orderPage.getContent()).thenReturn(Collections.emptyList());
        when(ordersRepository.findAll(pageableCaptor.capture())).thenReturn(orderPage);
        
        List<OrderData> result = service.getOrders(1, 3);
        assertEquals(0, result.size());
	}

	private List<OrderData> getSampleOrders() {
		List<OrderData> orders = new ArrayList<>();
		
		OrderData order2 = new OrderData();
        order2.setDistance(111);
        order2.setStatus(OrderStatus.UNASSIGNED);
        
        OrderData order3 = new OrderData();
        order3.setDistance(11122);
        order3.setStatus(OrderStatus.UNASSIGNED);
        
		orders.add(order);
		orders.add(order2);
		orders.add(order3);
		return orders;
	}
	
	public DistanceMetricsResponse setupResponse(String response) {
		try {
			return mapper.readValue(response, DistanceMetricsResponse.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
