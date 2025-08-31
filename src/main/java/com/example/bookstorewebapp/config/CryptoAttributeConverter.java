package com.example.bookstorewebapp.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Converter(autoApply = false)
public class CryptoAttributeConverter implements AttributeConverter<String, String> {

    private static final String PREFIX = "ENCv1:";          // marker
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;            // 16 bytes tag
    private static final int IV_LEN = 12;                   // 12 bytes IV
    private static final SecureRandom RAND = new SecureRandom();

    private static volatile SecretKeySpec KEY;

    private static SecretKeySpec key() {
        if (KEY == null) {
            synchronized (CryptoAttributeConverter.class) {
                if (KEY == null) {
                    String b64 = System.getenv("APP_ENC_KEY");
                    if (b64 == null || b64.isBlank()) {
                        throw new IllegalStateException("APP_ENC_KEY env var is required (base64-encoded AES key).");
                    }
                    byte[] keyBytes = Base64.getDecoder().decode(b64);
                    int len = keyBytes.length;
                    if (!(len == 16 || len == 24 || len == 32)) {
                        throw new IllegalStateException("APP_ENC_KEY must decode to 16, 24, or 32 bytes.");
                    }
                    KEY = new SecretKeySpec(keyBytes, "AES");
                }
            }
        }
        return KEY;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) return attribute;
        // If already in encrypted form, store as-is (avoid double-encrypt)
        if (attribute.startsWith(PREFIX)) return attribute;

        try {
            byte[] iv = new byte[IV_LEN];
            RAND.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ct = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

            byte[] out = new byte[iv.length + ct.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ct, 0, out, iv.length, ct.length);

            return PREFIX + Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return dbData;
        // Legacy plaintext? Return as-is so the app can start & the backfill can re-save encrypted.
        if (!dbData.startsWith(PREFIX)) return dbData;

        try {
            String b64 = dbData.substring(PREFIX.length());
            byte[] in = Base64.getDecoder().decode(b64);
            if (in.length < IV_LEN + 1) {
                // Corrupt or unexpected — fail closed
                throw new IllegalArgumentException("Encrypted payload too short");
            }
            byte[] iv = java.util.Arrays.copyOfRange(in, 0, IV_LEN);
            byte[] ct = java.util.Arrays.copyOfRange(in, IV_LEN, in.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] pt = cipher.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
