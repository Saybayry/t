package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AssessmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Assessment getAssessmentSample1() {
        return new Assessment().id(1L).isPresent(1L).assessment(1);
    }

    public static Assessment getAssessmentSample2() {
        return new Assessment().id(2L).isPresent(2L).assessment(2);
    }

    public static Assessment getAssessmentRandomSampleGenerator() {
        return new Assessment()
            .id(longCount.incrementAndGet())
            .isPresent(longCount.incrementAndGet())
            .assessment(intCount.incrementAndGet());
    }
}
