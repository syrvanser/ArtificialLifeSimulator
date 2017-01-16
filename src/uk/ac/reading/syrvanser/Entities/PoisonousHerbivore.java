package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 15/01/2017.
 *
 * @author syrvanser
 */
public class PoisonousHerbivore extends Herbivore {
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/AND.png");

    public PoisonousHerbivore(String species, int hPosition, int vPosition, AWorld world) {
        super(species, hPosition, vPosition, world);
    }

    @Override
    public Image getImage() {
        return PoisonousHerbivore.classImage;
    }

    @Override
    public String toText() {
        return "Poisonous Herbivore{" +
                "species='" + species + '\'' +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }
}
