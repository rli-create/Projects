package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.JavascriptExecutor;
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

	private void signUp(SignUpPage signUpPage, String firstName, String lastName,String userName, String password) {
		signUpPage.setInputFirstName(firstName);
		signUpPage.setInputLastName(lastName);
		signUpPage.setInputUsername(userName);
		signUpPage.setInputPassword(password);
		signUpPage.signup();
	}

	private void login(LoginPage loginPage, String userName, String password) {
		loginPage.setInputUsername(userName);
		loginPage.setInputPassword(password);
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
		WebDriverWait wait = new WebDriverWait(driver,20);
		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		LoginPage loginPage = new LoginPage(driver);
		signUp(signUpPage, "Ryan", "Li", "Ace1", "1");
		wait.until(ExpectedConditions.visibilityOf(loginPage.successMessage));
		Assertions.assertEquals(loginPage.successMessage.getText(), "You have successfully signed up");
	}
	@Test
	public void testLoginLogout() {
		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		signUp(signUpPage, "Ryan", "Li", "Ace2", "1");
		driver.get("http://localhost:" + this.port + "/login");
		LoginPage loginPage = new LoginPage(driver);
		login(loginPage, "Ace2", "1");
		Assertions.assertEquals("Home", driver.getTitle());
		driver.get("http://localhost:" + this.port + "/logout");
		Assertions.assertEquals("Login", driver.getTitle());
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}
	@Test
	public void TestNotes() {
		WebDriverWait wait = new WebDriverWait(driver,10);
		JavascriptExecutor jse = (JavascriptExecutor) driver;

		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		signUp(signUpPage, "Ryan", "Li", "Ace3", "1");
		driver.get("http://localhost:" + this.port + "/login");
		LoginPage loginPage = new LoginPage(driver);
		login(loginPage, "Ace3", "1");
		driver.get("http://localhost:" + this.port + "/home");
		HomePage homePage = new HomePage(driver);
		ResultPage resultPage = new ResultPage(driver);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.notesTab));
		jse.executeScript("arguments[0].click()", homePage.notesTab);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		//Add a note
		homePage.addNote();
		wait.until(ExpectedConditions.visibilityOf(homePage.noteModal));
		homePage.editNoteTitle("Hi");
		wait.until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteTitleElement(), "value"));
		homePage.editNoteDescription("Hello World!");
		wait.until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteDescriptionElement(), "value"));
		wait.until(ExpectedConditions.elementToBeClickable(homePage.submitNoteButton));
		homePage.saveNoteChanges();
		wait.until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
		jse.executeScript("arguments[0].click()", resultPage.successContinue);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.notesTab));
		jse.executeScript("arguments[0].click()", homePage.notesTab);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		Assertions.assertEquals("Hi", homePage.getFirstNoteTitle());
		Assertions.assertEquals("Hello World!", homePage.getFirstNoteDescription());

		//Edit a note
		homePage.editFirstNote();
		wait.until(ExpectedConditions.visibilityOf(homePage.noteModal));
		homePage.editNoteTitle("Hey");
		wait.until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteTitleElement(), "value"));
		homePage.editNoteDescription("How are you doing?");
		wait.until(ExpectedConditions.attributeToBeNotEmpty(homePage.getInputNoteDescriptionElement(), "value"));
		wait.until(ExpectedConditions.elementToBeClickable(homePage.submitNoteButton));
		homePage.saveNoteChanges();
		wait.until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
		jse.executeScript("arguments[0].click()", resultPage.successContinue);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.notesTab));
		jse.executeScript("arguments[0].click()", homePage.notesTab);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.addNoteButton));
		Assertions.assertEquals("Hey", homePage.getFirstNoteTitle());
		Assertions.assertEquals("How are you doing?", homePage.getFirstNoteDescription());

		//Delete a note
		homePage.deleteFirstNote();
		wait.until(ExpectedConditions.visibilityOf(homePage.deleteModal));
		homePage.confirmDelete();
		wait.until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
		jse.executeScript("arguments[0].click()", resultPage.successContinue);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.notesTab));
		jse.executeScript("arguments[0].click()", homePage.notesTab);
		Assertions.assertEquals(0, homePage.getNumberOfNotes());
	}

	@Test
	public void TestCredentials() {
		String[] urls = new String[]{"test1.com", "test2.com", "test3.com", "test4.com", "test5.com", "test6.com"};
		String[] usernames = new String[]{"Ryan1", "Ryan2", "Ryan3", "Ryan4", "Ryan5", "Ryan6"};
		String[] passwords = new String[]{"1", "2", "3", "4", "5", "6"};
		WebDriverWait wait = new WebDriverWait(driver,10);
		JavascriptExecutor jse = (JavascriptExecutor) driver;

		driver.get("http://localhost:" + this.port + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		signUp(signUpPage, "Ryan", "Li", "Ace4", "1");
		driver.get("http://localhost:" + this.port + "/login");
		LoginPage loginPage = new LoginPage(driver);
		login(loginPage, "Ace4", "1");
		driver.get("http://localhost:" + this.port + "/home");
		HomePage homePage = new HomePage(driver);
		ResultPage resultPage = new ResultPage(driver);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.credentialsTab));
		//homePage.switchToCredentials();
		jse.executeScript("arguments[0].click()", homePage.credentialsTab);
		wait.until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));

		//Add a set of credentials
		for (int i = 0;i < urls.length;i++) {
			homePage.addCredential();
			wait.until(ExpectedConditions.visibilityOf(homePage.credentialModal));
			homePage.editCredentialUrl(urls[i]);
			wait.until(ExpectedConditions.attributeToBeNotEmpty(homePage.inputCredentialUrl, "value"));
			homePage.editCredentialUsername(usernames[i]);
			wait.until(ExpectedConditions.attributeToBeNotEmpty(homePage.inputCredentailUsername, "value"));
			homePage.editCredentialPassword(passwords[i]);
			wait.until(ExpectedConditions.attributeToBeNotEmpty(homePage.inputCredentailPassword, "value"));
			wait.until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
			homePage.saveCredentialChanges();
			wait.until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
			jse.executeScript("arguments[0].click()", resultPage.successContinue);
			//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.elementToBeClickable(homePage.credentialsTab));
			jse.executeScript("arguments[0].click()", homePage.credentialsTab);
			wait.until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
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
			wait.until(ExpectedConditions.visibilityOf(homePage.credentialModal));
			Assertions.assertEquals(passwords[index], homePage.getInputCredentialPassword());
			homePage.editCredentialPassword(passwords[index] + passwords[index]);
			wait.until(ExpectedConditions.attributeToBe(homePage.inputCredentailPassword, "value", passwords[index] + passwords[index]));
			homePage.saveCredentialChanges();
			wait.until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
			jse.executeScript("arguments[0].click()", resultPage.successContinue);
			//driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.elementToBeClickable(homePage.credentialsTab));
			jse.executeScript("arguments[0].click()", homePage.credentialsTab);
			//homePage.switchToCredentials();
			wait.until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
			Assertions.assertNotEquals(password, homePage.getCredentialPasswords().get(index));
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		//Test delete
		Set<String> toDelete = new HashSet<>(Arrays.asList("test1.com", "test3.com", "test5.com"));
		Iterator<WebElement> iterator = homePage.getCredentialElements().iterator();
		for (String url : toDelete) {
			homePage.deleteCredentialWithUrl(url);
			wait.until(ExpectedConditions.visibilityOf(homePage.deleteModal));
			homePage.confirmDelete();
			wait.until(ExpectedConditions.elementToBeClickable(resultPage.successContinue));
			jse.executeScript("arguments[0].click()", resultPage.successContinue);
			wait.until(ExpectedConditions.elementToBeClickable(homePage.credentialsTab));
			jse.executeScript("arguments[0].click()", homePage.credentialsTab);
			wait.until(ExpectedConditions.elementToBeClickable(homePage.addCredentialButton));
		}
		displayUrls = homePage.getCredentialUrls();
		Assertions.assertTrue(!displayUrls.contains("test1.com"));
		Assertions.assertTrue(!displayUrls.contains("test3.com"));
		Assertions.assertTrue(!displayUrls.contains("test5.com"));
		Assertions.assertEquals(3, homePage.getCredentialUrls().size());
	}
}
