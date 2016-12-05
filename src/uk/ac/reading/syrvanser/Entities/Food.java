package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 07/11/2016.
 *
 * @author syrvanser
 */
public class Food extends AnEntity {
    public Food(int hPosition, int vPosition, AWorld world) {
        super("Food", 'f', hPosition, vPosition, 5, world);
        image = new Image("/uk/ac/reading/syrvanser/img/pizza.png");
    }

    @Override
    public String toText() {
        return "Food{" +
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
            return "Food{" +
                    "species='" + species + '\'' +
                    ", X=" + targetX +
                    ", Y=" + targetY +
                    '}';
        }
    }
}
