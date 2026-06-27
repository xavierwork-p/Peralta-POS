package com.peraltapos.security;

import com.peraltapos.common.web.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long TOKEN_HOURS = 12;

    private final byte[] secret;

    public AuthTokenService(@Value("${app.security.jwt-secret}") String secret) {
        if (secret == null || secret.isBlank() || "replace-with-a-long-secure-secret".equals(secret)) {
            this.secret = "peralta-pos-local-development-secret-change-before-production".getBytes(StandardCharsets.UTF_8);
        } else {
            this.secret = secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    public IssuedToken issue(UUID accountId) {
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusHours(TOKEN_HOURS);
        String payload = accountId + ":" + expiresAt.toEpochSecond();
        String token = base64(payload.getBytes(StandardCharsets.UTF_8)) + "." + base64(sign(payload));
        return new IssuedToken(token, expiresAt);
    }

    public Optional<UUID> verify(String token) {
        if (token == null || token.isBlank() || !token.contains(".")) {
            return Optional.empty();
        }

        String[] parts = token.split("\\.", 2);
        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            byte[] expected = sign(payload);
            byte[] actual = Base64.getUrlDecoder().decode(parts[1]);
            if (!MessageDigest.isEqual(expected, actual)) {
                return Optional.empty();
            }

            String[] payloadParts = payload.split(":", 2);
            OffsetDateTime expiresAt = OffsetDateTime.ofInstant(
                    Instant.ofEpochSecond(Long.parseLong(payloadParts[1])),
                    ZoneOffset.UTC
            );
            if (expiresAt.isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
                return Optional.empty();
            }

            return Optional.of(UUID.fromString(payloadParts[0]));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private byte[] sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new BusinessException("No se pudo firmar la sesion");
        }
    }

    private String base64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record IssuedToken(String token, OffsetDateTime expiresAt) {
    }
}
