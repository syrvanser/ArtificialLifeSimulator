package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Class for renewable food which regenerates over time
 * Cannot be walked through when it is growing
 * @author syrvanser
 * @since 15/01/2017
 */
public class RenewableFood extends Food {
    public static final int timeToGrow = 5;
    public static final Image classImageGrowing = new Image("/uk/ac/reading/syrvanser/img/wrap-growing.png");
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/wrap.png");
    private boolean canEat;
    private int timer;

    /**
     * Constructor
     *
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    public RenewableFood(int hPosition, int vPosition, AWorld world) {
        super(hPosition, vPosition, world);
        canEat = true;
    }

    /**
     * Provides information about whether food can be eaten
     * @return false if food is growing and true if can be eaten
     */
    public boolean getCanEat() {
        return canEat;
    }

    /**
     * Getter for the timer
     * @return current timer value, between 0 and timeToGrow
     */
    public int getTimer() {
        return timer;
    }

    /**
     * Changes state to 'growing'
     */
    public void eat() {
        timer = timeToGrow;
        canEat = false;
    }

    /**
     * Routine for updating food's state, called automatically every round
     */
    public void update() {
        if (!canEat) {

            if (timer == 0) {
                canEat = true;
            }
            if (timer > 0) {
                timer--;

            }
        }
    }

    /**
     * Image getter
     * @return food's image
     */
    @Override
    public Image getImage() {
        //  return canEat ? RenewableFood.classImageReady : RenewableFood.classImageGrowing;
        return (canEat || timer >= timeToGrow - 1) ? RenewableFood.classImage : RenewableFood.classImageGrowing;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
    @Override
    public String toText() {
        return "Renewable Food{" +
                " X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

}
