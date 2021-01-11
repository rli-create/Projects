package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ResultPage {
    @FindBy(id = "success-continue")
    public WebElement successContinue;

    @FindBy(id = "fail-continue")
    public WebElement failContinue;

    public ResultPage(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
    }

    public void successContinue() {
        this.successContinue.click();
    }

    public void failContinue() {
        this.failContinue.click();
    }
}
