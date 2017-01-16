package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Edible;

/**
 * Created by syrvanser on 07/11/2016.
 *
 * @author syrvanser
 */
public class Food extends AnEntity implements Edible {
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/pizza.png");

    public Food(int hPosition, int vPosition, AWorld world) {
        super("Food", hPosition, vPosition, 5, world);
    }


    @Override
    public Image getImage() {
        return Food.classImage;
    }

    @Override
    public String toText() {
        return "Food{" +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

    @Override
    public byte getLevel() {
        return 0;
    }
}
