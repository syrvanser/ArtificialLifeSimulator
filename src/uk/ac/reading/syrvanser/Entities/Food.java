package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Edible;

/**
 * Created by syrvanser on 07/11/2016.
 * This class represents food
 * @author syrvanser
 */
public class Food extends AnEntity implements Edible {
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/pizza.png");

    /**
     * Constructor
     *
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    public Food(int hPosition, int vPosition, AWorld world) {
        super("Food", hPosition, vPosition, 5, world);
    }

    /**
     * Image getter
     * @return food's image
     */
    @Override
    public Image getImage() {
        return Food.classImage;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
    @Override
    public String toText() {
        return "Food{" +
                " X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

    /**
     * Returns 0
     * All food entities belong to the lowest level
     * @return level
     */
    @Override
    public byte getLevel() {
        return 0;
    }
}
