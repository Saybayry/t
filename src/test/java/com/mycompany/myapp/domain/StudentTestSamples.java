package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StudentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Student getStudentSample1() {
        return new Student().id(1L).fname("fname1").mname("mname1").lname("lname1");
    }

    public static Student getStudentSample2() {
        return new Student().id(2L).fname("fname2").mname("mname2").lname("lname2");
    }

    public static Student getStudentRandomSampleGenerator() {
        return new Student()
            .id(longCount.incrementAndGet())
            .fname(UUID.randomUUID().toString())
            .mname(UUID.randomUUID().toString())
            .lname(UUID.randomUUID().toString());
    }
}
