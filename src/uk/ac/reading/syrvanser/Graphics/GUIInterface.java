package uk.ac.reading.syrvanser.Graphics;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.ac.reading.syrvanser.Entities.*;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Result;
import uk.ac.reading.syrvanser.Logic.Tuple;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by syrvanser on 17/10/2016.
 * Main interface class and the entry point of the program
 * Handles all gui-related tasks, including animation and buttons
 * @author syrvanser
 */
public class GUIInterface extends Application {

    private static final String ERROR_STYLE = " -fx-background-radius: 25px; -fx-background-color: coral; -fx-padding: 4px;"; //style for errors
    private static final int SCREEN_SIZE = 500; //screen width, in pixels
    private static final int fps = 60; //target number of frames per second
    public static int imageSize = 50; //default image size, in pixels
    private long simulationSpeed = 1000000000; //default animation speed, in nanoseconds (1 second)
    private double numOfUpdates = (fps * simulationSpeed) / (1000000000) + 1; //default number of updates per round
    private double deltaT = simulationSpeed / (numOfUpdates - 1); // default time dedicated per 1 animation update
    private double distance = imageSize / numOfUpdates; //default distance to be traveled by entities per one animation update
    private double fadeRate = 1 / numOfUpdates; //default fading rate for dying entities
    private boolean isRunning = false; //flag for checking if animation is running
    private AWorld world = new AWorld(); // program's world
    private Properties currentProperties = new Properties(); //variable for current world's properties
    private File propFile; //location of the current properties file
    private File lastConf; //location of the last properties file
    private Stage stagePrimary; //animation's stage
    private GraphicsContext gc; //animation's graphic context
    private FileChooser propFC = new FileChooser(); //file chooser
    private Canvas canvas; //animation's canvas

    /**
     * Entry point
     *
     * @param args extra arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Checks if a string represents a whole number
     * @param str string to check
     * @return true if it is a number, false otherwise
     */
    private static boolean isNumeric(String str) {
        return str.matches("^-?\\d+$");  //match a number with optional '-'
    }

    /**
     * Converts image to imageView with a predefined size
     * @param img Image to convert
     * @return image wrapped in ImageView
     */
    private ImageView imgToImgView(Image img) {
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(50); //50px
        imgView.setFitWidth(50); //50px
        return imgView;
    }

    /**
     * Sets up the menu bar, buttons and properties files
     * @return generated menu bar
     */
    private MenuBar setMenu() {
        MenuBar menuBar = new MenuBar();

        File configDirectory = new File("./configurations"); //directory for configuration files
        lastConf = new File(configDirectory, "last.properties"); //property file
        propFC.setInitialDirectory(configDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Configuration files (*.properties)", "*.properties");
        propFC.getExtensionFilters().add(extFilter);
        InputStream inputStream;
        try { //try loading from last configuration
            inputStream = new FileInputStream(lastConf);
            currentProperties.load(inputStream);
            inputStream.close();
        } catch (IOException e) { //if it fails, try creating a new file
            try {
                //noinspection ResultOfMethodCallIgnored
                configDirectory.mkdirs();
                //noinspection ResultOfMethodCallIgnored
                lastConf.delete();
                //noinspection ResultOfMethodCallIgnored
                lastConf.createNewFile();

            } catch (IOException e1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to create directory for config files!");
                alert.showAndWait();
            }
        }


        Menu mFile = new Menu("File");
        Menu mView = new Menu("View");
        Menu mEdit = new Menu("Edit");
        Menu mSimulation = new Menu("Simulation");
        Menu mHelp = new Menu("Help");

        MenuItem mNew = new MenuItem("New");
        mNew.setOnAction(actionEvent -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("New world");
            dialog.setHeaderText("Select settings for the new world: ");
            ButtonType done = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);

            dialog.getDialogPane().getButtonTypes().addAll(done, ButtonType.CANCEL);

            Node doneButton = dialog.getDialogPane().lookupButton(done);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 5, 10, 5));

            //Create slider for x
            Slider sldX = new Slider(1, 50, 10);
            sldX.setShowTickMarks(true);
            sldX.setMajorTickUnit(10);
            sldX.setMinorTickCount(9);
            sldX.setPrefWidth(250);
            sldX.setSnapToTicks(true);

