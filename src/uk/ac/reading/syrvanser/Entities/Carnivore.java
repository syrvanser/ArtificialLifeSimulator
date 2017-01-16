package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * A class for carnivore entities
 * Each entity group has a hierarchic level
 * Only entities which are one level lower can be eaten
 * @author syrvanser
 * @since 11/01/2017
 */
public class Carnivore extends LifeForm {

    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/doge.png");
    private final byte hierarchyLevel;

    /**
     * Constructor
     *
     * @param species   species name
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     * @param level     hierarchy level
     */
    public Carnivore(String species, int hPosition, int vPosition, AWorld world, byte level) {
        super(species, hPosition, vPosition, world);
        hierarchyLevel = level;
    }

    /**
     * Image getter
     * @return carnivore life form's image
     */
    @Override
    public Image getImage() {
        return Carnivore.classImage;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
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

    /**
     * Returns a number between 0 and 10
     * @return level
     */
    @Override
    public byte getLevel() {
        return hierarchyLevel;
    }
}
