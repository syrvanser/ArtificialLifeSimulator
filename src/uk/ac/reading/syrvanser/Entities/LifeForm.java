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
    private int currentX;
    private int currentY;
    private int detectionRadius = 5;

    public LifeForm(String species, char symbol, int hPosition, int vPosition, AWorld world) {
        super(species, symbol, hPosition, vPosition, 100000, world);
        this.currentX = targetX * IMGSIZE;
        this.currentY = targetY * IMGSIZE;
        this.image = new Image("/uk/ac/reading/syrvanser/img/doge.png");
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

    public void updatePosition() {

        int realX = targetX * IMGSIZE;
        int realY = targetY * IMGSIZE;
        if (currentX != realX)
            currentX += (realX > currentX ? 1 : -1);
        if (currentY != realY)
            currentY += (realY > currentY ? 1 : -1);
    }

    @Override
    public void display(GUIInterface i) {
        i.show(image, currentX, currentY, imageOpacity);                            // just send details the entity to the interface
    }

    @Override
    public String toText() {
        return "LifeForm{" +
                "species='" + species + '\'' +
                ", symbol=" + symbol +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", absX=" + currentX +
                ", absY=" + currentY +
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
