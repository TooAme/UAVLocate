package com.chenhy.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StaticsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Statics getStaticsSample1() {
        return new Statics().id(1L).posX(1L).posY(1L).posZ(1L).windSpeed(1L).windDirection("windDirection1");
    }

    public static Statics getStaticsSample2() {
        return new Statics().id(2L).posX(2L).posY(2L).posZ(2L).windSpeed(2L).windDirection("windDirection2");
    }

    public static Statics getStaticsRandomSampleGenerator() {
        return new Statics()
            .id(longCount.incrementAndGet())
            .posX(longCount.incrementAndGet())
            .posY(longCount.incrementAndGet())
            .posZ(longCount.incrementAndGet())
            .windSpeed(longCount.incrementAndGet())
            .windDirection(UUID.randomUUID().toString());
    }
}
