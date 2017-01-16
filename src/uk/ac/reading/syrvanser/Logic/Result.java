package uk.ac.reading.syrvanser.Logic;

/**
 * Created by syrvanser on 29/11/2016.
 * Class for a brief summary of information about an entity
 * @author syrvanser
 */
public class Result {
    public String n;
    public int x;
    public int y;
    public int r;
    public int e;

    /**
     * Constructor
     *
     * @param n name
     * @param x x position
     * @param y y position
     * @param r radius
     * @param e energy
     */
    public Result(String n, int x, int y, int r, int e) {
        this.n = n;
        this.x = x;
        this.y = y;
        this.r = r;
        this.e = e;
    }
}