            //Create slider for y
            Slider sldY = new Slider(1, 50, 10);
            sldY.setShowTickMarks(true);
            sldY.setMajorTickUnit(10);
            sldY.setMinorTickCount(9);
            sldY.setSnapToTicks(true);

            //Create slider for food
            Slider sldFood = new Slider(0, 100, 0);
            sldFood.setShowTickLabels(true);
            sldFood.setShowTickMarks(true);
            sldFood.setMinorTickCount(9);
            sldFood.setMajorTickUnit(10);
            sldFood.setSnapToTicks(true);

            //Create slider for obstacles
            Slider sldObs = new Slider(0, 100, 0);
            sldObs.setShowTickLabels(true);
            sldObs.setShowTickMarks(true);
            sldObs.setMinorTickCount(9);
            sldObs.setMajorTickUnit(10);
            sldObs.setSnapToTicks(true);

            //Textfield for entities
            TextField ent = new TextField();
            ent.setText("");

            //Labels for sliders
            Label lblFood = new Label("0%");
            Label lblObs = new Label("0%");
            Label lblX = new Label("10");
            Label lblY = new Label("10");

            //Error labels
            Label foodError = new Label("Food + obstacles should be less than 100%!");
            foodError.setVisible(false);
            foodError.setStyle(ERROR_STYLE);


            Label obsError = new Label("Food + obstacles should be less than 100%!");
            obsError.setVisible(false);
            obsError.setStyle(ERROR_STYLE);

            Label speciesError = new Label("Wrong format!");
            speciesError.setVisible(false);
            speciesError.setStyle(ERROR_STYLE);

            sldX.valueProperty().addListener((observable, oldValue, newValue) -> lblX.setText(newValue.intValue() + ""));
            sldY.valueProperty().addListener((observable, oldValue, newValue) -> lblY.setText(newValue.intValue() + ""));

            sldFood.valueProperty().addListener((observed, oldVal, newVal) -> { //listener for food slider
                lblFood.setText(newVal.intValue() + "%");
                if (newVal.intValue() + sldObs.getValue() > 100) {
                    doneButton.setDisable(true);
                    foodError.setVisible(true);
                } else {
                    doneButton.setDisable(false);
                    foodError.setVisible(false);
                    obsError.setVisible(false);
                }
            });

            sldObs.valueProperty().addListener((observed, oldVal, newVal) -> { //listener for obstacle slider
                lblObs.setText(newVal.intValue() + "%");
                if (sldFood.getValue() + newVal.intValue() > 100) {
                    doneButton.setDisable(true);
                    obsError.setVisible(true);
                } else {
                    doneButton.setDisable(false);
                    obsError.setVisible(false);
                    foodError.setVisible(false);
                }
            });

