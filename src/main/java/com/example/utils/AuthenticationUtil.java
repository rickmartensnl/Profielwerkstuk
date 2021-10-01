package com.example.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.exceptions.TokenVerifyException;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class AuthenticationUtil {

    private static RSAPrivateKey privateKey = null;
    private static RSAPublicKey publicKey = null;
    @Getter private static Algorithm algorithm = null;

    static {
        try {
            privateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile("src/main/resources/private.pem", "RSA");
            publicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile("src/main/resources/public.pem", "RSA");

            algorithm = Algorithm.RSA256(publicKey, privateKey);
        } catch (IOException e) {
            e.printStackTrace();
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
            throw new TokenVerifyException();
        }
    }

}
