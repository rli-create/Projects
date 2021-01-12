package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "submitButton")
    private WebElement submitButton;

    @FindBy(id = "success-message")
    public WebElement successMessage;

    public LoginPage(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
    }

    public void setInputUsername (String username) {
        this.inputUsername.sendKeys(username);
    }

    public void setInputPassword (String password) {
        this.inputPassword.sendKeys(password);
    }

    public void login() {
        this.submitButton.click();
    }
}
