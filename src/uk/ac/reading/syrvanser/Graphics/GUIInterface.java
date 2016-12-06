package uk.ac.reading.syrvanser.Graphics;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
    private static final long UPDATERATE = 1000000000; //1 sec
    private static final String CURRENT_VERSION = "0.7";
    public static int IMGSIZE = 20;
    VBox rtPane;
    private boolean isRunning = false;
    private AWorld world = new AWorld();
    //    private boolean displayMap = true;
    private Properties currentProperties = new Properties();
    private File propFile;
    private File lastConf;
    private Stage stagePrimary;
    private Canvas canvas;
    private BorderPane bp;
    private Group root;
    private GraphicsContext gc;
    //  FileChooser imgFC = new FileChooser();
    private FileChooser propFC = new FileChooser();

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

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    TextField name = new TextField();
                    name.setText(e.getSpecies());
                    TextField x = new TextField();
                    x.setText(e.getTargetX() + "");

                    TextField y = new TextField();
                    y.setText(e.getTargetY() + "");


                    TextField d = new TextField();
                    d.setText(e.getDetectionRadius() + "");


                    TextField en = new TextField();
                    en.setText(e.getEnergy() + "");


                    grid.add(new Label("Name:"), 0, 0);
                    grid.add(name, 1, 0);
                    grid.add(new Label("x:"), 0, 1);
                    grid.add(x, 1, 1);

                    grid.add(new Label("y:"), 0, 2);
                    grid.add(y, 1, 2);

                    grid.add(new Label("Detection range:"), 0, 3);
                    grid.add(d, 1, 3);

                    grid.add(new Label("Energy:"), 0, 4);
                    grid.add(en, 1, 4);

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

                    Optional<Result> result = dialog.showAndWait();
                    result.ifPresent(res -> {
                        if (world.getEntity(res.x, res.y) == null && world.canMove(res.x, res.y) && res.r > 0 && res.e >= 0) {
                            e.setSpecies(res.n);
                            e.setTargetX(res.x);
                            e.setTargetY(res.y);
                            e.setCurrentX(res.x * IMGSIZE);
                            e.setCurrentY(res.y * IMGSIZE);
                            e.setDetectionRadius(res.r);
                            e.setEnergy(res.e);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Wrong parameters!");
                            alert.setContentText("Check parameters and try again!");
                            alert.showAndWait();
                        }
                    });

                });
            }
        });
                        /*

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Select an action");
                        alert.setHeaderText("What would you like to modify?");
                        alert.setContentText("Choose your option");

                        ButtonType buttonPos = new ButtonType("Position");
                        ButtonType buttonName = new ButtonType("Name");
                        ButtonType buttonEnergy = new ButtonType("Energy");
                        ButtonType buttonDetect = new ButtonType("Detection");
                        ButtonType buttonImg = new ButtonType("Image");

                        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(buttonName, buttonPos, buttonDetect, buttonEnergy, buttonImg, buttonCancel);

                        Optional<ButtonType> choice = alert.showAndWait();

                        choice.ifPresent(res -> {
                            try {
                                Scanner s = new Scanner(System.in);
                                if (res == buttonPos) {
                                    TextInputDialog coordsDialog = new TextInputDialog();
                                    coordsDialog.setTitle("Coordinates");
                                    coordsDialog.setHeaderText("Enter x and y:");

                                    Optional<String> coordOpt = dialog.showAndWait();
                                    coordOpt.ifPresent(coord -> {
                                        try {
                                            String[] strArray = coord.split(" ", 2);
                                            int newX = Integer.parseInt(strArray[0]);
                                            int newY = Integer.parseInt(strArray[1]);
                                            if ((newX > 0 && newY > 0 && newX < world.getSizeX() && newY < world.getSizeY()) && (world.getEntitySymbol(newX, newY) == ' ')) {
                                                lf.setTargetX(newX);
                                                lf.setTargetY(newY);
                                            } else {
                                                throw new NumberFormatException();
                                            }

                                        } catch (NumberFormatException e) {
                                            Alert alert1 = new Alert(Alert.AlertType.ERROR);
                                            alert1.setTitle("Error");
                                            alert1.setHeaderText("Wrong input!");
                                            alert1.showAndWait();
                                        }
                                    });

                                } else if (res == buttonName) {

                                    TextInputDialog nameDialog = new TextInputDialog();
                                    nameDialog.setTitle("Name");
                                    nameDialog.setHeaderText("Enter the new name:");

                                    Optional<String> nameOpt = dialog.showAndWait();
                                    nameOpt.ifPresent(newName -> {
                                        lf.setSpecies(newName);
                                        world.addPair(new AbstractMap.SimpleEntry<>(newName, 1));
                                        world.removePair(new AbstractMap.SimpleEntry<>(newName, 1));
                                    });
                                } else if (res == buttonEnergy) {
                                    TextInputDialog nameDialog = new TextInputDialog();
                                    nameDialog.setTitle("Energy");
                                    nameDialog.setHeaderText("Enter the new energy");

                                    Optional<String> nameOpt = dialog.showAndWait();
                                    nameOpt.ifPresent(newName -> {
                                        lf.setSpecies(newName);
                                        world.addPair(new AbstractMap.SimpleEntry<>(newName, 1));
                                        world.removePair(new AbstractMap.SimpleEntry<>(newName, 1));
                                    });
                                    lf.setEnergy(newEnergy);
                                } else if (res == buttonDetect) {
                                    int newRadius = s.nextInt();

                                    if (newRadius < 0)
                                        System.out.println("Can't have negative detection radius!");
                                    else
                                        lf.setDetectionRadius(newRadius);

                                }


                            } catch (NumberFormatException e) {
                                System.out.println("Wrong number!");
                            }
                        }); */


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


  /*  private boolean add() {
        Scanner s = new Scanner(System.in);


        System.out.println("Enter species name:");
        String name = s.next();
        System.out.println("Enter species symbol:");
        char symbol = s.next().charAt(0);
        return world.addEntity(name, symbol);
    }
*/

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


  /*
    private void display() {
        System.out.print("/");
        for (int i = 0; i < world.getSizeX(); i++)
            System.out.print("-");
        System.out.println("\\");
        for (int i = 0; i < world.getSizeY(); i++) {
            System.out.print("|");
            for (int j = 0; j < world.getSizeX(); j++) {
                AnEntity e = world.getEntity(j, i);
                if (e == null)
                    System.out.print(" ");
                else if (e.getSymbol() == 'f')
                    System.out.print(e.getEnergy());
                else
                    System.out.print(e.getSymbol());
            }
            System.out.println("|");
        }
        System.out.print("\\");
        for (int i = 0; i < world.getSizeX(); i++)
            System.out.print("-");
        System.out.println("/");
    }
*/

    /**
     * Accepts a string and uses it to initialise the world
     *
     * @param input String to convert form
     * @return true if successful, false otherwise
     */
    private boolean fromText(String input) {
        world.clear();
        world.clearConfig();
        try {
            String[] array = input.split(" ");
            int x = Integer.parseInt(array[0]);
            int y = Integer.parseInt(array[1]);
            world = new AWorld(x, y, x * y);
            canvas.setHeight(y * IMGSIZE);
            canvas.setWidth(x * IMGSIZE);
            gc = canvas.getGraphicsContext2D();
            int foodNum = Integer.parseInt(array[2]);
            int obsNum = Integer.parseInt(array[3]);
            int foodAmount = world.getSizeY() * world.getSizeX() * foodNum / 100;

            int obsAmount = world.getSizeY() * world.getSizeX() * obsNum / 100;

            for (int i = 0; i < foodAmount; i++) { //add food
                System.out.println(world.maxEntities + "ffff");
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
                //Map.Entry<String, Integer> pair = new AbstractMap.SimpleEntry<>(form, num);
                //world.addPair(pair);
                counter += 2;
            }
            canvas = new Canvas(IMGSIZE * world.getSizeX(), IMGSIZE * world.getSizeX());
            root.getChildren().add(canvas);

            gc = canvas.getGraphicsContext2D();

            bp.setCenter(root);
            save(lastConf);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }




/*


    private void run(int num) {
        if (displayMap) {
            for (int i = 0; i < num; i++) {
                //System.out.flush();
                System.out.println();
                System.out.print("------Epoch #" + (i + 1) + "------");
                if (i <= 9)
                    System.out.print("-");
                System.out.println();
                world.run();
                //System.out.println(Arrays.toString(world.entities));
                display();
                //System.out.println(Arrays.toString(world.entities));
                System.out.println("---------------------");
            }
        } else {
            for (int i = 0; i < num; i++)
                world.run();
        }
    }
*/

    private boolean fromFile(Properties prop) {
        try {
            int loadedWidth = Integer.parseInt(prop.getProperty("width"));
            int loadedHeight = Integer.parseInt(prop.getProperty("height"));
            world.clear();
            world.clearConfig();
            world = new AWorld(loadedWidth, loadedHeight, loadedHeight * loadedWidth);
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

            canvas = new Canvas(IMGSIZE * world.getSizeX(), IMGSIZE * world.getSizeX());
            root.getChildren().add(canvas);

            gc = canvas.getGraphicsContext2D();

            bp.setCenter(root);
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

    public void show(Image image, int x, int y) {
        gc.drawImage(image, x, y, IMGSIZE, IMGSIZE);
    }

    private void draw() {

        gc.clearRect(0, 0, 512, 512);
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, 512, 512);
        world.show(this);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        stagePrimary = primaryStage;
        stagePrimary.setResizable(false);
        primaryStage.setTitle("Life form sim");
        bp = new BorderPane();
        bp.setPadding(new Insets(0, 0, 0, 0));

        bp.setTop(setMenu());

        root = new Group();

        System.out.println(currentProperties.getProperty("ant"));
        fromFile(currentProperties);
        System.out.println(world.getEntities());

        canvas = new Canvas(IMGSIZE * world.getSizeX(), IMGSIZE * world.getSizeX());
        root.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();

        bp.setCenter(root);

        world.show(this);
        // fromText("20 20 20 20 ant 5");
        AnimationTimer timer = new AnimationTimer() {
            int i = 1;
            private long lastUpdate = 0;

            public void handle(long currentNanoTime) {
                if (isRunning) {

                    if (currentNanoTime - lastUpdate >= (UPDATERATE / IMGSIZE) * i) {
                        i++;
                        // System.out.println("YEAH");
                        world.getEntities().stream().filter(e -> e instanceof LifeForm).forEach(e -> ((LifeForm) e).updatePosition());

                    }
                    if (currentNanoTime - lastUpdate >= UPDATERATE) {
                        i = 0;
                        world.run();
                        lastUpdate = currentNanoTime;
                    }
                }
                draw();


            }
        };

        timer.start();
        //  rtPane = new VBox();
        //  rtPane.setAlignment(Pos.CENTER);
        // rtPane.setPadding(new Insets(25, 25, 25, 25));

        //bp.setRight(rtPane);

        Scene scene = new Scene(bp, 800, 600);
        bp.prefHeightProperty().bind(scene.heightProperty());
        bp.prefWidthProperty().bind(scene.widthProperty());

        primaryStage.setScene(scene);
        primaryStage.show();

        /*Scanner s = new Scanner(System.in);
        char ch;
        File dir = new File("./configurations");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("Config directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }

        boolean exitFlag;
        do {
            System.out.println();
            System.out.print("Enter (F)ile, (V)iew, (E)dit, (S)imulation or (H)elp > ");
            ch = s.next().charAt(0);
            s.nextLine();
            switch (ch) {


                case 'v':
                case 'V':
                    exitFlag = false;
                    do {
                        System.out.println("(1)	Display configuration");
                        System.out.println("(2)	Edit configuration");
                        System.out.println("(3)	Display info about life forms");
                        System.out.println("(4)	Display info about map ");
                        ch = s.next().charAt(0);
                        s.nextLine();
                        switch (ch) {
                            case '1': //display config
                                System.out.println("Current config:");
                                System.out.println(world.getCurrentConfig());
                                exitFlag = true;
                                break;
                            case '2': //edit config
                                System.out.println("Enter a string:");
                                String inputString = s.nextLine();
                                if (fromText(inputString)) System.out.println("Done");
                                else
                                    System.out.println("Something went wrong!");
                                exitFlag = true;
                                break;
                            case '3': //show life forms info
                                list();
                                exitFlag = true;
                                break;
                            case '4': //show map info
                                System.out.println(world.stats());
                                display();
                                exitFlag = true;
                                break;
                            default:

                        }
                    } while (!exitFlag);
                    break;

                case 'e':
                case 'E':
                    exitFlag = false;
                    do {
                        System.out.println("(1)	Modify current life form parameters");
                        System.out.println("(2)	Remove current life form");
                        System.out.println("(3)	Add a new life form");
                        ch = s.next().charAt(0);
                        s.nextLine();
                        switch (ch) {
                            case '1': //Modify
                                System.out.println("Enter ID of the life form you would like to modify: ");
                                try {
                                    int id = s.nextInt();
                                    LifeForm lf = world.findById(id);
                                    if (lf == null)
                                        System.out.println("Not Found!");
                                    else {
                                        System.out.println("Wold you like to change (P)osition, (N)ame, (E)nergy or (D)etection radius?");
                                        ch = s.next().charAt(0);
                                        s.nextLine();
                                        try {
                                            switch (ch) {
                                                case 'p':
                                                case 'P':
                                                    System.out.println("Enter new x and y");

                                                    int newX = s.nextInt();
                                                    int newY = s.nextInt();
                                                    if ((newX > 0 && newY > 0 && newX < world.getSizeX() && newY < world.getSizeY()) && (world.getEntitySymbol(newX, newY) == ' ')) {
                                                        lf.setTargetX(newX);
                                                        lf.setTargetY(newY);
                                                    }
                                                    break;

                                                case 'n':
                                                case 'N':
                                                    System.out.println("Enter the new name:");
                                                    String newName = s.nextLine();
                                                    System.out.println("Enter the new symbol:");
                                                    char newSymbol = s.next().charAt(0);
                                                    s.nextLine();
                                                    lf.setSpecies(newName);
                                                    lf.setSymbol(newSymbol);
                                                    world.addPair(new AbstractMap.SimpleEntry<>(newName, 1));
                                                    world.removePair(new AbstractMap.SimpleEntry<>(newName, 1));
                                                    break;

                                                case 'e':
                                                case 'E':
                                                    int newEnergy = s.nextInt();
                                                    if (newEnergy < 0)
                                                        System.out.println("Can't have negative energy!");
                                                    else
                                                        lf.setEnergy(newEnergy);
                                                    break;

                                                case 'd':
                                                case 'D':
                                                    int newRadius = s.nextInt();
                                                    if (newRadius < 0)
                                                        System.out.println("Can't have negative detection radius!");
                                                    else
                                                        lf.setDetectionRadius(newRadius);
                                                    break;


                                                default:
                                                    System.out.println("Wrong input");
                                                    break;

                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("Wrong number!");
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid id!");
                                }
                                exitFlag = true;
                                break;
                            case '2': //Remove

                                exitFlag = true;
                                break;
                            case '3': //Add a new life form
                                if (add()) {
                                    System.out.println("Done!");
                                } else {
                                    System.out.println("Can't add an entity!");
                                }
                                exitFlag = true;
                                break;

                            default:

                        }
                    } while (!exitFlag);

                    break;

                case 's':
                case 'S':
                    exitFlag = false;
                    do {
                        System.out.println("1.	Run");
                        System.out.println("2.	Pause/restart");
                        System.out.println("3.	Reset");
                        System.out.println("4.	Display map at each iteration: ON/OFF");
                        ch = s.next().charAt(0);
                        s.nextLine();
                        switch (ch) {
                            case '1': //Run
                                System.out.println("How many times?");
                                try {
                                    int num = s.nextInt();

                                    run(num);
                                } catch (NumberFormatException e) {
                                    System.out.println("Not a number!");
                                }

                                exitFlag = true;
                                break;
                            case '2': //Pause/restart TODO
                                System.out.println("This option doesn't do anything... yet.");
                                exitFlag = true;
                                break;
                            case '3': //Reset
                                fromText(world.getCurrentConfig());
                                exitFlag = true;
                                break;
                            case '4': //display map
                                displayMap = !displayMap;
                                System.out.println("Set map display to " + (displayMap ? "on" : "off"));
                                exitFlag = true;
                                break;
                            default:

                        }
                    } while (!exitFlag);
                    break;

                case 'h':
                case 'H':
                    exitFlag = false;
                    do {
                        System.out.println("1.  Display info about application");
                        System.out.println("2.  Display info about author");
                        ch = s.next().charAt(0);
                        s.nextLine();
                        switch (ch) {
                            case '1': //App TODO
                                System.out.println("Artificial life simulator, ver  " + CURRENT_VERSION);

                                exitFlag = true;
                                break;
                            case '2': //Author TODO
                                System.out.println("Created by Ivan Syrovoiskii\n");
                                System.out.println("Roses are red");
                                System.out.println("Violets are blue");
                                System.out.println("Still doing this project");
                                System.out.println("Guess when it is due");

                                exitFlag = true;
                                break;
                            default:

                        }
                    } while (!exitFlag);
                    break;


                default:

            }
        } while (ch != 'X');

        s.close();
    }

    public static void main(String[] args) {
        Application.launch(args);
       /* do {
            System.out.println();
            System.out.print("Enter (A)dd entity, (D)isplay, (L)ist, (C)onfigure, (R)un or e(X)it > ");
            ch = s.next().charAt(0);
            s.nextLine();
            switch (ch) {
                case 'A':
                case 'a':
                    if(inter.add()) {
                        System.out.println("Done!");
                    } else {
                        System.out.println("Can't add an entity!");
                    }
                    break;
                case 'D':
                case 'd':
                    inter.display();
                    break;
                case 'L':
                case 'l':
                    System.out.println(inter.world.stats());
                    inter.list();
                    break;
                case 'C':
                case 'c':
                    System.out.println("Enter a string:");
                    String input = s.nextLine();
                    if (inter.fromText(input)) {
                        System.out.println("Done");
                    } else {
                        System.out.println("Something went wrong!");
                    }
                    break;
                case 'R':
                case 'r':
                    System.out.println("How many times?");
                    try {
                        int num = s.nextInt();

                        inter.run(num);
                    } catch (NumberFormatException e){
                        System.out.println("Not a number!");
                    }
                        break;
                case 'X':
                case 'x':
                    ch = 'X';
                    break;
                default:
                    continue;
            }
        } while (ch != 'X');
        */

    }


}
