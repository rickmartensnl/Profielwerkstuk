package com.example.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthenticationUtil {

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

}
