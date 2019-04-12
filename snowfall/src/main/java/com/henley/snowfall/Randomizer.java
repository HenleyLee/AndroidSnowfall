package com.henley.snowfall;

import java.util.Random;

/**
 * @author Henley
 * @date 2017/8/28 14:55
 */
final class Randomizer {

    private static final Random random = new Random();

    double randomDouble(int max) {
        return random.nextDouble() * (max + 1);
    }

    int randomInt(int min, int max) {
        return randomInt(max - min, false) + min;
    }

    int randomInt(int min, int max, boolean gaussian) {
        return randomInt(max - min, gaussian) + min;
    }

    int randomInt(int max, boolean gaussian) {
        if (gaussian) {
            return (int) (Math.abs(randomGaussian()) * (max + 1));
        } else {
            return random.nextInt(max + 1);
        }
    }

    double randomGaussian() {
        double gaussian = random.nextGaussian() / 3; // more 99% of instances in range (-1, 1)
        if (gaussian > -1 && gaussian < 1) {
            return gaussian;
        } else {
            return randomGaussian();
        }
    }

    int randomSignum() {
        if (random.nextBoolean()) {
            return 1;
        } else {
            return -1;
        }
    }

}
