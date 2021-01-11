package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;

public class HomePage {

    //Tabs
    @FindBy(id = "nav-files-tab")
    private WebElement filesTab;

    @FindBy(id = "nav-notes-tab")
    public WebElement notesTab;

    @FindBy(id = "nav-credentials-tab")
    public WebElement credentialsTab;

    //Notes
    @FindBy(id = "addNote")
    public WebElement addNoteButton;

    @FindBy(id = "note-title")
    public WebElement inputNoteTitle;

    @FindBy(id = "note-description")
    private WebElement inputNoteDescription;

    @FindBy(id = "saveNoteChanges")
    public WebElement submitNoteButton;

    @FindBy(id = "noteTable")
    public WebElement noteTable;

    @FindBy(id="noteModal")
    public WebElement noteModal;

    //Credentials
    @FindBy(id = "addCredential")
    public WebElement addCredentialButton;

    @FindBy(id = "credential-url")
    public WebElement inputCredentialUrl;

    @FindBy(id = "credential-username")
    public WebElement inputCredentailUsername;

    @FindBy(id = "credential-password")
    public WebElement inputCredentailPassword;

    @FindBy(id = "credentialTable")
    public WebElement credentialTable;

    @FindBy(id="credentialModal")
    public WebElement credentialModal;

    @FindBy(id = "saveCredentialChanges")
    public WebElement submitCredentialButton;

    //Delete
    @FindBy(id="deleteModal")
    public WebElement deleteModal;

    public HomePage(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
    }

    //Switch
    public void switchToNotes() {
        this.notesTab.click();
    }

    public void switchToCredentials() {
        this.credentialsTab.click();
    }

    //Credential Operations
    public void addCredential() {
        addCredentialButton.click();
    }

    public List<WebElement> getCredentialElements() {
        return this.credentialTable.findElements(new By.ByXPath("tbody/tr"));
    }

    public List<String> getCredentialUrls() {
        List<String> res = new ArrayList<>();
        List<WebElement> elements = getCredentialElements();
        for (WebElement element : elements) {
            res.add(element.findElement(By.xpath("th")).getText());
        }
        return res;
    }

    public List<String> getCredentialUsernames() {
        List<String> res = new ArrayList<>();
        List<WebElement> elements = getCredentialElements();
        for (WebElement element : elements) {
            res.add(element.findElement(By.xpath("td[2]")).getText());
        }
        return res;
    }

    public List<String> getCredentialPasswords() {
        List<String> res = new ArrayList<>();
        List<WebElement> elements = getCredentialElements();
        for (WebElement element : elements) {
            res.add(element.findElement(By.xpath("td[3]")).getText());
        }
        return res;
    }

    public void editIthCredential(int idx) {
        WebElement ithElement = getCredentialElements().get(idx);
        ithElement.findElement(By.xpath("td[1]/button")).click();
    }

    public void deleteCredentialWithUrl(String url) {
        List<WebElement> elements = getCredentialElements();
        for (WebElement element : elements) {
            if (element.findElement(By.xpath("th")).getText().equals(url)) {
                element.findElement(By.xpath("td[1]/a")).click();
                break;
            }
        }
    }

    public void editCredentialUrl(String url) {
        inputCredentialUrl.clear();
        inputCredentialUrl.sendKeys(url);
    }

    public void editCredentialUsername(String username) {
        inputCredentailUsername.clear();;
        inputCredentailUsername.sendKeys(username);
    }

    public void editCredentialPassword(String password) {
        inputCredentailPassword.clear();
        inputCredentailPassword.sendKeys(password);
    }

    public void saveCredentialChanges() {
        this.submitCredentialButton.click();
    }

    public String getInputCredentialPassword() {
        return inputCredentailPassword.getAttribute("value");
    }


    //Note Operations
    public int getNumberOfNotes() {
        return this.noteTable.findElements(new By.ByXPath("tbody/tr")).size();
    }

    public void editFirstNote() {
        //this.noteTable.findElements(new By.ByTagName("tr")).get(0).findElements(new By.ByTagName("td")).get(0).findElement(new By.ByTagName("button")).click();
        this.noteTable.findElement(new By.ByXPath("tbody/tr[1]/td[1]/button")).click();
    }

    public void deleteFirstNote() {
        //this.noteTable.findElements(new By.ByTagName("tr")).get(0).findElements(new By.ByTagName("td")).get(0).findElement(new By.ByTagName("a")).click();
        this.noteTable.findElement(new By.ByXPath("tbody/tr[1]/td[1]/a")).click();
    }

    public WebElement getFirstNoteTitleElement() {
        //return this.noteTable.findElement(new By.ByTagName("tbody")).findElements(new By.ByTagName("tr")).get(0).findElement(new By.ByTagName("th"));
        return this.noteTable.findElement(new By.ByXPath("tbody/tr[1]/th"));
    }

    public String getFirstNoteTitle() {
        return this.getFirstNoteTitleElement().getText();
    }

    public String getFirstNoteDescription() {
        //return this.noteTable.findElement(new By.ByTagName("tbody")).findElements(new By.ByTagName("tr")).get(0).findElements(new By.ByTagName("td")).get(1).getText();
        return this.noteTable.findElement(new By.ByXPath("tbody/tr[1]/td[2]")).getText();
    }

    public void addNote() {
        this.addNoteButton.click();
    }

    public void saveNoteChanges() {
        this.submitNoteButton.click();
    }

    public WebElement getInputNoteTitleElement() {
        return this.inputNoteTitle;
    }

    public void editNoteTitle(String notetitle) {
        this.inputNoteTitle.clear();
        this.inputNoteTitle.sendKeys(notetitle);
    }

    public WebElement getInputNoteDescriptionElement() {
        return this.inputNoteDescription;
    }

    public void editNoteDescription(String notedescription) {
        this.inputNoteDescription.clear();
        this.inputNoteDescription.sendKeys(notedescription);
    }

    //delete
    public void confirmDelete() {
        this.deleteModal.findElement(new By.ById("delete-yes")).click();
    }
}
