package com.company.orders.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.company.orders.api.config.GoogleApiConfig;
import com.company.orders.api.model.DistanceMetricsResponse;

public class GoogleApiClientServiceTest {
	
	@InjectMocks
	private GoogleApiClientService service;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private GoogleApiConfig config;
	
	@Mock
	private ResponseEntity<DistanceMetricsResponse> response;
	
	@Mock
	private DistanceMetricsResponse responseBody;
	
	private String origins, destinations;
	
	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		origins = "14.492447,121.014916";
		destinations= "14.493829,121.014562";
		when(config.getContextPath()).thenReturn("/orders");
		when(config.getHostUrl()).thenReturn("http://localhost");
		when(config.getKey()).thenReturn("xxxx");
	}
	
	@Test
	public void shouldReturnResponseBody() {
		when(responseBody.getStatus()).thenReturn("OK");
		when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		when(response.getBody()).thenReturn(responseBody);
		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(DistanceMetricsResponse.class))).thenReturn(response);
		DistanceMetricsResponse result = service.getDistanceMetrics(origins, destinations);
		assertNotNull(result);
		assertEquals("OK", result.getStatus());
	}
	
	@Test
	public void shouldThrowRestClientExceptionWhenFailedStatus() {
		when(responseBody.getStatus()).thenReturn("FAILED");
		when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		when(response.getBody()).thenReturn(responseBody);
		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(DistanceMetricsResponse.class))).thenReturn(response);
		assertThrows(RestClientException.class, () -> {
			service.getDistanceMetrics(origins, destinations);

		});
	}
	
	@Test
	public void shouldThrowRestClientExceptionWhenStatusCodeNot200() {
		when(response.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);
		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(DistanceMetricsResponse.class))).thenReturn(response);
		assertThrows(RestClientException.class, () -> {
			service.getDistanceMetrics(origins, destinations);

		});
	}

}
