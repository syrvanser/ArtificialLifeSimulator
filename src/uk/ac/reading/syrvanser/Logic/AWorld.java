package uk.ac.reading.syrvanser.Logic;

import uk.ac.reading.syrvanser.Entities.*;
import uk.ac.reading.syrvanser.Graphics.GUIInterface;

import java.util.*;

import static uk.ac.reading.syrvanser.Logic.Direction.*;

/**
 * Representation of a single world
 * This class handles all logic and deals with entity management.
 * @author syrvanser
 * @since 10/10/2016.
 */
public class AWorld {
    private static Random rng = new Random();
    private int maxEntities;
    private int maxObstacles = 0;
    private int maxFood = 0;
    private int foodAmount;

    private byte maxLevel = 2; //current maximum hierarchy level

    private Set<AnEntity> objectsToRemove = new HashSet<>(); //objects to be removed
    private int lifeAmount;
    private int sizeX;
    private int sizeY;
    private List<AnEntity> entities; //list of all entities
    private List<Tuple<String, Integer, Byte>> speciesList = new ArrayList<>(); //3-Tuple with entity information

    /**
     * Constructor
     *
     * @param hSize       horizontal size
     * @param vSize       vertical size
     * @param maxEntities maximum allowed number of entities
     */
    public AWorld(int hSize, int vSize, int maxEntities) {
        this.sizeY = vSize;
        this.sizeX = hSize;
        this.maxEntities = maxEntities;
        entities = new ArrayList<>();
        foodAmount = 0;
        lifeAmount = 0;
    }

    /**
     * Default constructor
     */
    public AWorld() {
        this.sizeY = 10;
        this.sizeX = 10;
        this.maxEntities = 100;
        entities = new ArrayList<>();
        foodAmount = 0;
        lifeAmount = 0;
    }

    /**
     * Getter for the list of objects to be removed
     * @return list of entities
     */
    public Set<AnEntity> getObjectsToRemove() {
        return objectsToRemove;
    }

    /**
     * Setter for the list of objects to be removed
     * @param objectsToRemove list of entities
     */
    public void setObjectsToRemove(Set<AnEntity> objectsToRemove) {
        this.objectsToRemove = objectsToRemove;
    }

    /**
     * Getter for the maximum amount of obstacles
     * @return number of obstacles
     */
    public int getMaxObstacles() {
        return maxObstacles;
    }

    /**
     * Setter for the maximum amount of obstacles
     * @param maxObstacles number of obstacles
     */
    public void setMaxObstacles(int maxObstacles) {
        this.maxObstacles = maxObstacles;
    }

    /**
     * Getter for the maximum amount of food
     * @return number of food
     */
    public int getMaxFood() {
        return maxFood;
    }

    /**
     * Setter for the maximum amount of food
     * @param maxFood number of food
     */
    public void setMaxFood(int maxFood) {
        this.maxFood = maxFood;
    }

    /**
     * Getter for the list of 3-tuples
     * @return list with species information
     */
    public List<Tuple<String, Integer, Byte>> getSpeciesList() {
        return speciesList;
    }

    /**
     * Getter for the list of entities
     * @return list of entities
     */
    public List<AnEntity> getEntities() {
        return entities;
    }

    /**
     * Returns the current configuration
     * @return single line string of a pre-defined format
     */
    public String getCurrentConfig() {
        StringBuilder configuration = new StringBuilder(""); //using stringBuilder for memory efficiency
        configuration.append(sizeX).append(" ").append(sizeY).append(" ").append(maxFood * 100 / (sizeX * sizeY)).append(" ").append(maxObstacles * 100 / (sizeX * sizeY));
        for (Tuple<String, Integer, Byte> spec : //assemble the line
                speciesList) {
            configuration.append(" ").append(spec.getFirst()).append(" ").append(spec.getSecond());
        }
        return new String(configuration);
    }

    /**
     * Getter for the horizontal size
     * @return width
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Getter for the vertical size
     * @return height
     */
    public int getSizeY() {
        return sizeY;
    }

    /**
     * Method for resetting the world
     */
    public void clear() {
        entities.clear();
        AnEntity.resetEntityCounter();
        foodAmount = 0;
        lifeAmount = 0;
        maxLevel = 2;
    }

    /**
     * Function for resetting the configuration
     */
    public void clearConfig() {
        maxFood = 0;
        maxEntities = 0;
        speciesList.clear();
    }

