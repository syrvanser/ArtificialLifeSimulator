package uk.ac.reading.syrvanser.Entities;

import uk.ac.reading.syrvanser.Graphics.GUIInterface;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Direction;
import uk.ac.reading.syrvanser.Logic.Edible;

import static uk.ac.reading.syrvanser.Graphics.GUIInterface.imageSize;


/**
 * Created by syrvanser on 07/11/2016.
 *
 * @author syrvanser
 */
public abstract class LifeForm extends AnEntity implements Edible {
    // private static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/doge.png");
    private double currentX;
    private double currentY;
    private int detectionRadius = 5;

    LifeForm(String species, int hPosition, int vPosition, AWorld world) {
        super(species, hPosition, vPosition, 15, world);
        this.currentX = targetX * imageSize;
        this.currentY = targetY * imageSize;
    }

    /*@Override
    public Image getImage() {
        return classImage;
    }
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

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public int getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(int detectionRadius) {
        this.detectionRadius = detectionRadius;
    }

    public void updatePosition(double distance) {

        double realX = targetX * imageSize;
        double realY = targetY * imageSize;
        if (currentX != realX)
            currentX += distance * (realX > currentX ? 1 : -1);
        if (currentY != realY)
            currentY += distance * (realY > currentY ? 1 : -1);
        //(Math.abs(currentY-realY)/distance)*
    }

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
            AnEntity entity = world.getEntity(xVal, yVal);
            if (entity == null)
                continue;
            if (entity instanceof RenewableFood && !((RenewableFood) entity).getCanEat()) {
                return -1;
            }
            if (entity instanceof Edible && ((Edible) entity).getLevel() == getLevel() - 1)
                return i;
            if ((entity instanceof Edible && ((Edible) entity).getLevel() != getLevel() - 1) || entity instanceof Obstacle)
                return -1;
        }
        return -1;
    }

    @Override
    public void display(GUIInterface i) {
        i.show(getImage(), (int) (currentX), (int) (currentY), imageOpacity);                            // just send details the entity to the interface
    }

}
