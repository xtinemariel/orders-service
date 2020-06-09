package com.company.orders.api.service;

import static com.company.orders.utils.Constants.PARAM_DESTINATIONS;
import static com.company.orders.utils.Constants.PARAM_KEY;
import static com.company.orders.utils.Constants.PARAM_ORIGINS;
import static com.company.orders.utils.Constants.STATUS_OK;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.company.orders.api.config.GoogleApiConfig;
import com.company.orders.api.model.DistanceMetricsResponse;

@Service
public class GoogleApiClientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleApiClientService.class);
	private RestTemplate restTemplate;
	private GoogleApiConfig googleApiconfig;

	public GoogleApiClientService(RestTemplate restTemplate, GoogleApiConfig googleApiconfig) {
		this.restTemplate = restTemplate;
		this.googleApiconfig = googleApiconfig;
	}

	public DistanceMetricsResponse getDistanceMetrics(String origins, String destinations) {
		
		URI distanceMetricsUri = UriComponentsBuilder.fromHttpUrl(googleApiconfig.getHostUrl())
				.path(googleApiconfig.getContextPath())
				.queryParam(PARAM_ORIGINS, origins)
				.queryParam(PARAM_DESTINATIONS, destinations)
				.queryParam(PARAM_KEY, googleApiconfig.getKey())
				.build().toUri();
		
		LOGGER.info("Connecting to Google Distance Metrics API: {}", distanceMetricsUri);
		ResponseEntity<DistanceMetricsResponse> response = restTemplate.exchange(distanceMetricsUri, HttpMethod.GET, null,
				 DistanceMetricsResponse.class);
		
		if (isFailedResponse(response)) {
			throw new RestClientException("Error response received from Google Distance Metrics API.");
		}
		LOGGER.info("Successful response received");
		return response.getBody();
	}

	private boolean isFailedResponse(ResponseEntity<DistanceMetricsResponse> response) {
		return response == null || !HttpStatus.OK.equals(response.getStatusCode()) || 
				(response.getBody() != null && !STATUS_OK.equals(response.getBody().getStatus()));
	}

}
