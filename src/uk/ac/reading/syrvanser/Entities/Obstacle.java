package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 07/11/2016.
 * Obstacle class, immobile object that cannot be walked through
 * @author syrvanser
 */
public class Obstacle extends AnEntity {

    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/wall.png");

    /**
     * Constructor
     *
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    public Obstacle(int hPosition, int vPosition, AWorld world) {
        super("Obstacle", hPosition, vPosition, 0, world);
    }

    /**
     * Image getter
     * @return obstacle's image
     */
    @Override
    public Image getImage() {
        return classImage;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
    @Override
    public String toText() {
        return "Obstacle{" +
                " X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

}
