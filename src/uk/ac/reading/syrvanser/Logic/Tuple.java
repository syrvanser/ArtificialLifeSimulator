package uk.ac.reading.syrvanser.Logic;

/**
 * Generic class implementing a 3-tuple
 * @author syrvanser
 * @since 15/01/2017
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class Tuple<T1, T2, T3> {

    private T1 first;
    private T2 second;
    private T3 third;

    /**
     * Constructor
     *
     * @param first  first element
     * @param second second element
     * @param third  third element
     */
    public Tuple(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Getter for the first element
     * @return first element
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Setter for the first element
     * @param first first element
     */
    public void setFirst(T1 first) {
        this.first = first;
    }

    /**
     * Getter for the second element
     * @return second element
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * Setter for the second element
     * @param second second element
     */
    public void setSecond(T2 second) {
        this.second = second;
    }

    /**
     * Getter for the third element
     * @return third element
     */
    public T3 getThird() {
        return third;
    }

    /**
     * Setter for the third element
     * @param third third element
     */
    public void setThird(T3 third) {
        this.third = third;
    }
}
