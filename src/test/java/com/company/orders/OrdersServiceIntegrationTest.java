package com.company.orders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.company.orders.model.OrderRequest;
import com.company.orders.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Disabled
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
    public void init()
    {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }
	
	@Test
    public void invokeGetOrdersSuccessfully() throws Exception
    {
        this.mockMvc
                .perform(get(ORDERS_PATH + "?page=1&limit=2"))
                .andExpect(status().isOk());
    }

	@Test
    public void invokeCreateOrderSuccessfully() throws Exception
    {
		OrderRequest request = new OrderRequest();
		request.setOrigin(new String[] {ORIGIN_1,ORIGIN_2});
		request.setDestination(new String[] {DESTINATION_1,DESTINATION_2});
        
        this.mockMvc
                .perform(post((mapper.writeValueAsString(request))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance", is(152)))
                .andExpect(jsonPath("$.status", is(OrderStatus.UNASSIGNED.toString())));
    }
	
}
