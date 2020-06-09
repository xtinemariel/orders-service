package com.company.orders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.company.orders.model.OrderRequest;
import com.company.orders.model.OrderStatus;
import com.company.orders.model.TakeOrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("integration")
public class OrdersServiceIntegrationTest {

	private static final String DESTINATION_2 = "121.014562";

	private static final String DESTINATION_1 = "14.493829";

	private static final String ORIGIN_2 = "121.014916";

	private static final String ORIGIN_1 = "14.492447";

	private static final String ORDERS_PATH = "/orders";

	private MockMvc mockMvc;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext applicationContext;

	@BeforeEach
	public void init() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}

	@Order(3)
	@Test
	public void invokeGetOrdersSuccessfully() throws Exception {
		this.mockMvc.perform(get(ORDERS_PATH + "?page=1&limit=2")).andExpect(status().isOk());
	}

	@Order(1)
	@Test
	public void invokeCreateOrderSuccessfully() throws Exception {
		OrderRequest request = new OrderRequest();
		request.setOrigin(new String[] { ORIGIN_1, ORIGIN_2 });
		request.setDestination(new String[] { DESTINATION_1, DESTINATION_2 });

		this.mockMvc
				.perform(post(ORDERS_PATH)
						.content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.distance", is(173)))
				.andExpect(jsonPath("$.status", is(OrderStatus.UNASSIGNED.toString())));
	}

	@Order(2)
	@Test
	public void invokeTakeOrderSuccessfully() throws Exception {
		TakeOrderRequest request = new TakeOrderRequest();
		request.setStatus("TAKEN");

		this.mockMvc
				.perform(patch(ORDERS_PATH + "/1")
						.content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("SUCCESS")));
	}

}
