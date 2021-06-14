package lucian;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.util.Observable;

public class ConnectionDB {

    Connection conn = null;

    public static Connection ConnectDB(){

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection =  (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3310/methotrexate_treatment", "root", "Dbpass@2000");
//            JOptionPane.showMessageDialog(null, "Connection establihed!");
            return connection;
        }
        catch (Exception exception){

            JOptionPane.showMessageDialog(null, exception);
            return null;
        }
    }

    public static ObservableList<TreatmentDetalis> getDataInfo(){

        Connection connection = ConnectDB();
        ObservableList<TreatmentDetalis> list = FXCollections.observableArrayList();

        try{

            PreparedStatement ps = connection.prepareStatement("select * from progress");
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                list.add(new TreatmentDetalis(Integer.parseInt(rs.getString("id")), rs.getString("date"), rs.getString("bodyPart")));
            }
        }
        catch (Exception exception){

        }

        return list;
    }
}
