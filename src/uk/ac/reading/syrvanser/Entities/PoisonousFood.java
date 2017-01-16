package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Poisonous;

/**
 * Created by syrvanser on 10/01/2017.
 * Poisonous food, life forms die when they eat it
 * @author syrvanser
 */
public class PoisonousFood extends Food implements Poisonous {

    public final static Image classImage = new Image("/uk/ac/reading/syrvanser/img/poison.png");

    /**
     * Constructor
     *
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    public PoisonousFood(int hPosition, int vPosition, AWorld world) {
        super(hPosition, vPosition, world);
    }

    /**
     * Image getter
     * @return food's image
     */
    @Override
    public Image getImage() {
        return PoisonousFood.classImage;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
    @Override
    public String toText() {
        return "Poisonous Food{" +
                " X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }
}
