package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Poisonous;

/**
 * Created by syrvanser on 10/01/2017.
 *
 * @author syrvanser
 */
public class PoisonousFood extends Food implements Poisonous {

    public final static Image classImage = new Image("/uk/ac/reading/syrvanser/img/poison.png");

    public PoisonousFood(int hPosition, int vPosition, AWorld world) {
        super(hPosition, vPosition, world);
    }

    @Override
    public Image getImage() {
        return PoisonousFood.classImage;
    }

    @Override
    public String toText() {
        return "Poisonous Food{" +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }


}
