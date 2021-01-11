package com.udacity.jwdnd.course1.cloudstorage.model;

public class CredentialDisplay {
    private Integer credentialid;
    private String url;
    private String username;
    private String encryptedPassword;
    private String decryptedPassword;

    public CredentialDisplay(Integer credentialid, String url, String username, String encryptedPassword, String decryptedPassword) {
        this.credentialid = credentialid;
        this.url = url;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = decryptedPassword;
    }

    public Integer getCredentialid() {
        return credentialid;
    }

    public void setCredentialid(Integer credentialid) {
        this.credentialid = credentialid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getDecryptedPassword() {
        return decryptedPassword;
    }

    public void setDecryptedPassword(String decryptedPassword) {
        this.decryptedPassword = decryptedPassword;
    }
}
