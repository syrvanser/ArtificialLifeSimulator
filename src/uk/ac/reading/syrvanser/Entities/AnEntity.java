package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Graphics.GUIInterface;
import uk.ac.reading.syrvanser.Logic.AWorld;

import static uk.ac.reading.syrvanser.Graphics.GUIInterface.imageSize;

/**
 * An abstract class representing a simple Entity
 * Stores information about name, position, energy, etc.
 * All entities inherit from this class
 * @author syrvanser
 * @since 10/10/2016.
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

    /**
     * Getter for the entity counter
     *
     * @return entity counter, always greater or equal to 0
     */
    public static int getEntityCounter() {
        return entityCounter;
    }

    /**
     * A method for resetting the counter
     */
    public static void resetEntityCounter() {
        AnEntity.entityCounter = 0;
    }

    /**
     * Abstract getter for the entity's image - every child has to reimplement it
     * @return entity's image
     */
    public abstract Image getImage();

    /**
     * Getter for the image opacity
     * @return opacity, a decimal number between 0 and 1
     */
    public double getImageOpacity() {
        return imageOpacity;
    }

    /**
     * Setter for the opacity, checks that it is within bounds
     * @param imageOpacity a decimal between 0 and 1
     */
    public void setImageOpacity(double imageOpacity) {

        if (imageOpacity > 1) {
            this.imageOpacity = 1;
        } else if (imageOpacity < 0) {
            this.imageOpacity = 0;
        } else {
            this.imageOpacity = imageOpacity;
        }
    }

    /**
     * Species name getter
     * @return entity's name
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Setter for the species name
     * @param species new name
     */
    public void setSpecies(String species) {
        this.species = species;
    }

    /**
     * Getter for the x position
     * @return integer number, x position
     */
    public int getTargetX() {
        return targetX;
    }

    /**
     * Setter for the x position
     * @param targetX integer number, x position
     */
    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    /**
     * Setter for the y position
     * @return integer number, y position
     */
    public int getTargetY() {
        return targetY;
    }

    /**
     * Setter for the y position
     * @param targetY integer number, y position
     */
    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    /**
     * Getter for the energy
     * @return non negative number, entity's energy
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Energy setter
     * @param energy new energy
     */
    public void setEnergy(int energy) {
        if (energy >= 0) {
            this.energy = energy;
        }
    }

    /**
     * Abstract method, returns a string with detailed information about the entity
     * @return string with information
     */
    public abstract String toText();

    /**
     *  Returns information about species name and position
     * @return string with information
     */
    @Override
    public String toString() {
        {
            return species + " at " + targetX + ", " + targetY;
        }
    }

    /**
     * Method for displaying an entity, sends all required information to the interface
     * @param i interface to use
     */
    public void display(GUIInterface i) {
        i.show(getImage(), targetX * imageSize, targetY * imageSize, imageOpacity);   //just send details the entity to the interface
    }

}

