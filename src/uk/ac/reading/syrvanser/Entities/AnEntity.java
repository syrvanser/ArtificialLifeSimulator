package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Graphics.GUIInterface;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Direction;

import static uk.ac.reading.syrvanser.Graphics.GUIInterface.IMGSIZE;

/**
 * Created by syrvanser on 10/10/2016.
 *
 * @author syrvanser
 */
public abstract class AnEntity {


    private static int entityCounter = 0;
    Image image;
    String species;
    char symbol;
    int targetX;
    int targetY;
    int energy;
    int uniqueID;
    double imageOpacity = 1;
    private AWorld world;

    /**
     * Constructor
     *
     * @param species   species name
     * @param symbol    species Symbol
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param energy    energy of the entity
     * @param world     world it belongs to
     */
    AnEntity(String species, char symbol, int hPosition, int vPosition, int energy, AWorld world) {
        this.species = species;
        this.symbol = symbol;
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
    public abstract String toString();

    /**
     * @param d     Direction
     * @param range detection range
     * @return true if there is food at the given direction, false otherwise
     */
    public int smellFood(Direction d, int range) {
        int xVal = this.targetX;
        int yVal = this.targetY;
        for (int i = 1; i <= range; i++) {
            switch (d) {
                case N:
                    yVal--;
                    break;
                case S:
                    yVal++;
                    break;
                case E:
                    xVal++;
                    break;
                case W:
                    xVal--;
                    break;
            }
            if (this.world.checkFood(xVal, yVal))
                return i;
            if(this.world.getEntity(xVal, yVal) == null)
                continue;
            if(this.world.getEntity(xVal, yVal) instanceof LifeForm || this.world.getEntity(xVal, yVal) instanceof Obstacle)
                return -1;

        }
        return -1;
    }

    public void display(GUIInterface i) {

        i.show(image, targetX * IMGSIZE, targetY * IMGSIZE, imageOpacity);                            // just send details the entity to the interface
    }

}

