package com.example.salarymanagementsystem.utility;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Generates a key suitable for HS512
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated HS512 Key (Base64 Encoded): " + base64Key);
        // Example output length will be 88 characters for a 512-bit key
    }
}