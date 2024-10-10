package example.controller;

import example.model.DatabaseFunctions;
import example.model.SessionManager;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Controller {
    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button enterAsGuestButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    RolesEnum userRole = RolesEnum.GUEST;

    public void changeScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) cancelButton.getScene().getWindow();

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

    public void registerButtonOnAction(ActionEvent e) {
        changeScene("/register.fxml", "register");
    }

    public void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void loginButtonOnAction(ActionEvent e) {
        loginMessageLabel.setText("You tried to login");
        if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            // loginMessageLabel.setText("You tried to login");
            validateLogin();

        } else {
            loginMessageLabel.setText("Please enter username, e-mail and password!");
        }

    }

    public void enterAsGuestButtonOnAction(ActionEvent e) {
        if(userRole == RolesEnum.GUEST) {
        changeScene("/optionsGuest.fxml", "optionsGuest");
        }
    }

    public void validateLogin() {
        DatabaseFunctions connectNow = new DatabaseFunctions();
        Connection connectDB = DatabaseFunctions.getConnection("appArt", "postgres", "1234");

        String verifyLogin = "SELECT user_id, username FROM users WHERE username = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);
            preparedStatement.setString(1, usernameField.getText());
            preparedStatement.setString(2, passwordField.getText());
            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next()) {
                int userId = queryResult.getInt("user_id");
                String username = queryResult.getString("username");


                SessionManager sessionManager = SessionManager.getInstance();
                sessionManager.setUserId(userId);
                sessionManager.setUsername(username);
                changeScene("/options.fxml", "options");
            } else {
                loginMessageLabel.setText("Invalid login. Please try again.");
            }
            preparedStatement.close();
            queryResult.close();
            connectDB.close();
            System.out.println("Login Successful");
            userRole = RolesEnum.USER;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}