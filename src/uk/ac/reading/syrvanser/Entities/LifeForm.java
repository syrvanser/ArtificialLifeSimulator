package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Graphics.GUIInterface;
import uk.ac.reading.syrvanser.Logic.AWorld;

import static uk.ac.reading.syrvanser.Graphics.GUIInterface.IMGSIZE;

/**
 * Created by syrvanser on 07/11/2016.
 *
 * @author syrvanser
 */
public class LifeForm extends AnEntity {
    private int detectionRadius = 5;

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    int currentX;
    int currentY;

    public int getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(int detectionRadius) {
        this.detectionRadius = detectionRadius;
    }

    public LifeForm(String species, char symbol, int hPosition, int vPosition, AWorld world) {
        super(species, symbol, hPosition, vPosition, 100000, world);
        this.currentX = targetX * IMGSIZE;
        this.currentY = targetY * IMGSIZE;
        this.image = new Image("/uk/ac/reading/syrvanser/img/doge.png");
    }


    public void updatePosition() {

        int realX = targetX * IMGSIZE;
        int realY = targetY * IMGSIZE;
        if (currentX != realX)
            currentX += (realX - currentX) / Math.abs(realX - currentX);
        if (currentY != realY)
            currentY += (realY - currentY) / Math.abs(realY- currentY);
    }

    @Override
    public void display(GUIInterface i) {
        i.show(image, currentX, currentY);							// just send details the entity to the interface
    }

    @Override
    public String toText() {
        return "LifeForm{" +
                "species='" + species + '\'' +
                ", symbol=" + symbol +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

    @Override
    public String toString() {
        {
            return "LifeForm{" +
                    "species='" + species + '\'' +
                    ", X=" + targetX +
                    ", Y=" + targetY +
                    '}';
        }
    }
}
