package uk.ac.reading.syrvanser.Graphics;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.ac.reading.syrvanser.Entities.AnEntity;
import uk.ac.reading.syrvanser.Entities.LifeForm;
import uk.ac.reading.syrvanser.Logic.AWorld;
import uk.ac.reading.syrvanser.Logic.Result;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by syrvanser on 17/10/2016.
 *
 * @author syrvanser
 * @version 9
 */
public class GUIInterface extends Application {
    //private static final String CURRENT_VERSION = "0.7";
    private static final int SCREENSIZE = 512;
    public static int IMGSIZE = SCREENSIZE / 10;
    private static long UPDATERATE = 1000000000; //1 sec
    private boolean isRunning = false;
    private AWorld world = new AWorld();
    private Properties currentProperties = new Properties();
    private File propFile;
    private File lastConf;
    private Stage stagePrimary;
    private GraphicsContext gc;
    private FileChooser propFC = new FileChooser();
    private boolean useSmoothAnimations = true;
    
    public static void main(String[] args) {
		Application.launch(args);
	}
    
    private static boolean isNumeric(String str) {
        return str.matches("^-?\\d+$");  //match a number with optional '-' and decimal.
    }

    private MenuBar setMenu() {
        MenuBar menuBar = new MenuBar();

        File configDirectory = new File("./configurations");
        lastConf = new File(configDirectory, "last.properties");
        propFC.setInitialDirectory(configDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Configuration files (*.properties)", "*.properties");
        propFC.getExtensionFilters().add(extFilter);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(lastConf);
            currentProperties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            try {
                //noinspection ResultOfMethodCallIgnored
                lastConf.createNewFile();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


        Menu mFile = new Menu("File");
        Menu mView = new Menu("View");
        Menu mEdit = new Menu("Edit");
        Menu mSimulation = new Menu("Simulation");
        Menu mHelp = new Menu("Help");

        if (!configDirectory.exists() && !configDirectory.mkdir()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to create directory for config files!");
            alert.showAndWait();

        }

        MenuItem mNew = new MenuItem("New");
        mNew.setOnAction(actionEvent -> {
            propFile = new File(configDirectory, "new.properties");
            currentProperties = new Properties();
            world.clearConfig();
            world.clear();
        });

        MenuItem mOpen = new MenuItem("Open");
        mOpen.setOnAction(actionEvent -> {
            File file = propFC.showOpenDialog(stagePrimary);
            InputStream input;
            try {
                input = new FileInputStream(file);
                currentProperties.load(input);
                fromFile(currentProperties);
                input.close();
                propFile = file;
            } catch (IOException e) {
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
            result.ifPresent(conf -> {
                if (!fromText(conf)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Wrong config line");
                    alert.showAndWait();
                }
            });

        });

        MenuItem mViewLifeInfo = new MenuItem("Life info");
        mViewLifeInfo.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entities Info");
            alert.setHeaderText("Click to see entities details:");
            Label label = new Label("Entities stats:");
            TextArea textArea = new TextArea(list());
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane statsContent = new GridPane();
            statsContent.setMaxWidth(Double.MAX_VALUE);
            statsContent.add(label, 0, 0);
            statsContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(statsContent);
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
        mModify.setOnAction(actionEvent -> {


            if (world.getEntities().size() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Nothing to modify!");
                alert.setContentText("Add some entities first");
                alert.showAndWait();
            } else {
                List<LifeForm> choices = world.getEntities().stream().filter(e -> e instanceof LifeForm).map(e -> (LifeForm) e).collect(Collectors.toList());
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

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 5, 10, 5));

                    TextField name = new TextField();
                    name.setText(e.getSpecies());
                    TextField x = new TextField();
                    x.setText(e.getTargetX() + "");

                    TextField y = new TextField();
                    y.setText(e.getTargetY() + "");


                    TextField d = new TextField();
                    d.setText(e.getDetectionRadius() + "");
                    Label xError = new Label("Must be a number less between 0 and " + (world.getSizeX() - 1));
                    xError.setVisible(false);
                    xError.setStyle(" -fx-background-radius: 25px; -fx-background-color: coral; -fx-padding: 4px;");
                    Label yError = new Label("Must be a number less between 0 and " + (world.getSizeY() - 1));
                    yError.setVisible(false);
                    yError.setStyle(" -fx-background-radius: 25px; -fx-background-color: coral; -fx-padding: 4px;");
                    Label dError = new Label("Must be a number less between 0 and 100");
                    dError.setVisible(false);
                    dError.setStyle(" -fx-background-radius: 25px; -fx-background-color: coral; -fx-padding: 4px;");
                    Label eError = new Label("Must be a number less between 0 and 10000");
                    eError.setVisible(false);
                    eError.setStyle(" -fx-background-radius: 25px; -fx-background-color: coral; -fx-padding: 4px;");

                    TextField en = new TextField();
                    en.setText(e.getEnergy() + "");
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


                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == done) {
                            try {
                                return new Result(name.getText(), Integer.parseInt(x.getText()), Integer.parseInt(y.getText()), Integer.parseInt(d.getText()), Integer.parseInt(en.getText()));
                            } catch (NumberFormatException exp) {
                                return null;
                            }
                        }
                        return null;
                    });
                    final boolean[] flag = {true};
                    do {
                        Optional<Result> result = dialog.showAndWait();
                        result.ifPresent(res -> {
                            if (world.getEntity(res.x, res.y) == null) {
                                e.setSpecies(res.n);
                                e.setTargetX(res.x);
                                e.setTargetY(res.y);
                                e.setCurrentX(res.x * IMGSIZE);
                                e.setCurrentY(res.y * IMGSIZE);
                                e.setDetectionRadius(res.r);
                                e.setEnergy(res.e);
                                flag[0] = false;
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("This position is occupied!");
                                alert.setContentText("Try again!");
                                alert.showAndWait();
                                flag[0] = true;
                            }
                        });
                    } while (flag[0]);
                });
            }
        });


        MenuItem mRemove = new MenuItem("Remove");
        mRemove.setOnAction(actionEvent -> {
            if (world.getEntities().size() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Nothing to remove!");
                alert.setContentText("Add some entities first");
                alert.showAndWait();
            } else {
                List<AnEntity> choices = world.getEntities().stream().filter(e -> e instanceof LifeForm).collect(Collectors.toList());
                ChoiceDialog<AnEntity> dialog1 = new ChoiceDialog<>(choices.get(0), choices);
                dialog1.setTitle("Entity selection");
                dialog1.setHeaderText("Choose an entity");
                dialog1.setContentText("Pick from the list:");

                Optional<AnEntity> entity = dialog1.showAndWait();

                entity.ifPresent(e -> {
                    world.remove(e);
                    world.removePair(new AbstractMap.SimpleEntry<>(e.getSpecies(), 1));
                    //noinspection SuspiciousMethodCalls
                    world.getEntities().removeAll(Collections.singleton(null));

                });
            }


        });


        MenuItem mAdd = new MenuItem("Add");
        mAdd.setOnAction(actionEvent -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add a lifeform");


            dialog.setHeaderText("Please enter the new life form's name ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(enteredName -> add(enteredName, enteredName.charAt(0)));
        });

        mEdit.getItems().addAll(mModify, mRemove, mAdd);

        MenuItem mRun = new MenuItem("Run");
        mRun.setOnAction(actionEvent -> isRunning = true);
        MenuItem mPause = new MenuItem("Pause");
        mPause.setOnAction(actionEvent -> isRunning = false);
        MenuItem mRestart = new MenuItem("Restart");
        mRestart.setOnAction(actionEvent -> fromText(world.getCurrentConfig()));
        mSimulation.getItems().addAll(mRun, mPause, mRestart);


        menuBar.getMenus().addAll(mFile, mView, mEdit, mSimulation, mHelp);

        return menuBar;

    }


    private boolean add(String s, char c) {
        return world.addEntity(s, c);
    }

    private String list() {
        List<AnEntity> entities = world.getEntities();
        StringBuilder result = new StringBuilder("");
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
     *
     * @param input String to convert form
     * @return true if successful, false otherwise
     */
    private boolean fromText(String input) {

        try {
            String[] array = input.split(" ");
            int x = Integer.parseInt(array[0]);
            int y = Integer.parseInt(array[1]);
            world.clear();
            world.clearConfig();
            world = new AWorld(x, y, x * y);
            IMGSIZE = world.getSizeX() > world.getSizeY() ? (SCREENSIZE / world.getSizeX()) : (SCREENSIZE / world.getSizeY());
            int foodNum = Integer.parseInt(array[2]);
            int obsNum = Integer.parseInt(array[3]);
            int foodAmount = world.getSizeY() * world.getSizeX() * foodNum / 100;

            int obsAmount = world.getSizeY() * world.getSizeX() * obsNum / 100;

            for (int i = 0; i < foodAmount; i++) { //add food
                add("Food", 'f');

            }
            world.setMaxFood(foodAmount);
            for (int i = 0; i < obsAmount; i++) { //add obstacles
                add("Obstacle", 'o');
            }
            world.setMaxObstacles(obsAmount);
            int counter = 4; //4th argument in the string
            while (counter < array.length) { //while not finished
                String form = array[counter];
                int num = Integer.parseInt(array[counter + 1]);
                for (int i = 0; i < num; i++) {

                    add(form, form.charAt(0));
                }

                counter += 2;
            }

            save(lastConf);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private boolean fromFile(Properties prop) {
        try {
            int loadedWidth = Integer.parseInt(prop.getProperty("width"));
            int loadedHeight = Integer.parseInt(prop.getProperty("height"));
            world.clear();
            world.clearConfig();
            world = new AWorld(loadedWidth, loadedHeight, loadedHeight * loadedWidth);
            IMGSIZE = world.getSizeX() > world.getSizeY() ? (SCREENSIZE / world.getSizeX()) : (SCREENSIZE / world.getSizeY());
            int loadedObstacles = Integer.parseInt(prop.getProperty("obs"));
            int loadedFood = Integer.parseInt(prop.getProperty("food"));
            Set<String> keys = prop.stringPropertyNames();

            int foodAmount = world.getSizeY() * world.getSizeX() * loadedFood / 100;
            int obsAmount = world.getSizeY() * world.getSizeX() * loadedObstacles / 100;
            for (int i = 0; i < foodAmount; i++) { //add food
                add("Food", 'f');


            }
            world.setMaxFood(foodAmount);
            for (int i = 0; i < obsAmount; i++) { //add obstacles
                add("Obstacle", 'o');
            }
            world.setMaxObstacles(obsAmount);
            for (String form : keys) { //while not finished
                if (form.equals("food") || form.equals("obs") || form.equals("width") || form.equals("height"))
                    continue;
                int num = Integer.parseInt(prop.getProperty(form));
                for (int i = 0; i < num; i++) {
                    add(form, form.charAt(0));
                }
                //Map.Entry<String, Integer> pair = new AbstractMap.SimpleEntry<>(form, num);
                //world.addPair(pair);

            }


            save(lastConf);
            return true;


        } catch (Exception e) {
            return false;
        }


    }

    private void save() {

        File file = propFC.showSaveDialog(stagePrimary);
        save(file);
    }

    private void save(File file) {
        OutputStream output;
        int width = world.getSizeX();
        int height = world.getSizeY();
        int food = world.getMaxFood();
        int obs = world.getMaxObstacles();
        List<Map.Entry<String, Integer>> entities = world.getSpeciesList();

        currentProperties.setProperty("width", width + "");
        currentProperties.setProperty("height", height + "");
        currentProperties.setProperty("food", food + "");
        currentProperties.setProperty("obs", obs + "");

        for (Map.Entry<String, Integer> entry : entities) {
            currentProperties.setProperty(entry.getKey(), entry.getValue() + "");
        }

        if (file != null) {
            try {
                PrintWriter writer = new PrintWriter(lastConf);
                writer.print("");
                writer.close();
                output = new FileOutputStream(lastConf);
                currentProperties.store(output, "ALS config file");
                output.close();

                if (file != lastConf) {
                    writer = new PrintWriter(file);
                    writer.print("");
                    writer.close();

                    output = new FileOutputStream(file);
                    currentProperties.store(output, "ALS config file");
                    output.close();
                }
                this.propFile = file;
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Something went wrong!");
                alert.showAndWait();
            } catch (NullPointerException ignored) {

            }
        }
    }

    public void show(Image image, int x, int y, double opacity) {
        gc.setGlobalAlpha(opacity);
        gc.drawImage(image, x, y, IMGSIZE, IMGSIZE);
    }

    private void draw() {
        gc.setGlobalAlpha(1);
        gc.clearRect(0, 0, world.getSizeX() * IMGSIZE, world.getSizeY() * IMGSIZE);
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, world.getSizeX() * IMGSIZE, world.getSizeY() * IMGSIZE);
        world.show(this);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        stagePrimary = primaryStage;
        stagePrimary.setResizable(false);
        primaryStage.setTitle("Life form sim");
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(0, 0, 0, 0));

        bp.setTop(setMenu());

        Group root = new Group();

        //  System.out.println(currentProperties.getProperty("ant"));
        fromFile(currentProperties);
        System.out.println(world.getEntities());
        Canvas canvas = new Canvas(SCREENSIZE, SCREENSIZE);
        root.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();

        bp.setCenter(root);

        CheckBox cbSmoothAimations = new CheckBox("Smooth animations");
        cbSmoothAimations.setSelected(true);

        Slider sldSpeed = new Slider(1, 10, 3);

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
        sldSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            UPDATERATE = (long) (UPDATERATE * Math.pow(1.2, oldValue.intValue() - newValue.intValue()));
            System.out.println(UPDATERATE);
        });

        cbSmoothAimations.selectedProperty().addListener((observable, oldValue, newValue) -> useSmoothAnimations = newValue);

        HBox bottomBox = new HBox();
        bottomBox.getChildren().addAll(btnPlay, btnPause, btnReset, lblSpeed, sldSpeed, cbSmoothAimations);
        bp.setBottom(bottomBox);
        HBox.setMargin(btnPlay, new Insets(10, 10, 10, 10));
        HBox.setMargin(btnPause, new Insets(10, 10, 10, 10));
        HBox.setMargin(btnReset, new Insets(10, 10, 10, 10));
        HBox.setMargin(sldSpeed, new Insets(10, 10, 10, 10));
        HBox.setMargin(lblSpeed, new Insets(10, 10, 10, 10));
        HBox.setMargin(cbSmoothAimations, new Insets(10, 10, 10, 10));

        world.show(this);
        // fromText("20 20 20 20 ant 5");
        AnimationTimer timer = new AnimationTimer() {
            int i = 0;
            private long lastUpdate = 0;

            public void handle(long currentNanoTime) {
                if (isRunning) {
                    if (useSmoothAnimations && currentNanoTime - lastUpdate >= (UPDATERATE / IMGSIZE) * i) {
                        i++;
                        double fadeRate = 1.5 / (double) IMGSIZE;
                        world.getEntities().stream().filter(e -> e instanceof LifeForm).forEach(e -> ((LifeForm) e).updatePosition());
                        world.getObjectsToRemove().forEach(e -> {
                            double opacity = e.getImageOpacity();
                            e.setImageOpacity(opacity >= 0.1 ? opacity - fadeRate : 0);
                        });
                        world.setObjectsToRemove(world.getObjectsToRemove().stream().filter(e -> e.getImageOpacity() > 0).collect(Collectors.toSet()));
                        System.out.println(i + " " + (currentNanoTime - lastUpdate) + " r:" + (UPDATERATE / IMGSIZE) * (i - 1) + "-" + (UPDATERATE / IMGSIZE) * (i));
                    }
                    if (currentNanoTime - lastUpdate > UPDATERATE) {

                            world.getEntities().stream().filter(e -> e instanceof LifeForm).forEach(e -> {
                                ((LifeForm) e).setCurrentX(e.getTargetX() * IMGSIZE);
                                ((LifeForm) e).setCurrentY(e.getTargetY() * IMGSIZE);
                                if (!useSmoothAnimations)
                                    world.getObjectsToRemove().clear();
                            });

                        i = 0;
                        world.run();
                        lastUpdate = currentNanoTime;
                    }
                }
                draw();


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
