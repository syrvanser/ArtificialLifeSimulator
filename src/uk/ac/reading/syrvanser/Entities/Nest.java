package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 16/01/2017.
 *
 * @author syrvanser
 */
public class Nest extends Obstacle {

    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/nest.png");

    public Nest(int hPosition, int vPosition, AWorld world) {
        super(hPosition, vPosition, world);
    }

    @Override
    public Image getImage() {
        return classImage;
    }

    @Override
    public String toText() {
        return "Nest{" +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }
}
