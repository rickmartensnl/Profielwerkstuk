package com.example.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.exceptions.TokenVerifyException;
import io.sentry.Sentry;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.regex.Pattern;

public class AuthenticationUtil {

    private static RSAPrivateKey privateKey = null;
    private static RSAPublicKey publicKey = null;
    @Getter private static Algorithm algorithm = null;

    static {
        try {
            privateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile("src/main/resources/private.pem", "RSA");
            publicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile("src/main/resources/public.pem", "RSA");

            algorithm = Algorithm.RSA256(publicKey, privateKey);
        } catch (IOException exception) {
            Sentry.captureException(exception);
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

    public static @Nullable DecodedJWT tokenToBody(String token) throws TokenVerifyException {
        if (token == null) {
            return null;
        }

        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer("https://pws.rickmartens.nl")
                    .build();
            return jwtVerifier.verify(token);
        } catch (JWTVerificationException exception) {
            Sentry.captureException(exception);
            throw new TokenVerifyException();
        }
    }

    public static boolean isValidEmail(String email) {
        String pattern = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";
        return Pattern.matches(pattern, email);
    }
}
