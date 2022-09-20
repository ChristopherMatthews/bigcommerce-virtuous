package com.bigcommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bigcommerce.domain.Order;
import com.bigcommerce.domain.Orders;
import com.bigcommerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.virtuous.domain.Product;

@RestController
public class OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${url}")
	private String url;
	@Value("${x_auth_token}")
	private String xauth_token;
	
	@PostMapping("/webhook")
	public ResponseEntity<Order> addOrder( @RequestBody Order order) {
		if (order == null || order.getData() == null || order.getData().getId() == null) {
			return new ResponseEntity<Order>(order,HttpStatus.BAD_REQUEST);
		}
		getOrderById(order);
		return new ResponseEntity<Order>(order,HttpStatus.OK);
	}
	
	@GetMapping("/servicestatus")
	public ResponseEntity<String> serviceStatus() {
		
		return new ResponseEntity<String>("Service is up and running",HttpStatus.OK);
	}
	
	
	private ResponseEntity<Orders> getOrderById(Order order) {
		Orders orders = null;
		try {
			HttpHeaders headers = getHttpHeaders();
			HttpEntity<String> request = new HttpEntity<String>(headers);
			System.out.println("Calling BigCommerce api to get order details");
			ResponseEntity<Orders> response = restTemplate.exchange(url+order.getData().getId(), HttpMethod.GET, request, Orders.class);
			System.out.println("Successfully called BigCommerce api: " + response.getStatusCode());
			orders = response.getBody();
			String product_url = null;
			if (orders != null && orders.getProducts() != null) {
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String jsonContact = null;
				jsonContact = ow.writeValueAsString(orders);
				//System.out.println(jsonContact);
				product_url = orders.getProducts().getUrl();
			}
			
			getProduct(product_url, orders);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Orders>(orders,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Orders>(orders,HttpStatus.OK);
	}
	
	private ResponseEntity<Product> getProduct(String product_url, Orders order) {
			
		try {
			if (product_url == null) {
				return new ResponseEntity<Product>(new Product(),HttpStatus.BAD_REQUEST);
			}
			HttpHeaders headers = getHttpHeaders();
			HttpEntity<String> request = new HttpEntity<String>(headers);
			ResponseEntity<List<Product>> response = restTemplate.exchange(product_url, HttpMethod.GET, request, new ParameterizedTypeReference<List<Product>>() {
			});
			if (response != null && response.getBody() != null) {
				orderService.updateVirtuous(response.getBody(), order);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Product>(new Product(),HttpStatus.OK);
	}
	
	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Auth-Token", xauth_token);
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		return headers;
	}

}
