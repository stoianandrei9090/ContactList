package ro.jademy.contactlist.fxcontactlist;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import javafx.stage.WindowEvent;
import ro.jademy.contactlist.model.User;
import ro.jademy.contactlist.service.*;


public class Main extends Application {

    private GridPane root;
    private static Stage primaryStage;
    private ScrollPane scrollPane;
    private Hyperlink[] scrollPaneOptions;
    private Scene mainScene;
    private Polygon triangleUp;
    private TextField[] textFields = new TextField[4];
    private static UserService userService = new DBUserService();
    private static List<User> userList;
    private int currUserId;
    private static User currUser;
    private Pane leftPane;
    private Pane rigtPane;
    private GridPane rightPaneGridPane;
    private TextField ageField;
    private CheckBox checkBox;
    private Label bottomRightFakeUpdateLabel;
    private ProgressBar bottomRightProgressBar;
    private StackPane progressBarGroup;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void configureRoot() {
        root = new GridPane();
        ColumnConstraints cCons1 = new ColumnConstraints();
        ColumnConstraints cCons2 = new ColumnConstraints();
        cCons1.setPercentWidth(25.0);
        cCons2.setPercentWidth(75.0);
        RowConstraints rCons1 = new RowConstraints();
        RowConstraints rCons2 = new RowConstraints();
        RowConstraints rCons3 = new RowConstraints();
        rCons1.setPercentHeight(20.0);
        rCons2.setPercentHeight(72.0);
        rCons3.setPercentHeight(8.0);
        root.getColumnConstraints().addAll(cCons1, cCons2);
        root.getRowConstraints().addAll(rCons1, rCons2, rCons3);
    }

    @Override
    public void start(Stage stage) throws Exception {

        configureRoot();
        bottomRightFakeUpdateLabel = new Label();
        bottomRightProgressBar = new ProgressBar();
        bottomRightFakeUpdateLabel.setText(null);
        progressBarGroup = new StackPane();
        primaryStage = stage;
        leftPane = createLeftPane();
        rigtPane = createRightPane();
        rigtPane.setStyle("-fx-background-color : white;");

        HBox upperBox = createHbox();
        Pane upperLeftCorner = new Pane();
        StackPane bottomRightPane = createBottomRightPane();
        bottomRightFakeUpdateLabel.setVisible(true);
        progressBarGroup.setVisible(false);

        upperLeftCorner.getStyleClass().add("upper-left-corner");


        GridPane.setConstraints(bottomRightPane, 1, 2, 1, 1);
        GridPane.setConstraints(leftPane, 0, 1, 1, 2);
        GridPane.setConstraints(rigtPane, 1, 1, 1, 1);
        GridPane.setConstraints(upperBox, 1, 0, 1, 1);
        GridPane.setConstraints(upperLeftCorner, 0, 0, 1, 1);


        root.getChildren().addAll(leftPane, upperBox, rigtPane, upperLeftCorner, bottomRightPane);
        root.setStyle("-fx-background-color: white;");

        mainScene = new Scene(root, 850, 550);
        mainScene.getStylesheets().add("/stylesheet.css");
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(875);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        //     userList.addAll(userService.getContacts());
        userList = new ArrayList<>();
        userList.addAll(userService.getContacts());
        fillScrollPane(userList);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        ScheduledExecutorService fakeUpdateService = Executors.newScheduledThreadPool(1);
        fakeUpdateService.scheduleWithFixedDelay(fakeUpdateRunnable, 5 , 1000, TimeUnit.SECONDS);


    }

