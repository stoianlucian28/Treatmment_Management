package lucian;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private int getID;
    private String getDate;
    private String getBodyPart;

    @FXML
    private Pane mainMenuPane, addDayPane;

    @FXML
    private Button mainMenu_btn ,addDay_btn;

    @FXML
    private TableView<TreatmentDetalis> table_info;

    @FXML
    private TableColumn<TreatmentDetalis, Integer> col_id;

    @FXML
    private TableColumn<TreatmentDetalis, String> col_date;

    @FXML
    private TableColumn<TreatmentDetalis, String> col_bodyPart;

//    @FXML
//    private JFXTextField txt_bodypart;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private ChoiceBox<String> choiceBodyPart;

    @FXML
    private JFXTextField updateID_txt;

    @FXML
    private JFXTextField updateBodyPart_txt;

    @FXML
    private JFXTextField updateDate_txt;

    @FXML
    private JFXTextField searchBar;


    @FXML
    private JFXButton saveButton;

    @FXML
    private Text updateMessage;

    ObservableList<TreatmentDetalis> listM;
    ObservableList<TreatmentDetalis> dataList;

    int index = -1;
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;


    @FXML
    private void handleButtonAction(ActionEvent event){

        if(event.getSource() == mainMenu_btn){

            mainMenuPane.toFront();
        }
        else if(event.getSource() == addDay_btn){

            addDayPane.toFront();
        }
    }

    @FXML
    private void close(MouseEvent event){

        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        s.close();
    }

    @FXML
    private void minimize(MouseEvent event){

        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        s.setIconified(true);
    }

    public void addEntry(){

        connection = ConnectionDB.ConnectDB();
        String sql = "insert into progress (date, bodyPart)values(?,? )";

        try{

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ((TextField)datePicker.getEditor()).getText());
            preparedStatement.setString(2, choiceBodyPart.getValue());

            if(((TextField)datePicker.getEditor()).getText().isEmpty() || choiceBodyPart.getValue() == null){

                JOptionPane.showMessageDialog(null, "Fields are empty!");
                return;
            }

            for(TreatmentDetalis data: listM){

                if(data.getDate().equals(((TextField)datePicker.getEditor()).getText())){

                    JOptionPane.showMessageDialog(null, "Another entry with same date already exist!");
                    return;
                }
            }

            preparedStatement.execute();

            JOptionPane.showMessageDialog(null, "Entry successfully added!");

            updateData();
            search_data();
        }
        catch (Exception exception){

            JOptionPane.showMessageDialog(null, exception);
        }
    }

    @FXML
    public void selectedData(MouseEvent event){

        index = table_info.getSelectionModel().getSelectedIndex();

        if(index <= -1){
            return;
        }

          getID = col_id.getCellData(index);
//        getDate = col_date.getCellData(index).toString();
//        getBodyPart = col_bodyPart.getCellData(index).toString();

        updateID_txt.setText(col_id.getCellData(index).toString());
        updateDate_txt.setText(col_date.getCellData(index).toString());
        updateBodyPart_txt.setText(col_bodyPart.getCellData(index).toString());
    }

    public void updateData(){

        col_id.setCellValueFactory(new PropertyValueFactory<TreatmentDetalis, Integer>("ID"));
        col_date.setCellValueFactory(new PropertyValueFactory<TreatmentDetalis, String>("Date"));
        col_bodyPart.setCellValueFactory(new PropertyValueFactory<TreatmentDetalis, String>("BodyPart"));

        listM = ConnectionDB.getDataInfo();
        table_info.setItems(listM);
    }

    public void deleteData(){

        connection = ConnectionDB.ConnectDB();
        String sql = "delete from progress where id = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, updateID_txt.getText());
            if(updateID_txt.getText().isEmpty()){

                JOptionPane.showMessageDialog(null, "You have to select and entry in order to delete it!");
                return;
            }

            preparedStatement.execute();

            JOptionPane.showMessageDialog(null, "Entry successfully deleted!");
            updateID_txt.setText("");
            updateDate_txt.setText("");
            updateBodyPart_txt.setText("");

            updateData();
            search_data();
        }
        catch (Exception exception){

            JOptionPane.showMessageDialog(null, exception);
        }
    }

    public void editData(){

        try{

            connection = ConnectionDB.ConnectDB();
            String value1 = updateID_txt.getText();
            String value2 = updateDate_txt.getText();
            String value3 = updateBodyPart_txt.getText();

            String sql = "update progress set id=\'" + value1 + "\'" + ",date= " + "\'" +value2 +"\'" + ",bodyPart=\'" + value3 +"\'" +
                    " where id=\'" + getID + "\'";

            preparedStatement = connection.prepareStatement(sql);

            if(value1.isEmpty() || value2.isEmpty() || value3.isEmpty()){

                JOptionPane.showMessageDialog(null, "You have to complete all fields!");
                return;
            }

            preparedStatement.execute();
            JOptionPane.showMessageDialog(null, "Entry successfully updated!");

            updateID_txt.setText("");
            updateDate_txt.setText("");
            updateBodyPart_txt.setText("");

            updateData();
            search_data();

        }
        catch (Exception exception){

            JOptionPane.showMessageDialog(null, exception);
        }
    }

    public void resetTableID(){

        try {

            connection = ConnectionDB.ConnectDB();
            String sql1 = "SET @num := 0;";
            String sql2 = "UPDATE progress SET id = @num := (@num+1);";
            String sql3 = "ALTER TABLE progress AUTO_INCREMENT =1;";


            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.execute();

            JOptionPane.showMessageDialog(null, "IDs successfully reseted!");
            updateData();
            search_data();
        }
        catch (Exception exception){

            JOptionPane.showMessageDialog(null, exception);
        }
    }

    @FXML
    void search_data() {
        col_id.setCellValueFactory(new PropertyValueFactory<TreatmentDetalis,Integer>("ID"));
        col_date.setCellValueFactory(new PropertyValueFactory<TreatmentDetalis,String>("Date"));
        col_bodyPart.setCellValueFactory(new PropertyValueFactory<TreatmentDetalis,String>("BodyPart"));

        dataList = ConnectionDB.getDataInfo();
        table_info.setItems(dataList);
        FilteredList<TreatmentDetalis> filteredData = new FilteredList<>(dataList, b -> true);
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(week -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (week.getDate().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                    return true; // Filter matches date
                } else if (week.getBodyPart().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches body part
                }
                else
                    return false; // Does not match.
            });
        });
        SortedList<TreatmentDetalis> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table_info.comparatorProperty());
        table_info.setItems(sortedData);
    }

    public void loadChoiceBox(){

        String a = "Brat drept";
        String b = "Picior stang";
        String c = "Brat stang";
        String d = "Picior drept";

        choiceBodyPart.getItems().add(a);
        choiceBodyPart.getItems().add(b);
        choiceBodyPart.getItems().add(c);
        choiceBodyPart.getItems().add(d);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        updateData();
        search_data();
        loadChoiceBox();
    }

}
