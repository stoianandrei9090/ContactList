package ro.jademy.contactlist.fxcontactlist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhoneNumbersDialog {

    private static Stage dialogStage;
    private static Map<String, PhoneNumber> phoneNumberMap;
    private static ListView<String> listView;
    private static Scene dialogScene;


    public static void createDialog(User currUser) {


        phoneNumberMap = currUser.getPhoneNumbers();
        listViewSetItems();
        dialogStage = new Stage();
        GridPane grid = new GridPane();
        dialogScene = new Scene(grid, 400,300);
        dialogStage.setResizable(false);
        RowConstraints r1 = new RowConstraints(), r2 = new RowConstraints(), r3 = new RowConstraints();
        r1.setPercentHeight(15);
        r2.setPercentHeight(70);
        r3.setPercentHeight(15);
        grid.getRowConstraints().addAll(r1, r2, r3);
        Label l = new Label ("Phone numbers map : ");
        l.setTranslateX(30);
        Button addBtn = new Button("Add");
        addBtn.setTranslateX(-30);
        addBtn.setOnAction(new addButtonPressed(currUser));
        Button removeBtn = new Button("Remove");
        removeBtn.setTranslateX(0);
        removeBtn.setOnAction(new removeEventHandler(currUser));
        Button closeBtn = new Button("Close");
        closeBtn.setTranslateX(30);
        closeBtn.setOnAction(closeEventHandler);


        grid.add(l, 0, 0);
        grid.add(listView, 0, 1);
        FlowPane btnGroup = new FlowPane();
        btnGroup.setAlignment(Pos.CENTER);
        btnGroup.getChildren().addAll(addBtn, removeBtn, closeBtn);
        grid.add(btnGroup, 0,2);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Main.getPrimaryStage());
        dialogStage.setScene(dialogScene);
        dialogStage.show();

    }

    private static void listViewSetItems() {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, PhoneNumber> entry : phoneNumberMap.entrySet()) {
            list.add(entry.getKey()+" "+entry.getValue().getCountryCode()+" "+entry.getValue().getNumber());
      //      System.out.println(entry.getKey()+" "+entry.getValue().getCountryCode()+" "+entry.getValue().getNumber());
        }
        ObservableList<String> items = FXCollections.observableArrayList(list);
        if(listView == null) { listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
        else listView.getItems().clear();
        listView.setItems(items);
    }

    private static class addButtonPressed implements EventHandler<ActionEvent>   {

        User currUser;

        public addButtonPressed(User currUser){
        this.currUser = currUser;
        }

        public void handle(ActionEvent event) {
            Stage enterPhoneNumber = new Stage();
            enterPhoneNumber.initModality(Modality.WINDOW_MODAL);
            enterPhoneNumber.initOwner(dialogStage);
            Button ok = new Button("OK"), cancel = new Button("Cancel");
            Label place = new Label("Place : "), countryCode = new Label("Country code : "),
                    labelNumber = new Label("Number : ");
            TextField t1 = new TextField(), t2 = new TextField(), t3 = new TextField();
            GridPane gridPane = new GridPane();
            FlowPane btnGroupAdd = new FlowPane();
            btnGroupAdd.setAlignment(Pos.CENTER);
            btnGroupAdd.getChildren().addAll(ok, cancel);
            ok.setTranslateX(-20);
            cancel.setTranslateX(20);
            gridPane.setPadding(new Insets(5,30,5,30));
            gridPane.setVgap(5);
            gridPane.setAlignment(Pos.CENTER_RIGHT);
            gridPane.add(place, 0, 0);
            GridPane.setHalignment(place, HPos.RIGHT);
            gridPane.add(t1, 1, 0);
            gridPane.add(countryCode, 0,1);
            GridPane.setHalignment(countryCode, HPos.RIGHT);
            gridPane.add(t2, 1,1);
            gridPane.add(labelNumber, 0,2);
            GridPane.setHalignment(labelNumber, HPos.RIGHT);
            gridPane.add(t3, 1, 2);
            gridPane.add(btnGroupAdd, 0,3,2,1);
            GridPane.setHalignment(btnGroupAdd, HPos.CENTER);
            GridPane.setValignment(btnGroupAdd, VPos.CENTER);

            ok.setOnAction(event1 -> {
                boolean countryCodeValid = (t2.getText().startsWith("+") && t2.getText().length()>=2
                        && t2.getText().substring(1).matches("^[1-9][0-9]*$")
                        ) ? true : false;
                boolean phoneNumberValid = t3.getText().matches("^[1-9][0-9]*$");
                boolean keyValid = (!phoneNumberMap.containsKey(t1.getText()));

            if(!countryCodeValid || !phoneNumberValid || !keyValid) {
                // alert message
                Alert alertMessage = new Alert(Alert.AlertType.ERROR);
                alertMessage.setTitle("Error");
                alertMessage.setHeaderText(null);
                alertMessage.setGraphic(null);
                alertMessage.setContentText("Please enter valid phone number!");
                alertMessage.showAndWait();
            }

            else {
                //add everything to map
                phoneNumberMap.put(t1.getText(), new PhoneNumber(t2.getText(), t3.getText()));
                currUser.setPhoneNumbers(phoneNumberMap);
                listViewSetItems();
                enterPhoneNumber.close();
                Main.updateContactList();
            }

            });

            cancel.setOnAction(event2-> enterPhoneNumber.close());

            gridPane.setHgap(40);
            ColumnConstraints c1 = new ColumnConstraints(), c2 = new ColumnConstraints();
            c1.setPercentWidth(35);
            c2.setPercentWidth(65);
            gridPane.getColumnConstraints().addAll(c1, c2);
            RowConstraints rn1 = new RowConstraints(),
            rn2 = new RowConstraints(), rn3 = new RowConstraints(),  rb = new RowConstraints();
            rn1.setPercentHeight(20);
            rn2.setPercentHeight(20);
            rn3.setPercentHeight(20);
            rb.setPercentHeight(20);
            gridPane.getRowConstraints().addAll(rn1, rn2, rn3, rb);
           /* for(Node n : gridPane.getChildren()) {
                if(n instanceof Control || n instanceof Label)
                    ((Control) n).setPadding(new Insets(5));
            } */


            Scene addPhScene = new Scene(gridPane, 450, 250);
            enterPhoneNumber.setScene(addPhScene);
            enterPhoneNumber.show();
        }
    };

     private static class removeEventHandler implements EventHandler<ActionEvent>  {

         private User currUser;
         public removeEventHandler(User currUser) {
             this.currUser = currUser;
         }

         @Override
         public void handle(ActionEvent event) {

            if(phoneNumberMap.size()>1) {
            String key = listView.getSelectionModel().getSelectedItem().split(" ")[0];
            for (String keyInSet : phoneNumberMap.keySet()) {
                if(keyInSet.contains(key)) key = keyInSet;
                break;
            }
            phoneNumberMap.remove(key);
            currUser.setPhoneNumbers(phoneNumberMap);
            listViewSetItems();
            Main.updateContactList(); }

            else {    Alert alertMessage = new Alert(Alert.AlertType.ERROR);
                alertMessage.setTitle("Error");
                alertMessage.setHeaderText(null);
                alertMessage.setGraphic(null);
                alertMessage.setContentText("Must have at least one phone number. Add another and then delete");
                alertMessage.showAndWait();}

         }
    };

     private static EventHandler<ActionEvent> closeEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
           dialogStage.close();
        }
    };

}
