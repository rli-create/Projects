package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.form.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.CredentialDisplay;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public List<CredentialDisplay> getAllCredentialsForUser(Integer userid) {
        List<Credential> credentialList = this.credentialMapper.getUserCredentials(userid);
        return credentialList.stream().map(e ->
                new CredentialDisplay(
                        e.getCredentialid(), e.getUrl(), e.getUsername(), e.getPassword(),
                        encryptionService.decryptValue(e.getPassword(), e.getKey())))
                .collect(Collectors.toList());
    }

    public Credential getCredentialById(Integer credentialid) {
        return this.credentialMapper.get(credentialid);
    }

    public Integer createCredential(Credential credential) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), encodedKey);
        credential.setKey(encodedKey);
        credential.setPassword(encryptedPassword);
        return this.credentialMapper.insert(credential);
    }

    public Integer updateCredential(Credential oldCredential, CredentialForm credentialForm) {
        oldCredential.setUrl(credentialForm.getUrl());
        oldCredential.setUsername(credentialForm.getUsername());
        if (!oldCredential.getPassword().equals(credentialForm.getPassword())) {
            String newPassword = credentialForm.getPassword();
            String encryptedPassword = encryptionService.encryptValue(newPassword, oldCredential.getKey());
            oldCredential.setPassword(encryptedPassword);
        }
        return this.credentialMapper.update(oldCredential);
    }

    public Integer deleteCredential(Integer credentialid) {
        return this.credentialMapper.delete(credentialid);
    }
}
