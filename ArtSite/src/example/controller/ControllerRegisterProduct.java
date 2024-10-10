package example.controller;

import example.model.DatabaseFunctions;
import example.model.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.*;
import java.util.List;

import static example.model.DatabaseFunctions.getGenreId;


public class ControllerRegisterProduct {
    @FXML
    public Button goBackButton;
    @FXML
    public Button addProductButton;
    @FXML
    public Button seeAllProductsButton;
    @FXML
    public Button seeAllGenres;
    @FXML
    public Label messageAddProductLabel;
    @FXML
    public TextField titleAddProductField;
    @FXML
    public TextField priceAddProductField;
    @FXML
    public TextField descriptionAddProductField;
    @FXML
    public TextField artistNameAddProductField;
    @FXML
    public TextField artistCountryAddProductField;
    @FXML
    public TextField genreAddProductField;
    @FXML
    public DatePicker dateAddProductDatePicker;



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
            // Handle the exception more gracefully
            System.err.println("Error loading scene: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }
    @FXML
    void goBackButtonOnAction(ActionEvent event) {
        changeScene("/options.fxml", "Options");
    }
    @FXML
    void addProductButtonOnAction(ActionEvent event) {
        if(conditions()==1){
            validateProduct();
        }else{
            messageAddProductLabel.setText("Please enter all marked fields!");
        }
    }
    @FXML
    void seeAllProductsButtonOnAction(ActionEvent event) {
        changeScene("/sales.fxml", "sales");
    }

    @FXML
    private void seeAllGenresButtonOnAction(ActionEvent event) {
        List<String> genresList = List.of(
                "Portrait", "Landscape", "Still Life", "Historical Painting",
                "Genre Painting", "Abstract", "Surrealism", "Expressionism",
                "Impressionism", "Cubism", "Pop Art", "Realism", "Minimalism",
                "Fauvism", "Symbolism", "Abstract Expressionism", "Nude",
                "Religious Painting"
        );

        ListView<String> listView = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList(genresList);
        listView.setItems(items);

        Popup popup = new Popup();
        popup.getContent().add(listView);

        popup.show(((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()));

        listView.setOnMouseClicked(mouseEvent -> {
            String selectedGenre = listView.getSelectionModel().getSelectedItem();
            setGenresTextField(selectedGenre);
            popup.hide();
        });
    }


    private List<String> getGenresListFromDatabase() {
        return List.of("Portrait", "Landscape", "Still Life", "Historical Painting",
                "Genre Painting", "Abstract", "Surrealism", "Expressionism",
                "Impressionism", "Cubism", "Pop Art", "Realism", "Minimalism",
                "Fauvism", "Symbolism", "Abstract Expressionism", "Nude",
                "Religious Painting");
    }

    private void setGenresTextField(String genre) {
        genreAddProductField.setText(genre);
    }
    int conditions(){
       int ok=1;
        if(titleAddProductField.getText().isBlank()) { ok=0; }
        if(priceAddProductField.getText().isBlank()) { ok=0; }
        if(artistNameAddProductField.getText().isBlank()) { ok=0; }
        if(artistCountryAddProductField.getText().isBlank()) { ok=0; }
        if(genreAddProductField.getText().isBlank()) { ok=0; }
        if(dateAddProductDatePicker.getValue()==null) { ok=0; }
        return ok;
    }

    void validateProduct() {
        DatabaseFunctions connectNow = new DatabaseFunctions();
        Connection connectDB = connectNow.getConnection("appArt", "postgres", "1234");
        messageAddProductLabel.setText("You tried to add a product.");
        try {
            String artistName = artistNameAddProductField.getText();
            String artistCountry = artistCountryAddProductField.getText();
            int artistId = example.model.DatabaseFunctions.insertArtist(connectDB, artistName, artistCountry);

            String title = titleAddProductField.getText();
            String price = priceAddProductField.getText();
            int priceInt = Integer.parseInt(price);
            String description = descriptionAddProductField.getText();
            String genre = genreAddProductField.getText();
            String date = dateAddProductDatePicker.getValue().toString();
            Date date1 = Date.valueOf(date);
            example.model.DatabaseFunctions.insertArtwork(connectDB, title, description, priceInt, date1, SessionManager.getInstance().getUserId(), artistId);

            example.model.DatabaseFunctions.insertGenre(connectDB, genre);

            int artworkId = getArtworkId(connectDB, title, SessionManager.getInstance().getUserId());

            int genreId = getGenreId(connectDB, genre);

            example.model.DatabaseFunctions.insertArtworkGenre(connectDB, artworkId, genreId);

            messageAddProductLabel.setText("Product added and available in Sales page.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getArtworkId(Connection conn, String title, int userId) {
        try {
            String query = String.format("SELECT artwork_id FROM artworks WHERE title = '%s' AND user_upload_id = %s", title, userId);
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("artwork_id");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }



}
