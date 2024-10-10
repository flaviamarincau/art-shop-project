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

public class ControllerOptions {
    @FXML
    private Button logOutButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button salesButton;

    @FXML
    private Button registerProductButton;

    public void changeScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) exitButton.getScene().getWindow();

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

    public void exitButtonOnAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void logOutButtonOnAction(ActionEvent event) {
        changeScene("/login.fxml", "Login");
    }

    public void salesButtonOnAction(ActionEvent event) {
        changeScene("/sales.fxml", "Sales");
    }

    public void registerProductButtonOnAction(ActionEvent event) {
        changeScene("/registerProduct.fxml", "Register Product");
    }

}
