package com.example.backend.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {
    private final TextEncryptor encryptor;

    public EncryptionService(
            @Value("${app.encryption.password}") String password,
            @Value("${app.encryption.salt}") String salt)
    {
        this.encryptor = Encryptors.text(password, salt);
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        return encryptor.decrypt(encryptedText);
    }
}
