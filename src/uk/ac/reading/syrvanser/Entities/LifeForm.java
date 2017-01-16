package uk.ac.reading.syrvanser.Entities;

import uk.ac.reading.syrvanser.Graphics.GUIInterface;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Direction;
import uk.ac.reading.syrvanser.Logic.Edible;

import static uk.ac.reading.syrvanser.Graphics.GUIInterface.imageSize;


/**
 * Created by syrvanser on 07/11/2016.
 * Abstract class for life forms
 * Stores information about actual position on the screen and definitions for some life form-specific methods
 * @author syrvanser
 */
public abstract class LifeForm extends AnEntity implements Edible {

    private double currentX;
    private double currentY;
    private int detectionRadius = 5;

    /**
     * Constructor
     *
     * @param hPosition horizontal position
     * @param vPosition vertical position
     * @param world     world it belongs to
     */
    LifeForm(String species, int hPosition, int vPosition, AWorld world) {
        super(species, hPosition, vPosition, 15, world);
        this.currentX = targetX * imageSize;
        this.currentY = targetY * imageSize;
    }

    /**
     * This method moves the entity one square in a specified direction
     * @param d direction to move
     */
    public void move(Direction d) {
        switch (d) {
            case N:
                setTargetY(targetY - 1);
                break;
            case E:
                setTargetX(targetX + 1);
                break;
            case S:
                setTargetY(targetY + 1);
                break;
            case W:
                setTargetX(targetX - 1);
                break;
        }
    }

    /**
     * Setter for the actual x position
     * @param currentX horizontal position
     */
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    /**
     * Setter for the actual y position
     * @param currentY vertical position
     */
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    /**
     * Getter for the detection radius
     * @return detection radius, >= 0
     */
    public int getDetectionRadius() {
        return detectionRadius;
    }

    /**
     * Setter for the detection radius
     * @param detectionRadius detection radius, >= 0
     */
    public void setDetectionRadius(int detectionRadius) {
        if (detectionRadius >= 0) {
            this.detectionRadius = detectionRadius;
        }
    }

    /**
     * Function for updating entity's position
     * @param distance how much enmity should be moved
     * Distance depends on the simulation speed and screen size
     */
    public void updatePosition(double distance) {

        double realX = targetX * imageSize;
        double realY = targetY * imageSize;
        if (currentX != realX)
            currentX += distance * (realX > currentX ? 1 : -1);
        if (currentY != realY)
            currentY += distance * (realY > currentY ? 1 : -1);
    }

    /**
     * Function for detecting food in a certain direction
     * @param d     direction to use
     * @param range detection range
     * @return distance to the food, -1 if there is an obstacle or no food around
     */
    public int smellFood(Direction d, int range) {
        int xVal = this.targetX; //current x position
        int yVal = this.targetY; //current y position
        for (int i = 1; i <= range; i++) { //for all cells within detection range
            switch (d) { //move 1 square depending on the direction
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
            AnEntity entity = world.getEntity(xVal, yVal); //get entity at the given position
            if (entity == null) //skip if empty
                continue;
            if (entity instanceof RenewableFood && !((RenewableFood) entity).getCanEat()) //return -1 if it's growing renewable food (treated as an obstacle)
                return -1;
            if (entity instanceof Edible && ((Edible) entity).getLevel() == getLevel() - 1) //return distance if can be eaten
                return i;
            if ((entity instanceof Edible && ((Edible) entity).getLevel() != getLevel() - 1) || entity instanceof Obstacle) //return -1 if cannot be eaten
                return -1;
        }
        return -1; //return -1 if no entities were found
    }

    /**
     * Method for displaying life forms,  sends all required information to the interface
     * Uses current positions instead of desired positions
     * @param i interface to use
     */
    @Override
    public void display(GUIInterface i) {
        i.show(getImage(), (int) currentX, (int) currentY, imageOpacity); // just send details the entity to the interface
    }

}
