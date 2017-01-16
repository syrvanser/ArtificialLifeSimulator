package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Graphics.GUIInterface;
import uk.ac.reading.syrvanser.Logic.AWorld;

import static uk.ac.reading.syrvanser.Graphics.GUIInterface.imageSize;

/**
 * Created by syrvanser on 10/10/2016.
 *
 * @author syrvanser
 */
public abstract class AnEntity {


    private static int entityCounter = 0;
    String species;
    int targetX;
    int targetY;
    int energy;
    int uniqueID;
    double imageOpacity = 1;
    AWorld world;

    /**
     * Constructor
     *
     * @param species   species name
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param energy    energy of the entity
     * @param world     world it belongs to
     */
    AnEntity(String species, int hPosition, int vPosition, int energy, AWorld world) {
        this.species = species;
        this.targetX = hPosition;
        this.targetY = vPosition;
        this.energy = energy;
        this.uniqueID = entityCounter++;
        this.world = world;
    }

    public static int getEntityCounter() {
        return entityCounter;
    }

    public static void resetEntityCounter() {
        AnEntity.entityCounter = 0;
    }

    public abstract Image getImage();

    public double getImageOpacity() {
        return imageOpacity;
    }

    public void setImageOpacity(double imageOpacity) {
        this.imageOpacity = imageOpacity;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getTargetX() {
        return targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public abstract String toText();

    @Override
    public String toString() {
        {
            return species + " at " + targetX + ", " + targetY;
        }
    }

    public void display(GUIInterface i) {
        i.show(getImage(), targetX * imageSize, targetY * imageSize, imageOpacity);                            // just send details the entity to the interface
    }

}

