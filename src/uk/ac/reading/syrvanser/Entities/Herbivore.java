package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 11/01/2017.
 *
 * @author syrvanser
 */
public class Herbivore extends LifeForm {
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/RM.png");


    public Herbivore(String species, int hPosition, int vPosition, AWorld world) {
        super(species, hPosition, vPosition, world);
    }

    @Override
    public Image getImage() {
        return Herbivore.classImage;
    }

    @Override
    public String toText() {
        return "Herbivore{" +
                "species='" + species + '\'' +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

    @Override
    public byte getLevel() {
        return 1;
    }
}
