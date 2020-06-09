package com.company.orders.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.company.orders.exception.RestResponseExceptionHandler;
import com.company.orders.model.OrderData;
import com.company.orders.model.OrderRequest;
import com.company.orders.model.OrderStatus;
import com.company.orders.model.TakeOrderRequest;
import com.company.orders.service.OrdersService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrdersControllerTest {

	private static final String DESTINATION_2 = "121.014562";

	private static final String DESTINATION_1 = "14.493829";

	private static final String ORIGIN_2 = "121.014916";

	private static final String ORIGIN_1 = "14.492447";

	private static final String ORDERS_PATH = "/orders";

	@InjectMocks
	private OrdersController controller;

	@Mock
	private OrdersService ordersService;

	@Mock
	private RestResponseExceptionHandler exceptionHandler;

	private OrderData order;

	private MockMvc mvc;

	private ObjectMapper mapper = new ObjectMapper();

	private OrderRequest request = new OrderRequest();

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		order = new OrderData();
		order.setDistance(152);
		order.setStatus(OrderStatus.UNASSIGNED);
	}

	@Test
	public void createSuccess() throws Exception {
		request.setOrigin(new String[] { ORIGIN_1, ORIGIN_2 });
		request.setDestination(new String[] { DESTINATION_1, DESTINATION_2 });
		when(ordersService.createOrder(any(OrderRequest.class))).thenReturn(order);
		mvc.perform(
				post(ORDERS_PATH)
				.content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.distance", is(152)))
				.andExpect(jsonPath("$.status", is(OrderStatus.UNASSIGNED.toString())));
	}

	@Test
	public void createShouldReturn400InvalidOrigin() throws Exception {
		request.setOrigin(new String[] { ORIGIN_1 });
		request.setDestination(new String[] { DESTINATION_1, DESTINATION_2 });
		when(ordersService.createOrder(any(OrderRequest.class))).thenReturn(order);
		mvc.perform(
				post(ORDERS_PATH)
				.content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createShouldReturn400InvalidDestination() throws Exception {
		request.setOrigin(new String[] { ORIGIN_1, ORIGIN_2 });
		request.setDestination(new String[] { DESTINATION_1 });
		when(ordersService.createOrder(any(OrderRequest.class))).thenReturn(order);
		mvc.perform(
				post(ORDERS_PATH).content(mapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void takeOrderSuccess() throws Exception {
		TakeOrderRequest request = new TakeOrderRequest();
		request.setStatus("TAKEN");
		when(ordersService.updateOrder(eq(1L), any(TakeOrderRequest.class))).thenReturn(order);
		mvc.perform(patch(ORDERS_PATH + "/{id}", 1L)
				.content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("SUCCESS")));
	}

	@Test
	public void getOrdersSuccess() throws Exception {
		List<OrderData> result = new ArrayList<>();
		result.add(order);
		when(ordersService.getOrders(any(Integer.class), any(Integer.class))).thenReturn(result);
		mvc.perform(get(ORDERS_PATH + "?page=1&limit=5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].distance", is(152)))
				.andExpect(jsonPath("$[0].status", is(OrderStatus.UNASSIGNED.toString())));
	}

	@Test
	public void getOrdersShouldReturn400InvalidPage() throws Exception {
		List<OrderData> result = new ArrayList<>();
		result.add(order);
		when(ordersService.getOrders(any(Integer.class), any(Integer.class))).thenReturn(result);
		mvc.perform(get(ORDERS_PATH + "?page=z&limit=5"))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getOrdersShouldReturn400InvalidLimit() throws Exception {
		List<OrderData> result = new ArrayList<>();
		result.add(order);
		when(ordersService.getOrders(any(Integer.class), any(Integer.class))).thenReturn(result);
		mvc.perform(get(ORDERS_PATH + "?page=1&limit=z"))
			.andExpect(status().isBadRequest());
	}
}
