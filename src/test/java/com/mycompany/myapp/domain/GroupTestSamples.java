package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GroupTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Group getGroupSample1() {
        return new Group().id(1L).name("name1").yearGraduation(1);
    }

    public static Group getGroupSample2() {
        return new Group().id(2L).name("name2").yearGraduation(2);
    }

    public static Group getGroupRandomSampleGenerator() {
        return new Group().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).yearGraduation(intCount.incrementAndGet());
    }
}
