package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EcommerceApplicationTest {
	private static UserController userController;
	private static CartController cartController;
	private static ItemController itemController;
	private static OrderController orderController;

	private static final UserRepository userRepository = mock(UserRepository.class);
	private static final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
	private static final CartRepository cartRepository = mock(CartRepository.class);
	private static final ItemRepository itemRepository = mock(ItemRepository.class);
	private static final OrderRepository orderRepository = mock(OrderRepository.class);


	@BeforeClass
	public static void setUp() {
		userController = new UserController();
		TestUtils.injectObjects(userController, "userRepository", userRepository);
		TestUtils.injectObjects(userController, "cartRepository", cartRepository);
		TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

		cartController = new CartController();
		TestUtils.injectObjects(cartController, "userRepository", userRepository);
		TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
		TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

		itemController = new ItemController();
		TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

		orderController = new OrderController();
		TestUtils.injectObjects(orderController, "userRepository", userRepository);
		TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
	}

	//User controller tests
	@Test
	public void TestCreateUser() {
		when(encoder.encode("Passw0rd!")).thenReturn("hashedPassword");
		final ResponseEntity<User> response = userController.createUser(getCreateUserRequest());

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		User u = response.getBody();
		assertNotNull(u);
		assertEquals(0, u.getId());
		assertEquals("Ryan", u.getUsername());
		assertEquals("hashedPassword", u.getPassword());
	}

	@Test
	public void TestGetUserById() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(getUser()));
		ResponseEntity<User> response = userController.findById(1L);

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		User u = response.getBody();
		assertEquals(1L, u.getId());
		assertEquals("Ryan", u.getUsername());
	}

	@Test
	public void TestGetUserByUsername() {
		when(userRepository.findByUsername("Ryan")).thenReturn(getUser());
		ResponseEntity<User> response = userController.findByUserName("Ryan");

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		User u = response.getBody();
		assertNotNull(u);
		assertEquals(1L, u.getId());
		assertEquals("Ryan", u.getUsername());
	}

	@Test
	public void TestGetUserByUsernameNotFound() {
		when(userRepository.findByUsername("Ryan")).thenReturn(null);
		ResponseEntity<User> response = userController.findByUserName("Ryan");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());

		User u = response.getBody();
		assertNull(u);
	}

	//Cart controller tests
	@Test
	public void TestAddToCart() {
		Cart c = getCart();
		User u = getUser();
		Item itm = getItem();
		c.setUser(u);
		u.setCart(c);

		when(userRepository.findByUsername("Ryan")).thenReturn(u);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(getItem()));
		ResponseEntity<Cart> response = cartController.addTocart(getCreateModifyCartRequest());

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(1L, cart.getId().longValue());
		assertEquals("Ryan", cart.getUser().getUsername());
		assertEquals(1L, cart.getUser().getId());
		assertEquals(Arrays.asList(itm, itm), cart.getItems());
		assertEquals(BigDecimal.valueOf(15.98), cart.getTotal());
	}

	@Test
	public void TestAddToCartUserNotFound() {
		Cart c = getCart();
		User u = getUser();
		c.setUser(u);
		u.setCart(c);

		when(userRepository.findByUsername("Ryan")).thenReturn(null);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(getItem()));
		ResponseEntity<Cart> response = cartController.addTocart(getCreateModifyCartRequest());

		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void TestAddToCartItemNotFound() {
		Cart c = getCart();
		User u = getUser();
		c.setUser(u);
		u.setCart(c);

		when(userRepository.findByUsername("Ryan")).thenReturn(u);
		when(itemRepository.findById(1L)).thenReturn(Optional.empty());
		ResponseEntity<Cart> response = cartController.addTocart(getCreateModifyCartRequest());

		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void TestRemoveFromCart() {
		Cart c = getCart();
		User u = getUser();
		Item itm = getItem();
		c.addItem(itm);
		c.addItem(itm);
		c.setUser(u);
		u.setCart(c);

		ModifyCartRequest modifyCartRequest = getCreateModifyCartRequest();
		modifyCartRequest.setQuantity(1);

		when(userRepository.findByUsername("Ryan")).thenReturn(u);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(itm));
		ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(1L, cart.getId().longValue());
		assertEquals("Ryan", cart.getUser().getUsername());
		assertEquals(1L, cart.getUser().getId());
		assertEquals(Arrays.asList(itm), cart.getItems());
		assertEquals(BigDecimal.valueOf(7.99), cart.getTotal());
	}

	//Item controller test
	@Test
	public void TestGetItems() {
		Item itm = getItem();
		when(itemRepository.findAll()).thenReturn(Arrays.asList(itm));
		ResponseEntity<List<Item>> response = itemController.getItems();

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		List<Item> items = response.getBody();
		assertEquals(Arrays.asList(itm), items);
	}

	@Test
	public void TestGetItemById() {
		Item itm = getItem();
		when(itemRepository.findById(1L)).thenReturn(Optional.of(itm));
		ResponseEntity<Item> response = itemController.getItemById(1L);

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		Item item = response.getBody();
		assertEquals(1L, item.getId().longValue());
		assertEquals("Sony PS5 Console", item.getName());
		assertEquals("Game Console", item.getDescription());
		assertEquals(BigDecimal.valueOf(7.99), item.getPrice());
	}

	@Test
	public void TestGetItemsByName() {
		Item itm1 = getItem();
		Item itm2 = getItem();
		itm2.setId(2L);

		when(itemRepository.findByName("Sony PS5 Console")).thenReturn(Arrays.asList(itm1, itm2));
		ResponseEntity<List<Item>> response = itemController.getItemsByName("Sony PS5 Console");

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		List<Item> items = response.getBody();
		assertEquals(1L, items.get(0).getId().longValue());
		assertEquals("Sony PS5 Console", items.get(0).getName());
		assertEquals(BigDecimal.valueOf(7.99), items.get(0).getPrice());
		assertEquals(2L, items.get(1).getId().longValue());
		assertEquals("Sony PS5 Console", items.get(1).getName());
		assertEquals(BigDecimal.valueOf(7.99), items.get(1).getPrice());
	}

	//Order controller test
	@Test
	public void TestSubmitOrder() {
		Cart c = getCart();
		User u = getUser();
		Item itm = getItem();
		c.addItem(itm);
		c.setUser(u);
		u.setCart(c);

		when(userRepository.findByUsername("Ryan")).thenReturn(u);
		ResponseEntity<UserOrder> response = orderController.submit("Ryan");
		UserOrder userOrder = response.getBody();
		assertEquals(c.getTotal(), userOrder.getTotal());
		assertEquals(c.getItems(), userOrder.getItems());
		assertEquals(c.getUser(), userOrder.getUser());
	}

	@Test
	public void TestGetOrdersForUser() {
		Cart c = getCart();
		User u = getUser();
		Item itm = getItem();
		c.addItem(itm);
		c.setUser(u);
		u.setCart(c);

		UserOrder order = UserOrder.createFromCart(u.getCart());

		when(userRepository.findByUsername("Ryan")).thenReturn(u);
		when(orderRepository.findByUser(u)).thenReturn(Collections.singletonList(order));
		ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Ryan");
		List<UserOrder> orders = response.getBody();
		assertEquals(c.getTotal(), orders.get(0).getTotal());
		assertEquals(c.getItems(), orders.get(0).getItems());
		assertEquals(c.getUser(), orders.get(0).getUser());
	}

	private CreateUserRequest getCreateUserRequest() {
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername("Ryan");
		createUserRequest.setPassword("Passw0rd!");
		createUserRequest.setConfirmPassword("Passw0rd!");
		return createUserRequest;
	}

	private ModifyCartRequest getCreateModifyCartRequest() {
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(2);
		modifyCartRequest.setUsername("Ryan");
		return modifyCartRequest;
	}

	private User getUser() {
		User u = new User();
		u.setId(1);
		u.setUsername("Ryan");
		u.setPassword("Passw0rd!");
		//u.setCart(getCart());
		return u;
	}

	private Cart getCart() {
		Cart cart = new Cart();
		cart.setId(1L);
		//cart.setUser(getUser());
		//cart.setTotal(BigDecimal.valueOf(7.99));
		//cart.addItem(getItem().get());
		return cart;
	}

	private Item getItem() {
		Item item = new Item();
		item.setId(1L);
		item.setName("Sony PS5 Console");
		item.setDescription("Game Console");
		item.setPrice(BigDecimal.valueOf(7.99));
		return item;
	}
}
