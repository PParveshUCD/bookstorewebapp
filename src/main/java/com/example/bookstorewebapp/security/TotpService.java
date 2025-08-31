package com.example.bookstorewebapp.security;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator; // from com.eatthepath:java-otp
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
public class TotpService {

    private final TimeBasedOneTimePasswordGenerator gen =
            new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30)); // 6 digits, HMAC-SHA1
    private final SecureRandom rng = new SecureRandom();
    private final Base32 base32 = new Base32(false); // no padding

    /** Generate a Base32 secret suitable for authenticator apps */
    public String newBase32Secret() {
        byte[] bytes = new byte[20]; // 160-bit secret
        rng.nextBytes(bytes);
        // Uppercase, no padding is typical for otpauth
        return base32.encodeToString(bytes).replace("=", "");
    }

    /** Verify a 6-digit code with a small ±1 step window */
    public boolean verify(String base32Secret, String code) {
        try {
            int provided = Integer.parseInt(code);
            byte[] keyBytes = base32.decode(base32Secret);
            Key key = new SecretKeySpec(keyBytes, gen.getAlgorithm());

            Instant now = Instant.now();
            int cur  = gen.generateOneTimePassword(key, now);
            int prev = gen.generateOneTimePassword(key, now.minus(gen.getTimeStep()));
            int next = gen.generateOneTimePassword(key, now.plus(gen.getTimeStep()));
            return provided == cur || provided == prev || provided == next;
        } catch (Exception e) {
            return false;
        }
    }

    /** otpauth URI for QR (Google Authenticator, etc.) */
    public String otpauthUri(String issuer, String account, String base32Secret) {
        String qs = "secret=" + url(base32Secret) +
                "&issuer=" + url(issuer) +
                "&algorithm=SHA1&digits=6&period=30";
        return "otpauth://totp/" + url(issuer) + ":" + url(account) + "?" + qs;
    }

    private static String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
