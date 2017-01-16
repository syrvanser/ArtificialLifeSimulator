package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Poisonous;

/**
 * Created by syrvanser on 15/01/2017.
 * Poisonous herbivore, predators die when eat it
 * @author syrvanser
 */
public class PoisonousHerbivore extends Herbivore implements Poisonous {
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/AND.png");

    /**
     * Constructor
     *
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    public PoisonousHerbivore(String species, int hPosition, int vPosition, AWorld world) {
        super(species, hPosition, vPosition, world);
    }

    /**
     * Image getter
     * @return life form's image
     */
    @Override
    public Image getImage() {
        return PoisonousHerbivore.classImage;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
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
