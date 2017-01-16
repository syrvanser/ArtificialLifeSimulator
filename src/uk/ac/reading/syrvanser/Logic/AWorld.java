package uk.ac.reading.syrvanser.Logic;

import uk.ac.reading.syrvanser.Entities.*;
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

    private byte maxLevel = 2;

    private Set<AnEntity> objectsToRemove = new HashSet<>();
    private int lifeAmount;
    private int sizeX;
    private int sizeY;
    private List<AnEntity> entities;
    private List<Tuple<String, Integer, Byte>> speciesList = new ArrayList<>();

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

    public List<Tuple<String, Integer, Byte>> getSpeciesList() {
        return speciesList;
    }

    public List<AnEntity> getEntities() {
        return entities;
    }

    public String getCurrentConfig() {
        StringBuilder configuration = new StringBuilder("");
        configuration.append(sizeX).append(" ").append(sizeY).append(" ").append(maxFood * 100 / (sizeX * sizeY)).append(" ").append(maxObstacles * 100 / (sizeX * sizeY));
        for (Tuple<String, Integer, Byte> spec :
                speciesList) {
            configuration.append(" ").append(spec.getFirst()).append(" ").append(spec.getSecond());
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
        maxLevel = 2;

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
     * @return true if successful, false otherwise
     */
    private boolean addEntity(String str, int hPosition, int vPosition) {
        //Scanner s = new Scanner(System.in);

        if (AnEntity.getEntityCounter() >= maxEntities)
            return false;
        //if (AnEntity.getEntityCounter() > sizeX * sizeY)
        //  return false;

        AnEntity newEnt;
        switch (str) {
            case "Food":
                int result = rng.nextInt(100);
                if (result < 10) {
                    newEnt = new PoisonousFood(hPosition, vPosition, this);
                } else if (result < 30) {
                    newEnt = new RenewableFood(hPosition, vPosition, this);
                } else {
                    newEnt = new Food(hPosition, vPosition, this);
                    //System.out.println();
                }
                maxFood++;
                foodAmount++;
                break;
            case "Obstacle":
                if (rng.nextInt(100) > 10) {
                    newEnt = new Obstacle(hPosition, vPosition, this);
                } else {
                    newEnt = new Nest(hPosition, vPosition, this);
                }
                maxObstacles++;
                break;
            default:
                byte level = -1;
                for (Tuple t :
                        speciesList) {
                    if (t.getFirst().equals(str)) {
                        level = (byte) t.getThird();
                        break;
                    }
                }
                if (level == -1) {
                    if (rng.nextInt(100) > 25) {
                        level = 1;
                    } else {

                        level = (byte) (rng.nextInt(maxLevel) + 2);
                        if (level == maxLevel + 1)
                            maxLevel++;
                    }
                }
                if (level == 1) {
                    if (rng.nextInt(100) > 10) {
                        newEnt = new Herbivore(str, hPosition, vPosition, this);
                    } else {
                        newEnt = new PoisonousHerbivore(str, hPosition, vPosition, this);
                    }
                } else {
                    newEnt = new Carnivore(str, hPosition, vPosition, this, level);
                }
                lifeAmount++;
                break;

        }
        entities.add(newEnt);
        addTuple(new Tuple<>(newEnt.getSpecies(), 1, newEnt instanceof LifeForm ? ((LifeForm) newEnt).getLevel() : -1));
        return true;
    }


    public boolean addEntity(String str) {
        int hPosition;
        int vPosition;

        do {
            hPosition = rng.nextInt(sizeX);
            vPosition = rng.nextInt(sizeY);
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
    private boolean canMove(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY)
            return false;
        AnEntity tmp = getEntity(x, y);
        return (tmp == null);
    }

    /**
     * Checks if there is food in the given cell and updates entity's energy if it's there
     *
     * @param entity which eats food
     */
    private void eatFood(AnEntity entity) {
        int x = entity.getTargetX();
        int y = entity.getTargetY();
        entities.stream().filter(e -> e != entity && e != null && e instanceof Edible && e.getTargetX() == x && e.getTargetY() == y).forEach(e -> {
            if (e instanceof Poisonous) {
                entity.setEnergy(0);
            } else {
                entity.setEnergy(entity.getEnergy() + e.getEnergy());
            }
            if (e instanceof RenewableFood) {
                ((RenewableFood) e).eat();
            } else {
                objectsToRemove.add(e);
                entities.set(entities.indexOf(e), null);
                foodAmount--;
            }
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
        Map<Integer, Integer> spawn = new HashMap<>();
        Collections.shuffle(entities);
        for (AnEntity ent : entities) {
            if (ent instanceof Nest) {
                if (rng.nextInt(100) < 5) {
                    spawn.put(ent.getTargetX(), ent.getTargetY());
                }
            }
            if (ent instanceof RenewableFood && !((RenewableFood) ent).getCanEat()) {
                ((RenewableFood) ent).update();
            }
            if (ent instanceof LifeForm) {
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

                if (directions.isEmpty()) { //if no food around move to the random direction
                    //System.out.println("Empty");
                    int X = currentEntity.getTargetX();
                    int Y = currentEntity.getTargetY();
                    if (canMove(X + 1, Y))
                        directions.add(new AbstractMap.SimpleEntry<>(E, 1));
                    if (canMove(X - 1, Y))
                        directions.add(new AbstractMap.SimpleEntry<>(W, 1));
                    if (canMove(X, Y + 1))
                        directions.add(new AbstractMap.SimpleEntry<>(S, 1));
                    if (canMove(X, Y - 1))
                        directions.add(new AbstractMap.SimpleEntry<>(N, 1));
                    if (!directions.isEmpty()) {
                        int index = rng.nextInt(directions.size());
                        Direction d = directions.get(index).getKey();
                        currentEntity.move(d);
                    }

                } else { //if not empty pick a random direction from the list
                    // System.out.println("Not empty");
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
                    currentEntity.move(d);
                }

                eatFood(currentEntity);


                if (currentEntity.getEnergy() <= 0) {
                    objectsToRemove.add(currentEntity);
                    entities.set(entities.indexOf(currentEntity), null);
                    lifeAmount--;
                }
            }
        }

        spawn.forEach((x, y) -> addEntity(speciesList.get(rng.nextInt(speciesList.size())).getFirst(), x, y));
        spawn.clear();

        //noinspection SuspiciousMethodCalls
        entities.removeAll(Collections.singleton(null));
    }

    public void remove(AnEntity e) {
        entities.set(entities.indexOf(e), null);
        lifeAmount--;
    }

    private void addTuple(Tuple<String, Integer, Byte> pair) {
        if (pair.getFirst().equals("Obstacle") || pair.getFirst().equals("Food"))
            return;
        for (Tuple<String, Integer, Byte> e :
                speciesList) {
            if (e.getFirst().equals(pair.getFirst())) {
                e.setSecond(e.getSecond() + pair.getSecond());
                return;
            }
        }
        speciesList.add(pair);
    }

    public void show(GUIInterface i) {
        for (AnEntity entity : entities) entity.display(i);
        for (AnEntity entity : objectsToRemove) entity.display(i);
    }

    public void removeTuple(Tuple<String, Integer, Byte> pair) {
        if (pair.getFirst().equals("Obstacle") || pair.getFirst().equals("Food"))
            return;
        for (Tuple<String, Integer, Byte> e :
                speciesList) {
            if (e.getFirst().equals(pair.getFirst())) {
                e.setSecond(e.getSecond() - pair.getSecond());
                if (e.getSecond() <= 0) {
                    speciesList.remove(e);
                }
                return;
            }
        }
    }
}