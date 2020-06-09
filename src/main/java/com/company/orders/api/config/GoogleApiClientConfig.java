package com.company.orders.api.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GoogleApiClientConfig {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, GoogleApiConfig googleApiConfig) {
		RestTemplate restTemplate = restTemplateBuilder
                .setReadTimeout(Duration.ofMillis(googleApiConfig.getReadTimeout()))
                .setConnectTimeout(Duration.ofMillis(googleApiConfig.getConnectionTimeout()))
                .build();
		return restTemplate;
	}

}
