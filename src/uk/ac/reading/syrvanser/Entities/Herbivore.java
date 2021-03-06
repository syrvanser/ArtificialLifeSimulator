package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Class for herbivore life forms
 * Those life forms are the only once that can eat objects from the Food class
 * @author syrvanser
 * @since 07/11/2016
 */
public class Herbivore extends LifeForm {
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/RM.png");

    /**
     * Constructor
     *
     * @param species species name
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    public Herbivore(String species, int hPosition, int vPosition, AWorld world) {
        super(species, hPosition, vPosition, world);
    }

    /**
     * Image getter
     * @return herbivore life form's image
     */
    @Override
    public Image getImage() {
        return Herbivore.classImage;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
    @Override
    public String toText() {
        return "Herbivore{" +
                "species='" + species + '\'' +
                " X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

    /**
     * Returns 1
     * All herbivore entities belong to the first level - just above food
     * @return level
     */
    @Override
    public byte getLevel() {
        return 1;
    }
}