    public StackPane createBottomRightPane() {
        StackPane bottomRightPane = new StackPane();
        bottomRightPane.setMaxWidth(350);
        bottomRightPane.getChildren().add(bottomRightFakeUpdateLabel);
        bottomRightPane.setPadding(new Insets(0,0,0,0));
        bottomRightPane.getStyleClass().add("bottom-right-pane");
        GridPane.setHalignment(bottomRightPane, HPos.RIGHT);
        bottomRightFakeUpdateLabel.setPadding(new Insets(15,15,15,0));
        StackPane.setAlignment(bottomRightFakeUpdateLabel, Pos.BOTTOM_RIGHT);
        bottomRightProgressBar.setPadding(new Insets(15,15,15,0));
        StackPane.setAlignment(bottomRightProgressBar, Pos.BOTTOM_RIGHT);
        progressBarGroup.getChildren().add(bottomRightProgressBar);
        Label progressBarLeftText = new Label("Updating : ");
        progressBarLeftText.setPadding(new Insets(15));
        StackPane.setAlignment(progressBarGroup, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(progressBarLeftText, Pos.BOTTOM_LEFT);
        progressBarLeftText.setTranslateY(-2);
        progressBarGroup.setMaxWidth(200);
        progressBarGroup.getChildren().add(progressBarLeftText);
        bottomRightPane.getChildren().addAll(progressBarGroup);
        progressBarGroup.setTranslateX(-27);
        bottomRightFakeUpdateLabel.setTranslateX(-27);
        return bottomRightPane;
    }


    public HBox createHbox() {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color : white;");
//        hBox.setPrefHeight(200);
        Hyperlink[] options = new Hyperlink[]{
                new Hyperlink("Create"),
                new Hyperlink("Remove"),
                new Hyperlink("Backup"),
                new Hyperlink("CheckFile"),
                new Hyperlink("Exit")
        };
        options[0].setOnAction(createClickedHandler);
        options[2].setOnAction(saveFileHandler);
        options[3].setOnAction(checkFileHandler);
        options[4].setOnAction(exitClickedHandler);
        options[1].setOnAction(removeHyperlinkClicked);
        hBox.setPadding(new Insets(15, 0, 20, 15));
        //Group optionsGroup = new Group();
        for (int i = 0; i < options.length; i++) {
            options[i].setUnderline(false);
            options[i].setFont(Font.font("Arial", FontWeight.BOLD, 22));

            if (i >= 1) HBox.setMargin(options[i], new Insets(0, 0, 0, 30));
            else HBox.setMargin(options[i], Insets.EMPTY);
            options[i].getStyleClass().add("upper-pane-options");
            options[i].setFocusTraversable(false);
            options[i].setId("upper_option_" + i);
            hBox.getChildren().add(options[i]);
        }

        hBox.setAlignment(Pos.CENTER);
        return hBox;

    }

    public GridPane createLeftPane() {

        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        ColumnConstraints columnConstraints3 = new ColumnConstraints();
        columnConstraints1.setPercentWidth(20);
        columnConstraints2.setPercentWidth(60);
        columnConstraints3.setPercentWidth(20);
        RowConstraints rowConstraints1 = new RowConstraints();
        RowConstraints rowConstraints2 = new RowConstraints();
        RowConstraints rowConstraints3 = new RowConstraints();
        rowConstraints1.setPercentHeight(13);
        rowConstraints2.setPercentHeight(74);
        rowConstraints3.setPercentHeight(13);


        GridPane leftPane = new GridPane();
        leftPane.getColumnConstraints().addAll(columnConstraints1, columnConstraints2, columnConstraints3);
        leftPane.getRowConstraints().addAll(rowConstraints1, rowConstraints2, rowConstraints3);
        leftPane.setPrefWidth(850.0 / 4.0);
        leftPane.setStyle("-fx-background-color : white");
        leftPane.setPadding(new Insets(0, 0, 0, 0));
        triangleUp = new Polygon();
        triangleUp.getPoints().addAll(0.0, 50.0,
                60.0, 50.0,
                30.0, 20.0);

        Polygon triangleDown = new Polygon();
        triangleDown.getPoints().addAll(0.0, 0.0,
                60.0, 0.0,
                30.0, 30.0);

        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("left-scroll-pane");
        GridPane.setConstraints(triangleUp, 1, 0, 1, 1);
        GridPane.setHalignment(triangleUp, HPos.CENTER);
        triangleUp.getStyleClass().add("left-pane-arrows");
        GridPane.setConstraints(scrollPane, 0, 1, 3, 1);
        GridPane.setHalignment(scrollPane, HPos.CENTER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        GridPane.setConstraints(triangleDown, 1, 2, 1, 1);
        GridPane.setHalignment(triangleDown, HPos.CENTER);
        triangleDown.getStyleClass().add("left-pane-arrows");
        //scrollPane.setFitToWidth(true);
        //scrollPane.setFitToHeight(true);
        leftPane.getChildren().addAll(triangleDown, scrollPane, triangleUp);
        triangleUp.setOnMousePressed(arrowUpPressed);
        triangleUp.setOnMouseReleased(arrowUpReleased);
        triangleDown.setOnMousePressed(arrowDownPressed);
        triangleDown.setOnMouseReleased(arrowDownReleased);

        return leftPane;

    }

    public Pane createRightPane() {
        Pane rightPane = new Pane();
        String[] stringsOnLabels = {"First name", "Second Name",
                "Email", "Job Title", "Age", "Phone Numbers", "Company", "Address", "Favorite"};
        rightPane.setStyle("-fx-background-color : white;");
        rightPaneGridPane = new GridPane();
        rightPaneGridPane.setPadding(new Insets(45, 70, 70, 70));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30.0);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70.0);
        rightPaneGridPane.getColumnConstraints().addAll(col1, col2);

        for (int i = 0; i < stringsOnLabels.length; i++) {

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / stringsOnLabels.length);
            rightPaneGridPane.getRowConstraints().add(row);

            Label textLabel = new Label(stringsOnLabels[i] + "  : ");
            textLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            GridPane.setConstraints(textLabel, 0, i, 1, 1);
            GridPane.setHalignment(textLabel, HPos.RIGHT);
            GridPane.setValignment(textLabel, VPos.CENTER);
            textLabel.setPadding(new Insets(7, 22, 7, 0));
            textLabel.getStyleClass().add("right-pane-text-label");

            if (i <= 3) {
                textFields[i] = new TextField();
                GridPane.setConstraints(textFields[i], 1, i, 1, 1);
                textFields[i].setMaxWidth(270);
                textFields[i].setId("rptxt" + i);
                textFields[i].textProperty().addListener(new RightPaneChangeListener(textFields[i]));

                rightPaneGridPane.getChildren().add(textFields[i]);

            } else {
                if (i == 4) {
                    ageField = new TextField();
                    GridPane.setConstraints(ageField, 1, i, 1, 1);
                    ageField.setMaxWidth(40);
                    ageField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (newValue.startsWith("0")) ageField.setText("");
                            if (newValue.length() > 2) newValue = newValue.substring(0, 2);


                            if (!newValue.matches("\\d*")) {
                                ageField.setText(newValue.replaceAll("[^\\d]", ""));
                                try {
                                    currUser.setAge(Integer.valueOf(ageField.getText()));
                                } catch (Exception e) {
                                    currUser.setAge(0);
                                }
                                ;
                                updateContactList();
                            } else {
                                ageField.setText(newValue);
                                try {
                                    currUser.setAge(Integer.valueOf(ageField.getText()));
                                } catch (Exception e) {
                                    currUser.setAge(0);
                                }
                                ;

                                updateContactList();
                            }
                        }
                    });
                    ;
                    rightPaneGridPane.getChildren().add(ageField);
                    //choiceBox.setMaxWidth();
                }

                if (i >= 5 && i < 8) {
                    Hyperlink hypEdit = new Hyperlink("Edit");
                    GridPane.setConstraints(hypEdit, 1, i, 1, 1);
                    GridPane.setValignment(hypEdit, VPos.CENTER);
                    hypEdit.setTranslateY(1);
                    switch (i) {
                        case 5:
                            hypEdit.setOnAction((event) -> PhoneNumbersDialog.createDialog(currUser));
                            break;
                        case 6:
                            hypEdit.setOnAction(event -> EditCompany.createWindow(currUser));
                            break;
                        case 7:
                            hypEdit.setOnAction(event -> EditUserAddress.createWindow(currUser));
                            break;

                    }
                    hypEdit.getStyleClass().add("right-pane-hyperlinks");
                    rightPaneGridPane.getChildren().add(hypEdit);
                }

                if (i == 8) {
                    checkBox = new CheckBox();
                    rightPaneGridPane.add(checkBox, 1, 8, 1, 1);
                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            currUser.setFavorite(newValue);
                            updateContactList();
                        }
                    });
                }

            }


            rightPaneGridPane.getChildren().add(textLabel);
        }

        rightPane.getChildren().add(rightPaneGridPane);
        rightPaneGridPane.setVisible(false);
        return rightPane;

    }


    private void fillScrollPane(List<User> userList) {
        VBox scrollPaneVBox = new VBox();
        scrollPaneVBox.setPadding(Insets.EMPTY);
        scrollPaneVBox.setAlignment(Pos.CENTER);
        scrollPaneOptions = new Hyperlink[userList.size()];
        Collections.sort(userList);
        for (int i = 0; i < userList.size(); i++) {
            scrollPaneOptions[i] = new Hyperlink(userList.get(i).getLastName()
                    + ", " + userList.get(i).getFirstName());
            scrollPaneOptions[i].setUnderline(false);
            scrollPaneOptions[i].setFont(Font.font("Arial", FontWeight.BOLD, 12));
            VBox.setMargin(scrollPaneOptions[i], Insets.EMPTY);
            scrollPaneOptions[i].setFocusTraversable(false);
            scrollPaneOptions[i].setId("scrollpaneoption_" + i + "_" + userList.get(i).getUserId());
            scrollPaneOptions[i].setOnAction(scrollPaneOptionCLicked);
            scrollPaneOptions[i].minWidthProperty().bind(Bindings.createDoubleBinding(() ->
                    scrollPane.getViewportBounds().getWidth(), scrollPane.viewOrderProperty()));
            scrollPaneOptions[i].setAlignment(Pos.CENTER);
            //scrollPaneOptions[i].setTranslateX(2);
            scrollPaneOptions[i].translateXProperty().bind(Bindings.createDoubleBinding(() -> triangleUp.getLayoutX()
                    - 77, triangleUp.layoutXProperty()));

        }
        scrollPaneVBox.getChildren().addAll(scrollPaneOptions);
        scrollPane.setContent(scrollPaneVBox);


    }

    private int getIndexOf(int userId) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId() == userId)
                return i;
        }
        return -1;
    }

    private EventHandler<ActionEvent> scrollPaneOptionCLicked = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (!rightPaneGridPane.isVisible()) rightPaneGridPane.setVisible(true);
            currUserId = Integer.valueOf(((Hyperlink) event.getSource()).getId().split("_")[2]);
            currUser = userService.getContact(currUserId);

            textFields[0].setText(currUser.getFirstName());
            textFields[1].setText(currUser.getLastName());
            textFields[2].setText(currUser.getEmail());
            textFields[3].setText(currUser.getJobTitle());
            ageField.setText(String.valueOf(currUser.getAge()));
            checkBox.setSelected(currUser.isFavorite());

        }
    };

    private EventHandler<ActionEvent> checkFileHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Platform.runLater(() -> {
                String s;
                try {
                    boolean b = checkFile();
                    if (b) s = "File was modified externally.";
                    else s = "File was not modified.";
                } catch (Exception e) {
                    s = e.getMessage();
                }
                Alert alertMessage = new Alert(Alert.AlertType.INFORMATION);
                alertMessage.setTitle("Checked file.");
                alertMessage.setHeaderText(null);
                alertMessage.setGraphic(null);
                alertMessage.setContentText(s);
                alertMessage.showAndWait();

                //
            });

        }
    };


        private EventHandler<ActionEvent> exitClickedHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                primaryStage.close();
                if(userService instanceof  DBUserService) {((DBUserService)userService).closeConnection();};
                Platform.exit();
                System.exit(0);
            }
        };

        private EventHandler<ActionEvent> saveFileHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save file as...");
                FileChooser.ExtensionFilter filterJSON = new FileChooser.ExtensionFilter (
                        "JSON files", "*.json", "*.JSON", "*.Json");
                FileChooser.ExtensionFilter filterConfig = new FileChooser.ExtensionFilter(
                        "Config file", "*.config", "*.CONFIG", "*.Config"
                );
                FileChooser.ExtensionFilter filterCsv = new FileChooser.ExtensionFilter(
                        "CSV file", "*.csv", "*.CSV", "*.Csv"
                );
                fileChooser.getExtensionFilters().addAll(filterJSON, filterConfig, filterCsv);
                String path = null;
                try {
                    path = fileChooser.showSaveDialog(primaryStage).getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(path);
                if(path!= null) {

                    String extension = path.split("\\.")[1];

                    if (extension.equals("json")) {
                        new FileUserService().writeToJson(userService.getContacts(), path);
                    } else {
                        new FileUserService().writeToFile(userService.getContacts(), path);
                    }

                }

            }
        };

        private EventHandler<ActionEvent> removeHyperlinkClicked = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (currUser == null) {
                    Alert alertMessage = new Alert(Alert.AlertType.ERROR);
                    alertMessage.setTitle("Error");
                    alertMessage.setHeaderText(null);
                    alertMessage.setGraphic(null);
                    alertMessage.setContentText("No users to delete!");
                    alertMessage.showAndWait();
                } else {

                    currUser = null;
                    rightPaneGridPane.setVisible(false);
                    userService.removeContact(currUserId);
                    userList.clear();
                    userList.addAll(userService.getContacts());
                    fillScrollPane(userService.getContacts());

                }


            }
        };

        final double scrollSpeed = 1.5;

        AnimationTimer timer = new AnimationTimer() {

            private long lastUpdate = 0;

            @Override
            public void handle(long time) {
                if (lastUpdate > 0) {
                    long elapsedNanos = time - lastUpdate;
                    double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
                    double delta = 0;
                    if (arrowUpisPressed) {
                        delta = -scrollSpeed * elapsedSeconds;
                    }
                    if (arrowDownisPressed) {
                        delta = scrollSpeed * elapsedSeconds;
                    }
                    double newValue =
                            clamp(scrollPane.getVvalue() + delta, scrollPane.getVmin(), scrollPane.getVmax());
                    scrollPane.setVvalue(newValue);
                }
                lastUpdate = time;
            }
        };

        private double clamp(double value, double min, double max) {
            return Math.min(max, Math.max(min, value));
        }

        boolean arrowUpisPressed = false, arrowDownisPressed = false;

        private EventHandler<MouseEvent> arrowUpPressed = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                ((Polygon) event.getSource()).setStyle("-fx-fill:pink;");
                arrowUpisPressed = true;
                if(!timerIsStarted) {
                    timer.start();
                    timerIsStarted = true;
                }
            }
        };

        private EventHandler<MouseEvent> arrowUpReleased = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ((Polygon) event.getSource()).setStyle("-fx-fill:#A52A2A;");
                arrowUpisPressed = false;

            }
        };

        private boolean timerIsStarted = false;

        private EventHandler<MouseEvent> arrowDownPressed = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                ((Polygon) event.getSource()).setStyle("-fx-fill:pink;");

                arrowDownisPressed = true;
                if (!timerIsStarted) {
                    timer.start();
                    timerIsStarted = true;
                }

            }
        };

        private EventHandler<MouseEvent> arrowDownReleased = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                ((Polygon) event.getSource()).setStyle("-fx-fill:#A52A2A;");
                arrowDownisPressed = false;
            }
        };

        class RightPaneChangeListener implements ChangeListener<String> {

            TextField textField;

            public RightPaneChangeListener(TextField textField) {
                this.textField = textField;
            }

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int tfint = -1;
                if (textField.getId().startsWith("rptxt"))
                    tfint = Integer.valueOf(textField.getId().replaceAll("rptxt", ""));
                switch (tfint) {
                    case 0:
                        currUser.setFirstName(newValue);
                        break;
                    case 1:
                        currUser.setLastName(newValue);
                        break;
                    case 2:
                        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                                "[a-zA-Z0-9_+&*-]+)*@" +
                                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                                "A-Z]{2,7}$";

                        if(newValue.matches(emailRegex)) currUser.setEmail(newValue);
                        else { textField.setText(oldValue);
                        Alert emailAlert = new Alert(Alert.AlertType.ERROR);
                        emailAlert.initModality(Modality.WINDOW_MODAL);
                        emailAlert.initOwner(getPrimaryStage());
                        emailAlert.setHeaderText(null);
                        emailAlert.setGraphic(null);
                        emailAlert.setTitle("Error");
                        emailAlert.setContentText("E-mail string not valid! Please re-enter!");
                        emailAlert.showAndWait();

                        }
                        break;
                    case 3:
                        currUser.setJobTitle(newValue);
                        break;
                }
            /*Updating contact list.
            userService.editContact(currUser);
            userList.clear();
            userList.addAll(userService.getContacts());
            */
                updateContactList();
                fillScrollPane(userList);
                if (getIndexOf(currUserId) == 0) scrollPane.setVvalue(0.0);
                else {
                    double v = ((double) getIndexOf(currUserId) + 1) / (userList.size());
                    scrollPane.setVvalue(v);
                }
            }
        }

        private EventHandler<ActionEvent> createClickedHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                User u = User.getInstance();
                int maxID = userService.getContacts().stream().map(user -> user.getUserId()).
                        max(Comparator.naturalOrder()).get();
                u.setUserId(maxID + 1);
                currUser = u;
                currUserId = u.getUserId();
                userService.addContact(u);
                userList.clear();
                userList.addAll(userService.getContacts());
                fillScrollPane(userList);
                rightPaneGridPane.setVisible(true);
                textFields[0].setText(currUser.getFirstName());
                textFields[1].setText(currUser.getLastName());
                textFields[2].setText(currUser.getEmail());
                ageField.setText(String.valueOf(currUser.getAge()));
                if (getIndexOf(currUserId) == 0) scrollPane.setVvalue(0.0);
                else {
                    double v = ((double) getIndexOf(currUserId) + 1) / (userList.size());
                    scrollPane.setVvalue(v);
                }
            }


        };

        public static boolean checkFile() throws Exception {
            if (userService instanceof FileUserService) {
                return ((FileUserService) userService).checkFile();
            } else throw new Exception("User service is not of type FileUserService.");
        }

        public static void updateContactList() {
            userService.editContact(currUser);
            userList.clear();
            userList.addAll(userService.getContacts());
        }

    Runnable fakeUpdateRunnable = new Runnable() {
        @Override
        public void run() {

            long time = 4000;
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    for (int i = 0; i < time; i++) {
                        updateProgress(i, time);
                        Thread.sleep(1);

                    }
                    return null;
                }
            };
            Thread th = new Thread(task);
            th.start();

            Platform.runLater(()-> {
              //  bottomRightProgressBar.setProgress(0.45);
                bottomRightProgressBar.progressProperty().bind(task.progressProperty());
                bottomRightFakeUpdateLabel.setVisible(false);
                progressBarGroup.setVisible(true);

            });

            /*
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            while(th.isAlive()) {

            }

            Platform.runLater(()-> {

            //    bottomRightProgressBar.progressProperty().unbind();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                bottomRightFakeUpdateLabel.setText("Last updated on : "+
                        dtf.format(LocalDateTime.now()));
                progressBarGroup.setVisible(false);
                bottomRightFakeUpdateLabel.setVisible(true);
            });

        }
    };


}



