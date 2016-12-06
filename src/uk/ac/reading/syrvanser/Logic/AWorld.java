package uk.ac.reading.syrvanser.Logic;

import uk.ac.reading.syrvanser.Entities.AnEntity;
import uk.ac.reading.syrvanser.Entities.Food;
import uk.ac.reading.syrvanser.Entities.LifeForm;
import uk.ac.reading.syrvanser.Entities.Obstacle;
import uk.ac.reading.syrvanser.Graphics.GUIInterface;

import java.util.*;

import static uk.ac.reading.syrvanser.Logic.Direction.*;

/**
 * Created by syrvanser on 10/10/2016.
 *
 * @author syrvanser
 */
public class AWorld {
    private static Random rng = new Random();
    private int maxEntities;
    private int maxObstacles = 0;
    private int maxFood = 0;
    private int foodAmount;


    private Set<AnEntity> objectsToRemove = new HashSet<>();
    private int lifeAmount;
    private int sizeX;
    private int sizeY;
    private List<AnEntity> entities;
    private List<Map.Entry<String, Integer>> speciesList = new ArrayList<>();

    public AWorld(int hSize, int vSize, int maxEntities) {
        this.sizeY = vSize;
        this.sizeX = hSize;
        this.maxEntities = maxEntities;
        entities = new ArrayList<>();
        foodAmount = 0;
        lifeAmount = 0;
    }

    public AWorld() {
        this.sizeY = 10;
        this.sizeX = 10;
        this.maxEntities = 100;
        entities = new ArrayList<>();
        foodAmount = 0;
        lifeAmount = 0;
    }

    public Set<AnEntity> getObjectsToRemove() {
        return objectsToRemove;
    }

    public void setObjectsToRemove(Set<AnEntity> objectsToRemove) {
        this.objectsToRemove = objectsToRemove;
    }

    public int getMaxObstacles() {
        return maxObstacles;
    }

    public void setMaxObstacles(int maxObstacles) {
        this.maxObstacles = maxObstacles;
    }

    public int getMaxFood() {
        return maxFood;
    }

    public void setMaxFood(int maxFood) {
        this.maxFood = maxFood;
    }

    public List<Map.Entry<String, Integer>> getSpeciesList() {
        return speciesList;
    }

    public List<AnEntity> getEntities() {
        return entities;
    }

    public String getCurrentConfig() {
        StringBuilder configuration = new StringBuilder("");
        configuration.append(sizeX).append(" ").append(sizeY).append(" ").append(maxFood * 100 / (sizeX * sizeY)).append(" ").append(maxObstacles * 100 / (sizeX * sizeY));
        for (Map.Entry<String, Integer> spec :
                speciesList) {
            configuration.append(" ").append(spec.getKey()).append(" ").append(spec.getValue());
        }
        return new String(configuration);
    }

    public int getSizeX() {
        return sizeX;
    }


    public int getSizeY() {
        return sizeY;
    }

    public void clear() {
        entities.clear();
        AnEntity.resetEntityCounter();
        foodAmount = 0;
        lifeAmount = 0;

    }


    public void clearConfig() {
        maxFood = 0;
        maxEntities = 0;
        speciesList.clear();
    }

    /**
     * Creates and adds an entity
     *
     * @param str entity name
     * @param c   first character
     * @return true if successful, false otherwise
     */
    public boolean addEntity(String str, char c) {
        //Scanner s = new Scanner(System.in);

        if (AnEntity.getEntityCounter() >= maxEntities)
            return false;
        //if (AnEntity.getEntityCounter() > sizeX * sizeY)
        //  return false;
        int hPosition;
        int vPosition;

        do {
            hPosition = rng.nextInt(sizeX);
            vPosition = rng.nextInt(sizeY);
        } while (getEntity(hPosition, vPosition) != null);
        AnEntity newEnt;
        switch (str) {
            case "Food":
                newEnt = new Food(hPosition, vPosition, this);
                maxFood++;
                foodAmount++;
                break;
            case "Obstacle":
                newEnt = new Obstacle(hPosition, vPosition, this);
                maxObstacles++;
                break;
            default:
                newEnt = new LifeForm(str, c, hPosition, vPosition, this);
                lifeAmount++;
                break;

        }
        entities.add(newEnt);
        addPair(new AbstractMap.SimpleEntry<>(newEnt.getSpecies(), 1));
        return true;
    }


    /**
     * Checks if there are any entities at the given position
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return entity's symbol, a whitespace if empty
     */
    public AnEntity getEntity(int x, int y) {
        for (AnEntity e :
                entities) {
            if (e == null)
                continue;
            if (e.getTargetX() == x && e.getTargetY() == y)
                return e;
        }
        return null;
    }


