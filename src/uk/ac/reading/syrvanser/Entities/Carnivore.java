package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 11/01/2017.
 *
 * @author syrvanser
 */
public class Carnivore extends LifeForm {

    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/doge.png");
    private final byte heirachyLevel;

    public Carnivore(String species, int hPosition, int vPosition, AWorld world, byte level) {
        super(species, hPosition, vPosition, world);
        heirachyLevel = level;
    }


    @Override
    public Image getImage() {
        return Carnivore.classImage;
    }

    @Override
    public String toText() {
        return "Carnivore{" +
                "species='" + species + '\'' +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

    @Override
    public byte getLevel() {
        return heirachyLevel;
    }
}
