package com.handsome.ddz.util;

import java.util.Random;

public class IDMaker {

    static Random random = new Random();

    public static synchronized Long randomId() {
        return System.currentTimeMillis() * 100000 + random.nextInt(100000);
    }
}
