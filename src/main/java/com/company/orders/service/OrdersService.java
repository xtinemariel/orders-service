package com.company.orders.service;

import static com.company.orders.utils.Constants.STATUS_OK;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.orders.api.model.Distance;
import com.company.orders.api.model.DistanceMetricsResponse;
import com.company.orders.api.model.Element;
import com.company.orders.api.model.Row;
import com.company.orders.api.service.GoogleApiClientService;
import com.company.orders.exception.GoogleApiClientException;
import com.company.orders.exception.OrderTakenException;
import com.company.orders.model.OrderData;
import com.company.orders.model.OrderRequest;
import com.company.orders.model.OrderStatus;
import com.company.orders.model.TakeOrderRequest;
import com.company.orders.repository.OrdersRepository;

@Service
public class OrdersService {

	private OrdersRepository ordersRepository;

	private GoogleApiClientService googleApiClientService;

	public OrdersService(OrdersRepository ordersRepository, GoogleApiClientService googleApiClientService) {
		this.ordersRepository = ordersRepository;
		this.googleApiClientService = googleApiClientService;
	}

	public OrderData createOrder(OrderRequest orderRequest) {

		DistanceMetricsResponse response = googleApiClientService.getDistanceMetrics(
				String.join(",", orderRequest.getOrigin()), String.join(",", orderRequest.getDestination()));

		if (response != null && STATUS_OK.equals(response.getStatus())) {
			List<Integer> distances = response.getRows().stream().map(Row::getElements).flatMap(Collection::stream)
					.filter(e -> STATUS_OK.equals(e.getStatus())).map(Element::getDistance).map(Distance::getValue)
					.collect(Collectors.toList());

			if (!distances.isEmpty()) {
				Integer distance = distances.stream().collect(Collectors.summingInt(Integer::intValue));
				OrderData order = new OrderData();
		        order.setDistance(distance);
		        order.setStatus(OrderStatus.UNASSIGNED);
				return ordersRepository.saveAndFlush(order);
			}
		}
		throw new GoogleApiClientException("Distance cannot be retrieved for order.");

	}

	@Transactional
	public OrderData updateOrder(Long id, TakeOrderRequest request) {
		OrderData order = ordersRepository.getOne(id);

		if (OrderStatus.TAKEN.equals(order.getStatus())) {
			throw new OrderTakenException("Order already taken.");
		}
		order.setStatus(OrderStatus.TAKEN);
		return ordersRepository.saveAndFlush(order);
	}

	public List<OrderData> getOrders(Integer page, Integer limit) {
		return ordersRepository.findAll(PageRequest.of(page - 1, limit)).getContent();
	}
}
