package uk.ac.reading.syrvanser.Entities;

import javafx.scene.image.Image;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Edible;

/**
 * Created by syrvanser on 15/01/2017.
 *
 * @author syrvanser
 */
public class RenewableFood extends Food implements Edible {
    public static final int timeToGrow = 5;
    public static final Image classImageGrowing = new Image("/uk/ac/reading/syrvanser/img/wrap-growing.png");
    public static final Image classImage = new Image("/uk/ac/reading/syrvanser/img/wrap.png");
    private boolean canEat;
    private int timer;

    public RenewableFood(int hPosition, int vPosition, AWorld world) {
        super(hPosition, vPosition, world);
        canEat = true;
    }

    public boolean getCanEat() {
        return canEat;
    }

    public int getTimer() {
        return timer;
    }

    public void eat() {
        timer = timeToGrow;
        //setImageOpacity(0);
        canEat = false;
    }

    public void update() {
        if (!canEat) {

            if (timer == 0) {
                //setImageOpacity(1);
                canEat = true;
            }
            if (timer > 0) {
                timer--;

            }
        }
    }

    @Override
    public Image getImage() {
        //  return canEat ? RenewableFood.classImageReady : RenewableFood.classImageGrowing;
        return (canEat || timer >= timeToGrow - 1) ? RenewableFood.classImage : RenewableFood.classImageGrowing;
    }

    @Override
    public String toText() {
        return "Renewable Food{" +
                ", X=" + targetX +
                ", Y=" + targetY +
                ", energy=" + energy +
                ", uniqueID=" + uniqueID +
                '}';
    }

}