    /**
     * Creates and adds an entity
     * @param str entity name
     * @return true if successful, false otherwise
     */
    private boolean addEntity(String str, int hPosition, int vPosition) {
        if (AnEntity.getEntityCounter() >= maxEntities) //return false if there is not enough space
            return false;
        AnEntity newEnt; //create new entity
        switch (str) {
            case "Food":
                int result = rng.nextInt(100);
                if (result < 10) { //10% chance
                    newEnt = new PoisonousFood(hPosition, vPosition, this);
                } else if (result < 30) { //20% chance
                    newEnt = new RenewableFood(hPosition, vPosition, this);
                } else { //70% chance
                    newEnt = new Food(hPosition, vPosition, this);
                }
                maxFood++;
                foodAmount++;
                break;
            case "Obstacle":
                if (rng.nextInt(100) > 10) { //90% chance
                    newEnt = new Obstacle(hPosition, vPosition, this);
                } else { //10% chance
                    newEnt = new Nest(hPosition, vPosition, this);
                }
                maxObstacles++;
                updateNests();
                break;
            default: //life forms
                byte level = -1;
                for (Tuple t : //check if there is an entity with the same name
                        speciesList) {
                    if (t.getFirst().equals(str)) {
                        level = (byte) t.getThird(); //get hierarchy level
                        break;
                    }
                }
                if (level == -1) { //if entity doesn't exist
                    if (rng.nextInt(100) > 25) { //75% to be herbivore
                        level = 1;
                    } else { //25% chance to be Carnivore
                        level = (byte) (rng.nextInt(maxLevel) + 2); //get level between 2 and maxLevel+1
                        if (level == maxLevel + 1)
                            maxLevel++;
                        if (level > 10) { //reset maxLevel if it's greater than 10
                            level = 10;
                        }
                    }
                }
                if (level == 1) { //herbivore
                    if (rng.nextInt(100) > 10) { // 90% chance
                        newEnt = new Herbivore(str, hPosition, vPosition, this);
                    } else { // 10% chance
                        newEnt = new PoisonousHerbivore(str, hPosition, vPosition, this);
                    }
                } else {
                    newEnt = new Carnivore(str, hPosition, vPosition, this, level);
                }
                lifeAmount++;
                break;

        }
        entities.add(newEnt);
        addTuple(new Tuple<>(newEnt.getSpecies(), 1, newEnt instanceof LifeForm ? ((LifeForm) newEnt).getLevel() : -1)); //add the new entity to the config
        return true;
    }

