package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 07/11/2016.
 *
 * @author syrvanser
 */
public class Obstacle extends AnEntity {

    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/wall.png");

    public Obstacle(int hPosition, int vPosition, AWorld world) {
        super("Obstacle", hPosition, vPosition, 0, world);
    }


    @Override
    public Image getImage() {
        return classImage;
    }

    @Override
    public String toText() {
        return "Obstacle{" +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

}
