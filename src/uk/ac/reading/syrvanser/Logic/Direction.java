package uk.ac.reading.syrvanser.Logic;

import java.util.Random;

/**
 * Created by syrvanser on 24/10/2016.
 *
 * @author syrvanser
 */
public enum Direction {
    N, S, E, W;

    /**
     * Pick a random value of the Direction enum.
     *
     * @return a random Direction
     */
    public static Direction getRandomDirection() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }

}