    /**
     * Creates and adds a new entity to a random position
     * @param str entity name
     * @return true if successful, false otherwise
     */
    public boolean addEntity(String str) {
        int hPosition;
        int vPosition;
        int counter = 0;
        int max = sizeX * sizeY * 100;
        do { //keep generating until a place is found
            if (counter > max) { //to prevent an infinite loop
                return false;
            }
            hPosition = rng.nextInt(sizeX);
            vPosition = rng.nextInt(sizeY);
            counter++;
        } while (getEntity(hPosition, vPosition) != null);

        return addEntity(str, hPosition, vPosition);
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
            if (e.getTargetX() == x && e.getTargetY() == y) //if coordinates match
                return e;
        }
        return null;
    }

    /**
     * @param x x position
     * @param y y position
     * @return true if can move, false if can't
     */
    private boolean canMove(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY)
            return false;
        AnEntity tmp = getEntity(x, y);
        return (tmp == null);
    }

    /**
     * Checks if there is food in the given cell and updates entity's energy if it's there
     * @param entity life form which eats food
     */
    private void eatFood(AnEntity entity) {
        int x = entity.getTargetX();
        int y = entity.getTargetY();
        entities.stream().filter(e -> e != entity && e != null && e instanceof Edible && e.getTargetX() == x && e.getTargetY() == y).forEach(e -> { //for all edible entities
            if (e instanceof Poisonous) { //kill if poisonous
                entity.setEnergy(0);
            } else {
                entity.setEnergy(entity.getEnergy() + e.getEnergy()); //update energy
            }
            if (e instanceof RenewableFood) {
                ((RenewableFood) e).eat(); //eat if renewable
            } else {
                objectsToRemove.add(e); //add to the removal set
                entities.set(entities.indexOf(e), null);
                foodAmount--;
            }
        });
    }

    /**
     * Method for updating all nests
     * Searches for all nests without species to spawn and assigns them a random species
     */
    public void updateNests() {
        entities.stream().filter(e -> e instanceof Nest).filter(e -> ((Nest) e).getLifeForm() == null).forEach(e -> ((Nest) e).setLifeForm(speciesList.size() > 0 ? speciesList.get(rng.nextInt(speciesList.size())).getFirst() : null));
    }

    /**
     * Method for getting statistics of te world
     * @return a multi-line string with text statistics about size and entities amounts
     */
    public String stats() {
        return "Width: " + sizeX + " Height: " + sizeY + "\nObstacles: " + maxObstacles + "\nFood left: " + foodAmount + " Life forms left: " + lifeAmount;
    }

    /**
     * Run simulation once
     * Emulates one round of the simulation
     * All entities are shuffled at the beginning of each round
     */
    public void run() {
        List<Nest> spawn = new ArrayList<>(); //list of nests
        Collections.shuffle(entities); //shuffle everything
        for (AnEntity ent : entities) { //for all entities
            if (ent instanceof Nest) { //if nest
                if (rng.nextInt(100) < 5) { //5% to span an entity
                    spawn.add((Nest) ent);
                }
            }
            if (ent instanceof RenewableFood && !((RenewableFood) ent).getCanEat()) { //update renewable food
                ((RenewableFood) ent).update();
            }
            if (ent instanceof LifeForm) { //move lifeforms
                LifeForm currentEntity = (LifeForm) ent;
                int energy = currentEntity.getEnergy();
                currentEntity.setEnergy(energy - 1); //lose 1 energy when moving
                List<Map.Entry<Direction, Integer>> directions = new ArrayList<>();

                //Add direction to the list if there's food in it
                int r = currentEntity.smellFood(N, currentEntity.getDetectionRadius());
                if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(N, r));

                r = currentEntity.smellFood(S, currentEntity.getDetectionRadius());
                if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(S, r));

                r = currentEntity.smellFood(E, currentEntity.getDetectionRadius());
                if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(E, r));

                r = currentEntity.smellFood(W, currentEntity.getDetectionRadius());
                if (r != -1) directions.add(new AbstractMap.SimpleEntry<>(W, r));

                if (directions.isEmpty()) { //if no food around move towards a random direction
                    int X = currentEntity.getTargetX();
                    int Y = currentEntity.getTargetY();
                    //check the the cell is free
                    if (canMove(X + 1, Y))
                        directions.add(new AbstractMap.SimpleEntry<>(E, 1));
                    if (canMove(X - 1, Y))
                        directions.add(new AbstractMap.SimpleEntry<>(W, 1));
                    if (canMove(X, Y + 1))
                        directions.add(new AbstractMap.SimpleEntry<>(S, 1));
                    if (canMove(X, Y - 1))
                        directions.add(new AbstractMap.SimpleEntry<>(N, 1));
                    //don't move if trapped
                    if (!directions.isEmpty()) {
                        int index = rng.nextInt(directions.size());
                        Direction d = directions.get(index).getKey();
                        currentEntity.move(d);
                    }

                } else { //if not empty pick a random direction from the list
                    int minimum = Integer.MAX_VALUE;

                    for (Map.Entry<Direction, Integer> entry : //pairs of directions and priorities
                            directions) {
                        if (entry.getValue() < minimum)
                            minimum = entry.getValue();
                    }

                    List<Direction> dir = new ArrayList<>(); //directions with biggest priority
                    for (Map.Entry<Direction, Integer> entry :
                            directions) {
                        if (entry.getValue() == minimum)
                            dir.add(entry.getKey());
                    }

                    int index = rng.nextInt(dir.size()); //pick a random direction
                    Direction d = dir.get(index);
                    currentEntity.move(d); //move
                }

                eatFood(currentEntity); //try eating food in this cell

                if (currentEntity.getEnergy() <= 0) { //kill entity if it's energy is below one
                    objectsToRemove.add(currentEntity);
                    entities.set(entities.indexOf(currentEntity), null);
                    lifeAmount--;
                }
            }
        }

        spawn.forEach(ent -> { //spawn entities for each nest
            if (ent.getLifeForm() != null) {
                addEntity(ent.getLifeForm(), ent.getTargetX(), ent.getTargetY());
            }
        });
        spawn.clear();

        //noinspection SuspiciousMethodCalls
        entities.removeAll(Collections.singleton(null)); //remove nulls
    }

    /**
     * Removes the given entity
     * @param e entity to remove
     */
    public void remove(AnEntity e) {
        entities.set(entities.indexOf(e), null);
        if (e instanceof LifeForm) {
            lifeAmount--;
        }
        if (e instanceof Food) {
            foodAmount--;
        }
    }

    /**
     * Adds a tuple of the entity name, amount ant level to the list
     *
     * @param tuple element to add
     */
    private void addTuple(Tuple<String, Integer, Byte> tuple) {
        if (tuple.getFirst().equals("Obstacle") || tuple.getFirst().equals("Food")) //skip if it isn't a life form
            return;
        for (Tuple<String, Integer, Byte> e :
                speciesList) {
            if (e.getFirst().equals(tuple.getFirst())) {
                e.setSecond(e.getSecond() + tuple.getSecond()); //add 1 if it is found
                return;
            }
        }
        speciesList.add(tuple); //add a fresh one otherwise
    }

    /**
     * Method for showing all entities
     * Shows both living entities and those which are marked to be removed
     * @param i interface to use
     */
    public void show(GUIInterface i) {
        for (AnEntity entity : entities) entity.display(i);
        for (AnEntity entity : objectsToRemove) entity.display(i);
    }

    /**
     * Checks if there is a species of the given tuple and updates its frequency
     *
     * @param tuple element to remove
     */
    public void removeTuple(Tuple<String, Integer, Byte> tuple) {
        if (tuple.getFirst().equals("Obstacle") || tuple.getFirst().equals("Food")) //return if isn't a life form
            return;
        for (Tuple<String, Integer, Byte> e : //iterate over all elements
                speciesList) {
            if (e.getFirst().equals(tuple.getFirst())) { //find the tuple
                e.setSecond(e.getSecond() - tuple.getSecond());
                if (e.getSecond() <= 0) { //remove if frequency goes negative
                    speciesList.remove(e);
                }
                return;
            }
        }
    }
}