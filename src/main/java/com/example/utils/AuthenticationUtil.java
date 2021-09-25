package com.example.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;

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
            publicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile("src/main/resources/publicWorks.pem", "RSA");

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

}
