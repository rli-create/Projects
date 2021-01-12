package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignUpPage {
    @FindBy(id = "inputFirstName")
    private WebElement inputFirstName;

    @FindBy(id = "inputLastName")
    private WebElement inputLastName;

    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "submit")
    private WebElement submitButton;

    public SignUpPage (WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
    }

    public void setInputFirstName(String firstName) {
        this.inputFirstName.sendKeys(firstName);
    }

    public void setInputLastName(String lastname) {
        this.inputLastName.sendKeys(lastname);
    }

    public void setInputUsername(String username) {
        this.inputUsername.sendKeys(username);
    }

    public void setInputPassword(String password) {
        this.inputPassword.sendKeys(password);
    }

    public void signup() {
        this.submitButton.click();
    }

}
