package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;

/**
 * Created by syrvanser on 16/01/2017.
 * Nest class, generates a random entity after a certain amount of time
 * Works as an obstacle
 * @author syrvanser
 */
public class Nest extends Obstacle {

    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/nest.png");
    private String lifeForm = null;

    /**
     * Constructor
     *
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    public Nest(int hPosition, int vPosition, AWorld world) {
        super(hPosition, vPosition, world);
    }

    /**
     * Image getter
     * @return nest's image
     */
    @Override
    public Image getImage() {
        return classImage;
    }

    /**
     * Returns a string with detailed information about the entity
     * @return string with information
     */
    @Override
    public String toText() {
        return "Nest{" +
                " X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                ", Life Form=" + lifeForm +
                '}';
    }

    /**
     * Getter for the life form it belongs to
     *
     * @return String with life form's name
     */
    public String getLifeForm() {
        return lifeForm;
    }

    public void setLifeForm(String lifeForm) {
        this.lifeForm = lifeForm;
    }
}