    /**
     * @param x x position
     * @param y y position
     * @return true if can move, false if can't
     */
    public boolean canMove(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY)
            return false;
        AnEntity tmp = getEntity(x, y);
        return (tmp == null || tmp instanceof Food);
    }


    /**
     * @param x x coordinate
     * @param y y coordinate
     * @return true if there is food at (x,y), false otherwise
     */
    public boolean checkFood(int x, int y) {
        return (getEntity(x, y) instanceof Food);
    }

    /**
     * Checks if there is food in the given cell and updates entity's energy if it's there
     *
     * @param entity which eats food
     */
    private void eatFood(AnEntity entity) {
        int x = entity.getTargetX();
        int y = entity.getTargetY();
        entities.stream().filter(e -> e != null && checkFood(x, y) && e.getTargetX() == x && e.getTargetY() == y).forEach(e -> {
            entity.setEnergy(entity.getEnergy() + e.getEnergy());
            objectsToRemove.add(e);
            entities.set(entities.indexOf(e), null);
            foodAmount--;
        });
        //entities.removeAll(Collections.singleton(null));
    }


    public String stats() {
        return "Width: " + sizeX + " Height : " + sizeY + "\nObstacles:" + maxObstacles + "\nFood left:" + foodAmount + " Life forms left: " + lifeAmount;
    }

    /**
     * Run simulation once
     */
    public void run() {
        for (AnEntity ent : entities) {
            if (ent != null && !(ent instanceof Food) && !(ent instanceof Obstacle)) {
                LifeForm e = (LifeForm) ent;
                int energy = e.getEnergy();

                e.setEnergy(energy - 1); //lose 1 energy when moving
                List<Map.Entry<Direction, Integer>> directions = new ArrayList<>();

                //Add direction to the list if there's food in it
                if (canMove(e.getTargetX(), e.getTargetY() - 1)) {
                    int r = e.smellFood(N, e.getDetectionRadius());
                    if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(N, r));
                }
                if (canMove(e.getTargetX(), e.getTargetY() + 1)) {
                    int r = e.smellFood(S, e.getDetectionRadius());
                    if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(S, r));
                }
                if (canMove(e.getTargetX() + 1, e.getTargetY())) {
                    int r = e.smellFood(E, e.getDetectionRadius());
                    if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(E, r));
                }
                if (canMove(e.getTargetX() - 1, e.getTargetY())) {
                    int r = e.smellFood(W, e.getDetectionRadius());
                    if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(W, r));
                }

                if (directions.isEmpty()) { //if no food around move to the random direction


              /*  System.out.println("DEBUG: random");
                System.out.println(e.smellFood(N, 5) + " " + canMove(e.getTargetX(), e.getTargetY() - 1));
                System.out.println(e.smellFood(S, 5) + " " + canMove(e.getTargetX(), e.getTargetY() + 1));
                System.out.println(e.smellFood(E, 5) + " " + canMove(e.getTargetX() + 1, e.getTargetY()));
                System.out.println(e.smellFood(W, 5) + " " + canMove(e.getTargetX() - 1, e.getTargetY())); */

                    Direction d = Direction.getRandomDirection(); //Pick a random direction
                    int randomX = e.getTargetX();
                    int randomY = e.getTargetY();
                    switch (d) {
                        case N:
                            randomY--;
                            break;
                        case S:
                            randomY++;
                            break;
                        case W:
                            randomX--;
                            break;
                        case E:
                            randomX++;
                            break;
                    }

                    if (canMove(randomX, randomY)) {
                        e.setTargetX(randomX);
                        e.setTargetY(randomY);
                    }
                } else { //if not empty pick a random direction from the list
                   /* System.out.println("DEBUG: food");
                    System.out.println(e.smellFood(N, 5) + " " + canMove(e.getTargetX(), e.getTargetY() - 1));
                    System.out.println(e.smellFood(S, 5) + " " + canMove(e.getTargetX(), e.getTargetY() + 1));
                    System.out.println(e.smellFood(E, 5) + " " + canMove(e.getTargetX() + 1, e.getTargetY()));
                    System.out.println(e.smellFood(W, 5) + " " + canMove(e.getTargetX() - 1, e.getTargetY()))*/
                    int minimum = Integer.MAX_VALUE;

                    for (Map.Entry<Direction, Integer> entry :
                            directions) {
                        if (entry.getValue() < minimum)
                            minimum = entry.getValue();

                    }
                    List<Direction> dir = new ArrayList<>();
                    for (Map.Entry<Direction, Integer> entry :
                            directions) {
                        if (entry.getValue() == minimum)
                            dir.add(entry.getKey());
                    }

                    int index = rng.nextInt(dir.size());
                    Direction d = dir.get(index);
                    switch (d) {
                        case N:
                            e.setTargetY(e.getTargetY() - 1);
                            break;
                        case E:
                            e.setTargetX(e.getTargetX() + 1);
                            break;
                        case S:
                            e.setTargetY(e.getTargetY() + 1);
                            break;
                        case W:
                            e.setTargetX(e.getTargetX() - 1);
                            break;
                    }
                }

                eatFood(e);


                if (e.getEnergy() <= 0) {
                    objectsToRemove.add(e);
                    entities.set(entities.indexOf(e), null);
                    lifeAmount--;
                }
            }
        }
        //noinspection SuspiciousMethodCalls
        entities.removeAll(Collections.singleton(null));
    }

    public void remove(AnEntity e) {
        entities.set(entities.indexOf(e), null);
        lifeAmount--;
    }

    private void addPair(Map.Entry<String, Integer> pair) {
        if (pair.getKey().equals("Obstacle") || pair.getKey().equals("Food"))
            return;
        for (Map.Entry<String, Integer> e :
                speciesList) {
            if (e.getKey().equals(pair.getKey())) {
                e.setValue(e.getValue() + pair.getValue());
                return;
            }
        }
        speciesList.add(pair);
    }

    public void show(GUIInterface i) {
        for (AnEntity entity : entities) entity.display(i);
        for (AnEntity entity : objectsToRemove) entity.display(i);
    }

    public void removePair(Map.Entry<String, Integer> pair) {
        if (pair.getKey().equals("Obstacle") || pair.getKey().equals("Food"))
            return;
        for (Map.Entry<String, Integer> e :
                speciesList) {
            if (e.getKey().equals(pair.getKey())) {
                e.setValue(e.getValue() - pair.getValue());
                if (e.getValue() <= 0) {
                    speciesList.remove(e);
                }
                return;
            }
        }
    }

}