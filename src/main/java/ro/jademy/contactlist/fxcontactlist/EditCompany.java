package ro.jademy.contactlist.fxcontactlist;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.User;

import javax.swing.*;

public class EditCompany {

    private static Stage windowStage;
    private static TextField companyNameTextField;

    public static void createWindow(User currUser) {


        GridPane grid  = new GridPane();
        EditAddressGridClass e = new EditAddressGridClass();
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(9);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(91);
        grid.getRowConstraints().addAll(r1, r2);
        grid.add(e.createGrid(), 0,1);
        GridPane upperFieldGrid = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c1.setPercentWidth(30);
        c2.setPercentWidth(70);
        upperFieldGrid.getColumnConstraints().addAll(c1, c2);
        Label companyLabel = new Label("Company Name : ");
        companyNameTextField = new TextField();
        GridPane.setValignment(companyNameTextField, VPos.CENTER);
        GridPane.setHalignment(companyNameTextField, HPos.CENTER);
        GridPane.setValignment(companyLabel, VPos.CENTER);
        GridPane.setHalignment(companyLabel, HPos.CENTER);
        upperFieldGrid.setPadding(new Insets(40, 27, 6, 27));
        upperFieldGrid.setAlignment(Pos.CENTER);
        upperFieldGrid.add(companyLabel, 0,0);
        upperFieldGrid.add(companyNameTextField, 1, 0);
        grid.add(upperFieldGrid, 0,0);

        Scene scene = new Scene(grid, 437, 383);
        windowStage = new Stage();
        windowStage.initModality(Modality.WINDOW_MODAL);
        windowStage.initOwner(Main.getPrimaryStage());
        windowStage.setScene(scene);
        windowStage.setResizable(false);
        windowStage.show();

        Address cAddress = currUser.getCompany().getAddress();
        companyNameTextField.setText(currUser.getCompany().getName());
        e.getTextField(0).setText(cAddress.getStreetName());
        e.getTextField(1).setText(String.valueOf(cAddress.getStreetNumber()));
        e.getTextField(2).setText(String.valueOf(cAddress.getApartmentNumber()));
        // floor zipcode city country
        e.getTextField(3).setText(cAddress.getFloor());
        e.getTextField(4).setText(cAddress.getZipCode());
        e.getTextField(5).setText(cAddress.getCity());
        e.getTextField(6).setText(cAddress.getCountry());


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
                /*String[] fields = {"Street Name", "Street Number",
                "Apartment Number", "Floor", "Zip Code", "City", "Country"};


                 */
                currUser.getCompany().setName(companyNameTextField.getText());
                currUser.getCompany().getAddress().setStreetName(e.getTextField(0).getText());
                currUser.getCompany().getAddress().
                        setStreetNumber(Integer.parseInt(e.getTextField(1).getText()));
                currUser.getCompany().getAddress().
                setApartmentNumber(Integer.parseInt(e.getTextField(2).getText()));
                currUser.getCompany().getAddress().setFloor(e.getTextField(3).getText());
                currUser.getCompany().getAddress().setZipCode(e.getTextField(4).getText());
                currUser.getCompany().getAddress().setCity(e.getTextField(5).getText());
                currUser.getCompany().getAddress().setCountry(e.getTextField(6).getText());
                Main.updateContactList();
                windowStage.close();

            }
        }
    }
}
