package com.example.utils;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}