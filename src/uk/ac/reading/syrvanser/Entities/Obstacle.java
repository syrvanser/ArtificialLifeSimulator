package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 07/11/2016.
 *
 * @author syrvanser
 */
public class Obstacle extends AnEntity {
    public Obstacle(int hPosition, int vPosition, AWorld world) {
        super("Obstacle", 'o', hPosition, vPosition, 0, world);
        image = new Image("/uk/ac/reading/syrvanser/img/wall.png");
    }

    @Override
    public String toText() {
        return "Obstacle{" +
                "species='" + species + '\'' +
                ", symbol=" + symbol +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

    @Override
    public String toString() {
        {
            return "Obstacle{" +
                    "species='" + species + '\'' +
                    ", X=" + targetX +
                    ", Y=" + targetY +
                    '}';
        }
    }
}
