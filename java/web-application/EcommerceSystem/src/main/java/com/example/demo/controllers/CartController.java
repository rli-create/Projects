package com.example.demo.controllers;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;

import com.splunk.TcpInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private static final Logger logger = LoggerFactory.getLogger(CartController.class);
	//@Autowired
	//TcpInput splunkLogger;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			logger.error(String.format("Error:when adding item to cart: user %s not found", request.getUsername()));
			/*try {
				splunkLogger.submit(String.format("Error:when adding item to cart: user %s not found", request.getUsername()));
			}
			catch (IOException e) {
				logger.warn("Failed to send log to splunk");
			}*/
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.error(String.format("Error when adding item to cart: item %s not found", request.getItemId()));
			/*try {
				splunkLogger.submit(String.format("Error when adding item to cart: item %s not found", request.getItemId()));
			}
			catch (IOException e) {
				logger.warn("Failed to send log to splunk");
			}*/
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		logger.info(String.format("Successfully add item: %s to user: %s cart", request.getItemId(), user.getUsername()));
		/*try {
			splunkLogger.submit(String.format("Successfully add item: %s to user: %s cart", request.getItemId(), user.getUsername()));
		}
		catch (IOException e) {
			logger.warn("Failed to send log to splunk");
		}*/
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request){
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			logger.error(String.format("Error when removing item from cart: user %s not found", user.getUsername()));
			/*try {
				splunkLogger.submit(String.format("Error when removing item from cart: user %s not found", user.getUsername()));
			}
			catch (IOException e) {
				logger.warn("Failed to send log to splunk");
			}*/
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.error(String.format("Error when removing item from cart: item %s not found", item.get().getId()));
			/*try {
				splunkLogger.submit(String.format("Error when removing item from cart: item %s not found", item.get().getId()));
			}
			catch (IOException e) {
				logger.warn("Failed to send log to splunk");
			}*/
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		logger.info(String.format("Successfully removed item:%s from user:%s cart", item.get().getId(), request.getUsername()));
		/*try {
			splunkLogger.submit(String.format("Successfully removed item:%s from user:%s cart", item.get().getId(), request.getUsername()));
		}
		catch (IOException e) {
			logger.warn("Failed to send log to splunk");
		}*/
		return ResponseEntity.ok(cart);
	}
		
}
