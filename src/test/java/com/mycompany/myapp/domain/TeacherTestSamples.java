package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TeacherTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Teacher getTeacherSample1() {
        return new Teacher().id(1L).fname("fname1").mname("mname1").lname("lname1");
    }

    public static Teacher getTeacherSample2() {
        return new Teacher().id(2L).fname("fname2").mname("mname2").lname("lname2");
    }

    public static Teacher getTeacherRandomSampleGenerator() {
        return new Teacher()
            .id(longCount.incrementAndGet())
            .fname(UUID.randomUUID().toString())
            .mname(UUID.randomUUID().toString())
            .lname(UUID.randomUUID().toString());
    }
}
