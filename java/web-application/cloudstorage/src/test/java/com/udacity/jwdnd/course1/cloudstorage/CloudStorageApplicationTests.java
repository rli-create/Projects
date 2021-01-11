package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	private void signUp(SignUpPage signUpPage) {
		signUpPage.setInputFirstName("Ryan");
		signUpPage.setInputLastName("Li");
		signUpPage.setInputUsername("Aceflyer");
		signUpPage.setInputPassword("1");
		signUpPage.signup();
	}

	private void login(LoginPage loginPage) {
		loginPage.setInputUsername("Aceflyer");
		loginPage.setInputPassword("1");
		loginPage.login();
	}

	@Test
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}
	@Test
	public void getSignupPage() {
		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}

	@Test
	public void getHomePageWithoutSignup() {
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	public void testSignUp() {
		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		signUp(signUpPage);
		Assertions.assertTrue(signUpPage.getSuccessMessage().startsWith("You successfully signed up!"));
	}
	@Test
	public void testLoginLogout() {
		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		signUp(signUpPage);
		driver.get("http://localhost:" + this.port + "/login");
		LoginPage loginPage = new LoginPage(driver);
		login(loginPage);
		Assertions.assertEquals("Home", driver.getTitle());
		driver.get("http://localhost:" + this.port + "/logout");
		Assertions.assertEquals("Login", driver.getTitle());
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}
	@Test
	public void TestNotes() {
		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		signUp(signUpPage);
		driver.get("http://localhost:" + this.port + "/login");
		LoginPage loginPage = new LoginPage(driver);
		login(loginPage);
		driver.get("http://localhost:" + this.port + "/home");
		HomePage homePage = new HomePage(driver);
		ResultPage resultPage = new ResultPage(driver);
		WebElement notesTab = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.notesTab));
		homePage.switchToNotes();
		WebElement addNoteShowUp = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		//Add a note
		homePage.addNote();
		WebElement noteModalVisible = new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(homePage.noteModal));
		homePage.editNoteTitle("Hi");
		Boolean inputTitleAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteTitleElement(), "value"));
		homePage.editNoteDescription("Hello World!");
		Boolean inputDescriptionAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteDescriptionElement(), "value"));
		WebElement submit = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.submitNoteButton));
		homePage.saveNoteChanges();
		WebElement result = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
		resultPage.successContinue();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		WebElement addNoteButtonAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		homePage.switchToNotes();
		addNoteShowUp = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		Assertions.assertEquals("Hi", homePage.getFirstNoteTitle());
		Assertions.assertEquals("Hello World!", homePage.getFirstNoteDescription());

		//Edit a note
		homePage.editFirstNote();
		noteModalVisible = new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(homePage.noteModal));
		homePage.editNoteTitle("Hey");
		inputTitleAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteTitleElement(), "value"));
		homePage.editNoteDescription("How are you doing?");
		inputDescriptionAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteDescriptionElement(), "value"));
		submit = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.submitNoteButton));
		homePage.saveNoteChanges();
		result = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
		resultPage.successContinue();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		addNoteButtonAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		homePage.switchToNotes();
		addNoteShowUp = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		Assertions.assertEquals("Hey", homePage.getFirstNoteTitle());
		Assertions.assertEquals("How are you doing?", homePage.getFirstNoteDescription());

		//Delete a note
		homePage.deleteFirstNote();
		WebElement deleteModal = new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(homePage.deleteModal));
		homePage.confirmDelete();
		result = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
		resultPage.successContinue();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		addNoteButtonAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		homePage.switchToNotes();
		Assertions.assertEquals(0, homePage.getNumberOfNotes());
	}

	@Test
	public void TestCredentials() {
		String[] urls = new String[]{"test1.com", "test2.com", "test3.com", "test4.com", "test5.com", "test6.com"};
		String[] usernames = new String[]{"Ryan1", "Ryan2", "Ryan3", "Ryan4", "Ryan5", "Ryan6"};
		String[] passwords = new String[]{"1", "2", "3", "4", "5", "6"};

		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		signUp(signUpPage);
		driver.get("http://localhost:" + this.port + "/login");
		LoginPage loginPage = new LoginPage(driver);
		login(loginPage);
		driver.get("http://localhost:" + this.port + "/home");
		HomePage homePage = new HomePage(driver);
		ResultPage resultPage = new ResultPage(driver);
		WebElement credentialsTab = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.credentialsTab));
		homePage.switchToCredentials();
		WebElement addCredentialsShowUp = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));

		//Add a set of credentials
		for (int i = 0;i < urls.length;i++) {
			homePage.addCredential();
			WebElement credentialModalVisible = new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(homePage.credentialModal));
			homePage.editCredentialUrl(urls[i]);
			Boolean inputUrlAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBeNotEmpty(homePage.inputCredentialUrl, "value"));
			homePage.editCredentialUsername(usernames[i]);
			Boolean inputUsernameAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBeNotEmpty(homePage.inputCredentailUsername, "value"));
			homePage.editCredentialPassword(passwords[i]);
			Boolean inputPasswordAvailable = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBeNotEmpty(homePage.inputCredentailPassword, "value"));
			WebElement submit = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
			homePage.saveCredentialChanges();
			WebElement result = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
			resultPage.successContinue();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			homePage.switchToCredentials();
			WebElement addCredentialShowUp = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
		}
		List<String> displayUrls = homePage.getCredentialUrls();
		List<String> displayUsernames = homePage.getCredentialUsernames();
		List<String> displayPasswords= homePage.getCredentialPasswords();
		for (int i = 0;i < urls.length;i++) {
			Assertions.assertEquals(displayUrls.get(i), urls[i]);
			Assertions.assertEquals(displayUsernames.get(i), usernames[i]);
			Assertions.assertNotEquals(displayPasswords.get(i), passwords[i]);
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		//Test view/edit
		int[] idx = new int[]{0, 2, 4};
		for (int index : idx) {
			String password = homePage.getCredentialPasswords().get(index);
			homePage.editIthCredential(index);
			WebElement credentialModalVisible = new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(homePage.credentialModal));
			Assertions.assertEquals(passwords[index], homePage.getInputCredentialPassword());
			homePage.editCredentialPassword(passwords[index] + passwords[index]);
			Boolean inputCredential = new WebDriverWait(driver,10).until(ExpectedConditions.attributeToBe(homePage.inputCredentailPassword, "value", passwords[index] + passwords[index]));
			homePage.saveCredentialChanges();
			WebElement result = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
			resultPage.successContinue();
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			homePage.switchToCredentials();
			WebElement addCredentialShowUp = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
			Assertions.assertNotEquals(password, homePage.getCredentialPasswords().get(index));
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		//Test delete
		Set<String> toDelete = new HashSet<>(Arrays.asList("test1.com", "test3.com", "test5.com"));
		Iterator<WebElement> iterator = homePage.getCredentialElements().iterator();
		for (String url : toDelete) {
			homePage.deleteCredentialWithUrl(url);
			WebElement deleteModal = new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(homePage.deleteModal));
			homePage.confirmDelete();
			WebElement result = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
			resultPage.successContinue();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			homePage.switchToCredentials();
			WebElement addCredentialShowUp = new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
		}
		displayUrls = homePage.getCredentialUrls();
		Assertions.assertTrue(!displayUrls.contains("test1.com"));
		Assertions.assertTrue(!displayUrls.contains("test3.com"));
		Assertions.assertTrue(!displayUrls.contains("test5.com"));
		Assertions.assertEquals(3, homePage.getCredentialUrls().size());
	}
}
