package ro.jademy.contactlist.fxcontactlist;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.User;

public class EditUserAddress  {

    static Stage windowStage;

    public static void createWindow(User currUser) {

        GridPane grid  = new GridPane();
        EditAddressGridClass e = new EditAddressGridClass();
        grid = e.createGrid();
        Scene scene = new Scene(grid, 437, 345);
        windowStage = new Stage();
        windowStage.setScene(scene);
        windowStage.setResizable(false);
        windowStage.initModality(Modality.WINDOW_MODAL);
        windowStage.initOwner(Main.getPrimaryStage());
        windowStage.show();
        Address uAddress = currUser.getAddress();
        e.getTextField(0).setText(uAddress.getStreetName());
        e.getTextField(1).setText(String.valueOf(uAddress.getStreetNumber()));
        e.getTextField(2).setText(String.valueOf(uAddress.getApartmentNumber()));
        // floor zipcode city country
        e.getTextField(3).setText(uAddress.getFloor());
        e.getTextField(4).setText(uAddress.getZipCode());
        e.getTextField(5).setText(uAddress.getCity());
        e.getTextField(6).setText(uAddress.getCountry());
        e.getOkBtn().setOnAction(new OkEventHandlerClass(e, currUser));

    }

    static class OkEventHandlerClass implements EventHandler<ActionEvent> {

        EditAddressGridClass e;
        User currUser;

        public OkEventHandlerClass(EditAddressGridClass e, User currUser) {
            this.e = e;
            this.currUser = currUser;
        }
        @Override
        public void handle(ActionEvent event) {
            if(!e.validateAddressFields()) e.commonAlertMessage();
            else {


                currUser.getAddress().setStreetName(e.getTextField(0).getText());
                currUser.getAddress().
                        setStreetNumber(Integer.parseInt(e.getTextField(1).getText()));
                currUser.getAddress().
                        setApartmentNumber(Integer.parseInt(e.getTextField(2).getText()));
                currUser.getAddress().setFloor(e.getTextField(3).getText());
                currUser.getAddress().setZipCode(e.getTextField(4).getText());
                currUser.getAddress().setCity(e.getTextField(5).getText());
                currUser.getAddress().setCountry(e.getTextField(6).getText());
                Main.updateContactList();
                windowStage.close();

            }
        }
    }



}