            ent.textProperty().addListener((observed, oldText, newText) -> { //parse entities
                if (!newText.equals("")) { //if not empty
                    String[] array = newText.split(" "); //split using whitespaces
                    int counter = 0; //4th argument in the string
                    try {
                        while (counter < array.length) { //while not finished
                            if (array[counter].equals("")) { //throw an exception if one of the parameters is empty
                                throw new NumberFormatException();
                            }
                            if (Integer.parseInt(array[counter + 1]) < 0) {
                                throw new NumberFormatException();
                            }
                            counter += 2;
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { //catch both exceptions
                        doneButton.setDisable(true);
                        speciesError.setVisible(true);
                    }
                }
                doneButton.setDisable(false);
                speciesError.setVisible(false);
            });

            //Add everything to the grid
            grid.add(new Label("Width:"), 0, 0);
            grid.add(sldX, 1, 0);
            grid.add(lblX, 2, 0);

            grid.add(new Label("Height:"), 0, 1);
            grid.add(sldY, 1, 1);
            grid.add(lblY, 2, 1);

            grid.add(new Label("Obstacles percentage:"), 0, 2);
            grid.add(sldObs, 1, 2);
            grid.add(lblObs, 2, 2);
            grid.add(obsError, 3, 2);

            grid.add(new Label("Food percentage:"), 0, 3);
            grid.add(sldFood, 1, 3);
            grid.add(lblFood, 2, 3);
            grid.add(foodError, 3, 3);

            grid.add(new Label("Species (optional)"), 0, 4);
            grid.add(ent, 1, 4);
            grid.add(speciesError, 2, 4);

            dialog.getDialogPane().setContent(grid);

            final boolean[] flag = {false}; //flag for stopping the while loop

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == done) {
                    return (int) sldX.getValue() + " " + (int) sldY.getValue() + " " + (int) sldFood.getValue() + " " + (int) sldObs.getValue() + " " + ent.getText();
                }
                return null;
            });

            do {

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(res -> {
                    propFile = new File(configDirectory, "new.properties"); //reset everything
                    currentProperties = new Properties();
                    world.clearConfig();
                    world.clear();
                    if (!fromText(res)) { //try parsing parameters
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Wrong configuration!");
                        alert.setContentText("Try again!");
                        alert.showAndWait();
                        flag[0] = true;
                    } else {
                        flag[0] = false;
                    }
                });
                final Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL); //close if cancel is pressed
                cancel.addEventFilter(ActionEvent.ACTION, event -> {
                    flag[0] = false;
                    fromFile(currentProperties);
                });
            } while (flag[0]);

        });

        MenuItem mOpen = new MenuItem("Open");
        mOpen.setOnAction(actionEvent -> {
            File file = propFC.showOpenDialog(stagePrimary); //open file chooser
            InputStream input;
            try {
                input = new FileInputStream(file);
                currentProperties.load(input); //load from file
                fromFile(currentProperties);
                input.close();
                propFile = file;
            } catch (IOException e) { //show an error message if something is wrong
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Something went wrong!");
                alert.showAndWait();
            } catch (NullPointerException ignored) {
            }
        });

        MenuItem mSave = new MenuItem("Save");
        mSave.setOnAction(actionEvent -> save(propFile));

        MenuItem mSaveAs = new MenuItem("Save as");
        mSaveAs.setOnAction(actionEvent -> save());

        MenuItem mExit = new MenuItem("Exit");
        mExit.setOnAction(actionEvent -> Platform.exit());

        mFile.getItems().addAll(mNew, mOpen, mSave, mSaveAs, mExit);


        MenuItem mViewConf = new MenuItem("View configuration");
        mViewConf.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setContentText(world.getCurrentConfig());
            alert.showAndWait();
        });

        MenuItem mEditConf = new MenuItem("Edit configuration");
        mEditConf.setOnAction(actionEvent -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Edit configuration");
            dialog.setHeaderText("Please enter the new configuration:");
            dialog.setContentText("e. g. 10 10 20 20 ant 1");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(conf -> { //show an error message if parsing fails
                if (!fromText(conf)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Wrong config line");
                    alert.showAndWait();
                }
            });
        });

        MenuItem mViewLifeInfo = new MenuItem("Life info");
        mViewLifeInfo.setOnAction(actionEvent -> { //info tab
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entities Info");
            alert.setHeaderText("Entities stats:");
            TextArea textArea = new TextArea(list());
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        });

        MenuItem mViewMapInfo = new MenuItem("Map info");
        mViewMapInfo.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("World Info");
            alert.setContentText(world.stats());
            alert.showAndWait();
        });

        mView.getItems().addAll(mViewConf, mEditConf, mViewLifeInfo, mViewMapInfo);


        MenuItem mModify = new MenuItem("Modify");
        mModify.setOnAction(actionEvent -> { //Modification
            List<LifeForm> choices = world.getEntities().stream().filter(e -> e instanceof LifeForm).map(e -> (LifeForm) e).collect(Collectors.toList()); //all life forms
            if (choices.size() == 0) { //check that there are some lifeforms
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Nothing to modify!");
                alert.setContentText("Add some entities first");
                alert.showAndWait();
            } else {
                ChoiceDialog<LifeForm> dialog1 = new ChoiceDialog<>(choices.get(0), choices);
                dialog1.setTitle("Entity selection");
                dialog1.setHeaderText("Choose an entity");
                dialog1.setContentText("Pick from the list:");

                Optional<LifeForm> entity = dialog1.showAndWait();

                entity.ifPresent(e -> {

                    Dialog<Result> dialog = new Dialog<>();
                    dialog.setTitle("Life form editor");
                    dialog.setHeaderText("Choose what you would like to edit: ");

                    ButtonType done = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);

                    dialog.getDialogPane().getButtonTypes().addAll(done, ButtonType.CANCEL);

                    Node doneButton = dialog.getDialogPane().lookupButton(done);

                    GridPane grid = new GridPane(); //grid for the window
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 5, 10, 5));

                    //Text fields
                    TextField name = new TextField();
                    name.setText(e.getSpecies());

                    TextField x = new TextField();
                    x.setText(e.getTargetX() + "");

                    TextField y = new TextField();
                    y.setText(e.getTargetY() + "");

                    TextField d = new TextField();
                    d.setText(e.getDetectionRadius() + "");

                    //Labels
                    Label xError = new Label("Must be a number less between 0 and " + (world.getSizeX() - 1));
                    xError.setVisible(false);
                    xError.setStyle(ERROR_STYLE);

                    Label yError = new Label("Must be a number less between 0 and " + (world.getSizeY() - 1));
                    yError.setVisible(false);
                    yError.setStyle(ERROR_STYLE);

                    Label dError = new Label("Must be a number less between 0 and 100");
                    dError.setVisible(false);
                    dError.setStyle(ERROR_STYLE);

                    Label eError = new Label("Must be a number less between 0 and 10000");
                    eError.setVisible(false);
                    eError.setStyle(ERROR_STYLE);

                    TextField en = new TextField();
                    en.setText(e.getEnergy() + "");
                    //Validation
                    x.textProperty().addListener((obs, oldText, newText) -> {
                        if (!(isNumeric(newText) && Integer.parseInt(newText) >= 0 && Integer.parseInt(newText) < world.getSizeX())) {
                            doneButton.setDisable(true);
                            xError.setVisible(true);
                        } else {
                            doneButton.setDisable(false);
                            xError.setVisible(false);
                        }
                    });

                    y.textProperty().addListener((obs, oldText, newText) -> {

                        if (!(isNumeric(newText) && Integer.parseInt(newText) >= 0 && Integer.parseInt(newText) < world.getSizeY())) {
                            doneButton.setDisable(true);
                            yError.setVisible(true);
                        } else {
                            doneButton.setDisable(false);
                            yError.setVisible(false);
                        }
                    });

                    d.textProperty().addListener((obs, oldText, newText) -> {
                        if (!(isNumeric(newText) && Integer.parseInt(newText) >= 0 && Integer.parseInt(newText) < 100)) {
                            doneButton.setDisable(true);
                            dError.setVisible(true);
                        } else {
                            doneButton.setDisable(false);
                            dError.setVisible(false);
                        }
                    });

                    en.textProperty().addListener((obs, oldText, newText) -> {
                        if (!(isNumeric(newText) && Integer.parseInt(newText) >= 0 && Integer.parseInt(newText) < 10000)) {
                            doneButton.setDisable(true);
                            eError.setVisible(true);
                        } else {
                            doneButton.setDisable(false);
                            eError.setVisible(false);
                        }

                    });

                    //add everything to the grid
                    grid.add(new Label("Name:"), 0, 0);
                    grid.add(name, 1, 0);

                    grid.add(new Label("x:"), 0, 1);
                    grid.add(x, 1, 1);
                    grid.add(xError, 2, 1);

                    grid.add(new Label("y:"), 0, 2);
                    grid.add(y, 1, 2);
                    grid.add(yError, 2, 2);

                    grid.add(new Label("Detection range:"), 0, 3);
                    grid.add(d, 1, 3);
                    grid.add(dError, 2, 3);

                    grid.add(new Label("Energy:"), 0, 4);
                    grid.add(en, 1, 4);
                    grid.add(eError, 2, 4);

                    dialog.getDialogPane().setContent(grid);
                    final boolean[] flag = {false}; //flag for exiting the while loop

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == done) {
                            return new Result(name.getText(), Integer.parseInt(x.getText()), Integer.parseInt(y.getText()), Integer.parseInt(d.getText()), Integer.parseInt(en.getText())); //return all values
                        }
                        return null; //return null if cancel is pressed
                    });

                    do { //keep displaying the window
                        Optional<Result> result = dialog.showAndWait();
                        result.ifPresent(res -> {
                            AnEntity possibleEntity = world.getEntity(res.x, res.y);
                            if (possibleEntity == null) { //if empty
                                e.setSpecies(res.n);
                                e.setTargetX(res.x);
                                e.setTargetY(res.y);
                                e.setCurrentX(res.x * imageSize);
                                e.setCurrentY(res.y * imageSize);
                                e.setDetectionRadius(res.r);
                                e.setEnergy(res.e);
                                flag[0] = false; //exit
                            } else if (possibleEntity == e) { //if entity didn't move
                                flag[0] = false; //exit
                            } else { //error
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("This position is occupied!");
                                alert.setContentText("Try again!");
                                alert.showAndWait();
                                flag[0] = true; //show again
                            }
                        });

                        final Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                        cancel.addEventFilter(ActionEvent.ACTION, event -> flag[0] = false); //exit if cancel is pressed
                    } while (flag[0]);
                });
            }
            draw();
        });


        MenuItem mRemove = new MenuItem("Remove");
        mRemove.setOnAction(actionEvent -> {
            List<AnEntity> choices = world.getEntities().stream().filter(e -> e instanceof LifeForm).collect(Collectors.toList()); //all lifeforms
            if (choices.size() == 0) { //if there is something to remove
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Nothing to remove!");
                alert.setContentText("Add some entities first");
                alert.showAndWait();
            } else {
                ChoiceDialog<AnEntity> dialog1 = new ChoiceDialog<>(choices.get(0), choices);
                dialog1.setTitle("Entity selection");
                dialog1.setHeaderText("Choose an entity");
                dialog1.setContentText("Pick from the list:");

                Optional<AnEntity> entity = dialog1.showAndWait();

                entity.ifPresent(e -> {
                    world.remove(e);
                    world.removeTuple(new Tuple<>(e.getSpecies(), 1, (byte) 1)); //subtract one from the life form amount
                    //noinspection SuspiciousMethodCalls
                    world.getEntities().removeAll(Collections.singleton(null));
                });
            }
            draw(); //redraw
        });

        MenuItem mAdd = new MenuItem("Add");
        mAdd.setOnAction(actionEvent -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add a life form");
            dialog.setHeaderText("Please enter the new life form's name ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(this::add); //just ask for a string and feed it to add function
            draw();
        });

        mEdit.getItems().addAll(mModify, mRemove, mAdd);

        MenuItem mRun = new MenuItem("Run");
        mRun.setOnAction(actionEvent -> isRunning = true);

        MenuItem mPause = new MenuItem("Pause");
        mPause.setOnAction(actionEvent -> isRunning = false);

        MenuItem mRestart = new MenuItem("Restart");
        mRestart.setOnAction(actionEvent -> fromText(world.getCurrentConfig())); //reload current config

        mSimulation.getItems().addAll(mRun, mPause, mRestart);

        MenuItem mAbout = new MenuItem("About Program");
        mAbout.setOnAction(actionEvent -> {
            Dialog dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("About");
            dialog.setContentText("Available entities: ");
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            //Show all images
            grid.add(imgToImgView(Obstacle.classImage), 0, 0);
            grid.add(new Label("Obstacle - a solid wall"), 1, 0);

            grid.add(imgToImgView(Food.classImage), 0, 1);
            grid.add(new Label("Food - can be eaten by herbivore life forms"), 1, 1);

            grid.add(imgToImgView(PoisonousFood.classImage), 0, 2);
            grid.add(new Label("Poisonous Food - life forms die when they eat it"), 1, 2);

            grid.add(imgToImgView(RenewableFood.classImage), 0, 3);
            grid.add(new Label("Renewable Food - respawns after time"), 1, 3);

            grid.add(imgToImgView(RenewableFood.classImageGrowing), 0, 4);
            grid.add(new Label("Growing Renewable Food - looks like that when it's growing"), 1, 4);

            grid.add(imgToImgView(Herbivore.classImage), 0, 5);
            grid.add(new Label("Herbivore - can only eat basic food"), 1, 5);

            grid.add(imgToImgView(PoisonousHerbivore.classImage), 0, 6);
            grid.add(new Label("Poisonous Herbivore - predators die when eat it"), 1, 6);

            grid.add(imgToImgView(Carnivore.classImage), 0, 7);
            grid.add(new Label("Carnivore - hunt other entities, can't eat basic food"), 1, 7);

            grid.add(imgToImgView(Nest.classImage), 0, 8);
            grid.add(new Label("Nest - randomly spawns entities"), 1, 8);

            dialog.getDialogPane().setContent(grid);
            dialog.showAndWait();
        });
        MenuItem mInfo = new MenuItem("Info");
        mInfo.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Information:");
            alert.setContentText("Artificial life simulator\nVersion №10\nDeveloped by Ivan Syrovoiskii\nLast release: 16/01/17\n\nRoses are red,\nViolets are blue\nExhausting all-nighter\nI choose you");
            alert.showAndWait();

        });
        mHelp.getItems().addAll(mAbout, mInfo);

        menuBar.getMenus().addAll(mFile, mView, mEdit, mSimulation, mHelp);

        return menuBar;

    }

    /**
     * Function for adding an entity
     * @param s entity's name
     * @return true if successful, false otherwise
     */
    private boolean add(String s) {
        return world.addEntity(s);
    }

    /**
     * Lists all entities
     * @return multi-line string with all entities
     */
    private String list() {
        List<AnEntity> entities = world.getEntities();
        StringBuilder result = new StringBuilder(""); //using StringBuilder for more efficiency
        for (AnEntity e :
                entities) {
            if (e == null)
                continue;
            result.append(e.toText()).append("\n");
        }
        return result.toString();
    }


    /**
     * Accepts a string and uses it to initialise the world
     * @param input String to convert form
     * @return true if successful, false otherwise
     */
    private boolean fromText(String input) {

        try {
            String[] array = input.split(" "); //split using whitespaces
            int x = Integer.parseInt(array[0]);
            int y = Integer.parseInt(array[1]);
            world.clear(); //clear all settings
            currentProperties = new Properties();
            world = new AWorld(x, y, x * y); //create a new world
            imageSize = world.getSizeX() > world.getSizeY() ? (SCREEN_SIZE / world.getSizeX()) : (SCREEN_SIZE / world.getSizeY()); //calculate new image size
            distance = imageSize / numOfUpdates; //calculate new distance
            canvas.setHeight(imageSize * y); //resize canvas
            canvas.setWidth(imageSize * x);
            int foodNum = Integer.parseInt(array[2]);
            int obsNum = Integer.parseInt(array[3]);
            int foodAmount = world.getSizeY() * world.getSizeX() * foodNum / 100;
            int obsAmount = world.getSizeY() * world.getSizeX() * obsNum / 100;

            for (int i = 0; i < foodAmount; i++) { //add food
                add("Food");
            }

            world.setMaxFood(foodAmount);

            for (int i = 0; i < obsAmount; i++) { //add obstacles
                add("Obstacle");
            }
            world.setMaxObstacles(obsAmount);
            int counter = 4; //4th argument in the string
            while (counter < array.length) { //while not finished
                String form = array[counter];
                int num = Integer.parseInt(array[counter + 1]);
                for (int i = 0; i < num; i++) {
                    add(form);
                }
                counter += 2;
            }
            isRunning = false;
            save(lastConf); //save config

        } catch (Exception e) {
            return false;
        }
        world.updateNests(); //update all nests using new entities
        draw();
        return true;
    }

    /**
     * Creates a new world using a file with settings
     * @param prop properties to use
     * @return true if successful, false otherwise
     */
    private boolean fromFile(Properties prop) {
        try {
            int loadedWidth = Integer.parseInt(prop.getProperty("width"));
            int loadedHeight = Integer.parseInt(prop.getProperty("height"));
            world.clear(); //clear everything
            world.clearConfig();
            currentProperties = new Properties();
            world = new AWorld(loadedWidth, loadedHeight, loadedHeight * loadedWidth); //create a new world
            imageSize = world.getSizeX() > world.getSizeY() ? (SCREEN_SIZE / world.getSizeX()) : (SCREEN_SIZE / world.getSizeY()); //calculate image size
            distance = imageSize / numOfUpdates; //calculate distance
            canvas.setHeight(imageSize * loadedHeight);
            canvas.setWidth(imageSize * loadedWidth);
            int loadedObstacles = Integer.parseInt(prop.getProperty("obs"));
            int loadedFood = Integer.parseInt(prop.getProperty("food"));
            Set<String> keys = prop.stringPropertyNames(); //get all keys

            int foodAmount = world.getSizeY() * world.getSizeX() * loadedFood / 100;
            int obsAmount = world.getSizeY() * world.getSizeX() * loadedObstacles / 100;
            for (int i = 0; i < foodAmount; i++) { //add food
                add("Food");
            }
            world.setMaxFood(foodAmount);
            for (int i = 0; i < obsAmount; i++) { //add obstacles
                add("Obstacle");
            }
            world.setMaxObstacles(obsAmount);
            for (String form : keys) { //for all keys
                if (form.equals("food") || form.equals("obs") || form.equals("width") || form.equals("height")) //skip if it's a know key
                    continue;
                int num = Integer.parseInt(prop.getProperty(form));
                for (int i = 0; i < num; i++) {
                    add(form);
                }
            }
            world.updateNests(); //update all nests
            isRunning = false;
            save(lastConf);
            draw();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Saves the current configuration to the last location
     */
    private void save() {
        File file = propFC.showSaveDialog(stagePrimary);
        save(file);
    }

    /**
     * Saves the current configuration to a specified location
     * @param file Location for the new file
     */
    private void save(File file) {
        OutputStream output;
        int width = world.getSizeX();
        int height = world.getSizeY();
        int food = world.getMaxFood() * 100 / (world.getSizeX() * world.getSizeY()); //calculate food percentage
        int obs = world.getMaxObstacles() * 100 / (world.getSizeX() * world.getSizeY()); //calculate obstacles percentage
        List<Tuple<String, Integer, Byte>> entities = world.getSpeciesList(); //get the list with properties

        currentProperties.setProperty("width", width + "");
        currentProperties.setProperty("height", height + "");
        currentProperties.setProperty("food", food + "");
        currentProperties.setProperty("obs", obs + "");

        for (Tuple<String, Integer, Byte> entry : entities) { //set properties
            currentProperties.setProperty(entry.getFirst(), entry.getSecond() + "");
        }

        if (file != null) { //if file is open
            try {
                PrintWriter writer = new PrintWriter(lastConf); //save to the last configuration file
                writer.print(""); //clear the file
                writer.close();
                output = new FileOutputStream(lastConf);
                currentProperties.store(output, "ALS config file");
                output.close();

                if (file != lastConf) { //save to the second file
                    writer = new PrintWriter(file);
                    writer.print("");
                    writer.close();

                    output = new FileOutputStream(file);
                    currentProperties.store(output, "ALS config file");
                    output.close();
                }
                this.propFile = file;
            } catch (IOException e) { //show an error if something is wrong
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Something went wrong!");
                alert.showAndWait();
            } catch (NullPointerException ignored) { //ignore null pointer exception
            }
        }
    }

    /**
     * Method for showing an image
     * @param image image to draw
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param opacity opacity, between 0 and 1
     */
    public void show(Image image, int x, int y, double opacity) {
        gc.setGlobalAlpha(opacity);
        gc.drawImage(image, x, y, imageSize, imageSize);
    }

    /**
     * Clears the screen and draws all entities of the current world
     */
    private void draw() {
        gc.setGlobalAlpha(1);
        gc.clearRect(0, 0, world.getSizeX() * imageSize, world.getSizeY() * imageSize);
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, world.getSizeX() * imageSize, world.getSizeY() * imageSize);
        world.show(this);
    }

    /**
     * Staring method, initialises everything
     * @param primaryStage used stage
     * @throws Exception possible exceptions
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        canvas = new Canvas(SCREEN_SIZE, SCREEN_SIZE); //create canvas
        stagePrimary = primaryStage;
        stagePrimary.setResizable(false);
        primaryStage.setTitle("Life form sim"); //title
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(0, 0, 0, 0));

        bp.setTop(setMenu());

        Group root = new Group();

        fromFile(currentProperties); //load from properties

        root.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();

        bp.setCenter(root);

        Slider sldSpeed = new Slider(1, 10, 3); //speed slider

        sldSpeed.setShowTickLabels(true);
        sldSpeed.setShowTickMarks(true);
        sldSpeed.setBlockIncrement(1);

        Label lblSpeed = new Label("Speed:");
        Button btnPause = new Button("Pause");
        Button btnPlay = new Button("Play");
        Button btnReset = new Button("Reset");

        btnPause.setOnAction(event -> isRunning = false);
        btnPlay.setOnAction(event -> isRunning = true);
        btnReset.setOnAction(event -> fromText(world.getCurrentConfig()));

        sldSpeed.valueProperty().addListener((observable, oldValue, newValue) -> { //update constants when speed is changed
            simulationSpeed = (long) (simulationSpeed * Math.pow(1.3, oldValue.intValue() - newValue.intValue()));
            numOfUpdates = (fps * simulationSpeed) / (1000000000) + 1;
            fadeRate = 1 / numOfUpdates;
            distance = imageSize / numOfUpdates;
            deltaT = simulationSpeed / (numOfUpdates - 1);
        });

        HBox bottomBox = new HBox();
        bottomBox.getChildren().addAll(btnPlay, btnPause, btnReset, lblSpeed, sldSpeed);

        bp.setBottom(bottomBox);
        //Set margins
        HBox.setMargin(btnPlay, new Insets(10, 10, 10, 10));
        HBox.setMargin(btnPause, new Insets(10, 10, 10, 10));
        HBox.setMargin(btnReset, new Insets(10, 10, 10, 10));
        HBox.setMargin(sldSpeed, new Insets(10, 10, 10, 10));
        HBox.setMargin(lblSpeed, new Insets(10, 10, 10, 10));

        draw(); //redraw

        AnimationTimer timer = new AnimationTimer() { //main animation loop
            long lastUpdate = 0;
            int timesExecuted = 0;

            public void handle(long currentNanoTime) {

                if (isRunning) {
                    if (currentNanoTime - lastUpdate >= deltaT * timesExecuted) { //if it is time for update
                        world.getEntities().stream().filter(e -> e instanceof LifeForm).forEach(e -> ((LifeForm) e).updatePosition(distance)); //move all life forms
                        world.getObjectsToRemove().forEach(e -> { //move all life forms which are about to be removed
                            if (e instanceof LifeForm) {
                                ((LifeForm) e).updatePosition(distance);
                            }
                            double opacity = e.getImageOpacity();
                            e.setImageOpacity(opacity >= 0.1 ? opacity - fadeRate : 0); //update opacity
                        });

                        world.getEntities().stream().filter(e -> e instanceof RenewableFood).forEach(e -> { //for all renewable food
                            double opacity = e.getImageOpacity();
                            if (!((RenewableFood) e).getCanEat()) { //start fading out
                                if (((RenewableFood) e).getTimer() + 1 >= RenewableFood.timeToGrow) {
                                    e.setImageOpacity(opacity >= 0.1 ? opacity - fadeRate : 0);

                                } else {
                                    e.setImageOpacity(opacity <= 0.9 ? opacity + (fadeRate) : 1); //fade in
                                }
                            }
                        });

                        world.setObjectsToRemove(world.getObjectsToRemove().stream().filter(e -> e.getImageOpacity() > 0).collect(Collectors.toSet())); //remove all invisible objects
                        timesExecuted++;
                    }

                    if (currentNanoTime - lastUpdate > simulationSpeed) { //end of the round

                        world.getEntities().stream().filter(e -> e instanceof LifeForm).forEach(e -> { //move entities to the precise position
                            ((LifeForm) e).setCurrentX(e.getTargetX() * imageSize);
                            ((LifeForm) e).setCurrentY(e.getTargetY() * imageSize);
                        });

                        timesExecuted = 0;
                        world.run();
                    }

                    draw();
                    if (currentNanoTime - lastUpdate > simulationSpeed) { //reset timer
                        lastUpdate = currentNanoTime;
                    }
                }
            }
        };

        timer.start();
        Scene scene = new Scene(bp, 800, 600);
        bp.prefHeightProperty().bind(scene.heightProperty());
        bp.prefWidthProperty().bind(scene.widthProperty());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
