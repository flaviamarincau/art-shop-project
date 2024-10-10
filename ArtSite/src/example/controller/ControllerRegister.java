package example.controller;

import example.model.DatabaseFunctions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class ControllerRegister {

    @FXML
    private Button goBackButton;
    @FXML
    private Button signInButton;
    @FXML
    private TextField usernameField0;
    @FXML
    private TextField emailField0;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passwordField0;
    @FXML
    private Label registerMessageLabel;


@FXML
public void changeScene(String fxml, String title) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        Stage stage = (Stage) goBackButton.getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);

        stage.show();
    } catch (IOException e) {
        System.err.println("Error loading scene: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void goBackButtonOnAction(ActionEvent e) {

    changeScene("/login.fxml", "login");
    }

    public void registerButtonOnAction(ActionEvent e) {
        //registerMessageLabel.setText("You tried to register.");
        if(usernameField0.getText().isBlank()==false && emailField0.getText().isBlank()==false && passwordField0.getText().isBlank()==false){
            validateRegister();
        }else{
            registerMessageLabel.setText("Please enter username, e-mail and/or password");
        }
    }

    public void validateRegister() {
        DatabaseFunctions connectNow = new DatabaseFunctions();
        Connection connectDB = connectNow.getConnection("appArt", "postgres", "1234");


        String verifyRegister = "SELECT count(1) FROM users WHERE username = '" + usernameField0.getText() + "' OR email = '" + emailField0.getText() + "'";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyRegister);

            while (queryResult.next()) { // If the query returns a result, get the value from the first column and check if it's 1
                if (queryResult.getInt(1) == 0) {
                    registerMessageLabel.setText("Account created! Log in now.");
                    //ADD CREDENTIALS INTO THE DATABASE
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    String username = usernameField0.getText();
                    String email = emailField0.getText();
                    String password = passwordField0.getText();
                    example.model.DatabaseFunctions.insertCredentials(connectDB, firstName, lastName, username, email, password);
                } else {
                    registerMessageLabel.setText("Username or e-mail already exists!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
