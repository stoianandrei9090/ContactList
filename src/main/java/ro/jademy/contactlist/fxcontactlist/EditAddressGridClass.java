package ro.jademy.contactlist.fxcontactlist;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class EditAddressGridClass {

    private TextField[] addressTextFields;
    private Button okBtn;

    public TextField getTextField(int i) {
        if(i>=addressTextFields.length)
            throw new RuntimeException("Null pointer excep. getTextField param too high.");
        else return this.addressTextFields[i];
    }

    public Button getOkBtn(){
        return this.okBtn;
    }

    public boolean validateAddressFields() {
        //have to work on these
        String s = addressTextFields[1].getText();
        boolean apNumStreetNum = true;
        try
        {
           if(Integer.parseInt(addressTextFields[1].getText())<1 ||
            Integer.parseInt(addressTextFields[2].getText())<1) apNumStreetNum = false;
        } catch (Exception e) {
            apNumStreetNum = false;
        };
        /*
        if(!s.equals("")) s = s.substring(0, s.length()-1);
        String digitRegex = "^[1-9][0-9]*$";
        boolean strNumB =  s.matches(digitRegex) && addressTextFields[1].getText().substring(s.length()).
                toLowerCase().matches("[a-z]");
        boolean apaNumB = addressTextFields[2].getText().matches(digitRegex); */

        boolean floorNumB = addressTextFields[3].getText().matches("[0-9][1-9]*") ||
                addressTextFields[3].getText().matches("-[0-7]");
        boolean zipCodeB = addressTextFields[4].getText().matches("([0-9])*")
        && !addressTextFields[4].getText().replaceAll("0", "").equals("");
        String cityString, countryString;
        cityString = addressTextFields[5].getText();
        countryString = addressTextFields[6].getText();
        boolean cityCountryOK = (cityString.equals(cityString.replaceAll("[0-9]", ""))&&
        countryString.equals(countryString.replaceAll("[0-9]", ""))) ;
        if(apNumStreetNum && floorNumB && zipCodeB && cityCountryOK) return true;
        else return false;
    }

    public static void commonAlertMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText("Insert valid inputs");
        alert.showAndWait();
    }

    public GridPane createGrid() {
        GridPane grid  = new GridPane();
        addressTextFields = new TextField[7];
        okBtn = new Button("OK");

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c1.setPercentWidth(30);
        c2.setPercentWidth(70);
        grid.getColumnConstraints().addAll(c1, c2);
        String[] fields = {"Street Name", "Street Number", "Apartment Number", "Floor", "Zip Code", "City", "Country"};
        for (int i = 0; i < 7 ; i++) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(11);
            grid.getRowConstraints().add(r);
            Label l = new Label(fields[i]+" : ");
            addressTextFields[i] = new TextField();
            GridPane.setValignment(addressTextFields[i], VPos.CENTER);
            GridPane.setHalignment(addressTextFields[i], HPos.CENTER);
            GridPane.setValignment(l, VPos.CENTER);
            GridPane.setHalignment(l, HPos.CENTER);
            grid.add(addressTextFields[i], 1, i);
            grid.add(l, 0, i);
        }
        grid.setPadding(new Insets(40,27, 6,27));
        grid.setVgap(2);
        RowConstraints r8 = new RowConstraints();
        r8.setPercentHeight(23);
        grid.getRowConstraints().add(r8);
        GridPane.setHalignment(okBtn, HPos.CENTER);
        grid.add(okBtn, 0, 7, 2, 1);

        return grid;
    };
}
