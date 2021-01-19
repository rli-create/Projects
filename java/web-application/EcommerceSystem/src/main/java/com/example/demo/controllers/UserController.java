package com.example.demo.controllers;

import com.splunk.TcpInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	TcpInput splunkLogger;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			logger.error(String.format("User with name: %s not found", username));
			try {
				splunkLogger.submit(String.format("Error:User with name: %s not found", username));
			}
			catch (IOException e) {
				logger.warn("Failed to send log to splunk");
			}
			return ResponseEntity.notFound().build();
		}
		else {
			logger.info(String.format("Successfully found user: %s", username));
			try {
				splunkLogger.submit(String.format("Info:User with name: %s not found", username));
			}
			catch (IOException e) {
				logger.warn("Failed to send log to splunk");
			}
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if (createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			logger.error("User password length must be longer or equal to 7 and password and confirmPassword must be consistent");
			try {
				splunkLogger.submit("ERROR:created user failed, password length must be longer or equal to 7 and password and confirmPassword must be consistent");
			}
			catch (IOException e) {
				logger.warn("Failed to send log to splunk");
			}
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		logger.info(String.format("Successfully created user: %s with id %s", user.getUsername(), user.getId()));
		try {
			splunkLogger.submit(String.format("INFO:Successfully created user: %s with id %s", user.getUsername(), user.getId()));
		}
		catch (IOException e) {
			logger.warn("Failed to send log to splunk");
		}
		return ResponseEntity.ok(user);
	}
}
