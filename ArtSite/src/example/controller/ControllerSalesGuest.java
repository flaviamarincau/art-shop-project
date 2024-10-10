package example.controller;

import example.model.DatabaseFunctions;
import example.model.Products;
import example.model.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerSalesGuest implements Initializable {
    @FXML
    private Button goBackButton;

    @FXML
    private Button addProductButton;

    @FXML
    private Button updateProductButton;

    @FXML
    private Button deleteProductButton;

    @FXML
    private TextField searchByArtistField;

    @FXML
    private TextField searchByTitleField;

    @FXML
    private TableView<Products> tableProducts;

    @FXML
    private TableColumn<Products, String> titleTableColumn;

    @FXML
    private TableColumn<Products, String> descriptionTableColumn;

    @FXML
    private TableColumn<Products, Integer> priceTableColumn;

    @FXML
    private TableColumn<Products, String> artistTableColumn;

    @FXML
    private TableColumn<Products, String> artistCountryTableColumn;

    @FXML
    private TableColumn<Products, Date> creationDateTableColumn;

    @FXML
    private TableColumn<Products, String> genreTableColumn;

    @FXML
    private TableColumn<Products, String> userUploadTableColumn;

    @FXML
    private TextField editTitleField;
    @FXML
    private TextField editDescriptionField;
    @FXML
    private TextField editPriceField;
    @FXML
    private TextField editArtistField;
    @FXML
    private TextField editArtistCountryField;
    @FXML
    private TextField editGenreField;
    @FXML
    private DatePicker editDatePicker;

    private ObservableList<Products> productsList;

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

    @FXML
    void goBackButtonOnAction(ActionEvent event) {
        changeScene("/optionsGuest.fxml", "OptionsGuest");
    }

    @FXML
    void addProductButtonOnAction(ActionEvent event) {
        showAlert("Log in needed", "Please log in to add a product.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        artistTableColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        artistCountryTableColumn.setCellValueFactory(new PropertyValueFactory<>("artistCountry"));
        creationDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        genreTableColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        userUploadTableColumn.setCellValueFactory(new PropertyValueFactory<>("userUpload"));
        fetchProductsFromDatabase();
    }

    private void fetchProductsFromDatabase() {
        Connection conn = DatabaseFunctions.getConnection("appArt", "postgres", "1234");

        productsList = FXCollections.observableArrayList();
        String selectProductsSQL = "SELECT a.price, a.title, a.description, a.creation_date, " +
                "a.artist_id, a.user_upload_id, ag.genre_id " +
                "FROM artworks a " +
                "LEFT JOIN artwork_genres ag ON a.artwork_id = ag.artwork_id " +
                "LEFT JOIN artists a2 ON a.artist_id = a2.artist_id";

        try (PreparedStatement preparedStatement = conn.prepareStatement(selectProductsSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                double price = resultSet.getDouble("price");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Date creationDate = resultSet.getDate("creation_date");
                int artistId = resultSet.getInt("artist_id");
                int userUploadId = resultSet.getInt("user_upload_id");
                int genreId = resultSet.getInt("genre_id");

                String[] artistDetails = DatabaseFunctions.getArtistDetails(conn, artistId);

                String usernameUpload = DatabaseFunctions.getUserUploadDetails(conn, userUploadId);

                String genreName = DatabaseFunctions.getGenreDetails(conn, genreId);

                String artistName = artistDetails[0];
                String artistCountry = artistDetails[1];

                Products product = new Products(price, title, description, artistName, artistCountry, genreName, usernameUpload, creationDate);
                productsList.add(product);
            }

            tableProducts.setItems(productsList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseFunctions.closeConnection();
        }
    }

@FXML
void updateProductButtonOnAction(ActionEvent event) {
        showAlert("Log in needed", "Please log in to update a product.");
    }

@FXML
void deleteProductButtonOnAction(ActionEvent event) {

        showAlert("Log in needed", "Please log in to delete a product.");
    }


@FXML
    void searchByArtistFieldOnAction(ActionEvent event) {
        String artistName = searchByArtistField.getText().trim();
        if (!artistName.isEmpty()) {
            searchProductsByArtist(artistName);
        } else {
            fetchProductsFromDatabase();
        }
    }

    @FXML
    void searchByTitleFieldOnAction(ActionEvent event) {
        String title = searchByTitleField.getText().trim();
        if (!title.isEmpty()) {
            searchProductsByTitle(title);
        } else {
            fetchProductsFromDatabase();
        }
    }

    private void searchProductsByArtist(String artistName) {
        Connection conn = DatabaseFunctions.getConnection("appArt", "postgres", "1234");

        productsList = FXCollections.observableArrayList();
        String selectProductsByArtistSQL = "SELECT a.price, a.title, a.description, a.creation_date, " +
                "a.artist_id, a.user_upload_id, ag.genre_id " +
                "FROM artworks a " +
                "LEFT JOIN artwork_genres ag ON a.artwork_id = ag.artwork_id " +
                "LEFT JOIN artists a2 ON a.artist_id = a2.artist_id " +  // Added space before WHERE
                "WHERE a2.artist_name ILIKE ?";  // Use ILIKE for case-insensitive search
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectProductsByArtistSQL)) {
            preparedStatement.setString(1, "%" + artistName + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double price = resultSet.getDouble("price");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Date creationDate = resultSet.getDate("creation_date");
                int artistId = resultSet.getInt("artist_id");
                int userUploadId = resultSet.getInt("user_upload_id");
                int genreId = resultSet.getInt("genre_id");

                String[] artistDetails = DatabaseFunctions.getArtistDetails(conn, artistId);

                String usernameUpload = DatabaseFunctions.getUserUploadDetails(conn, userUploadId);

                String genreName = DatabaseFunctions.getGenreDetails(conn, genreId);

                String artistNameFromDB = artistDetails[0];
                String artistCountry = artistDetails[1];

                Products product = new Products(price, title, description, artistNameFromDB, artistCountry, genreName, usernameUpload, creationDate);
                productsList.add(product);
            }

            tableProducts.setItems(productsList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseFunctions.closeConnection();
        }
    }


    private void searchProductsByTitle(String title) {
        Connection conn = DatabaseFunctions.getConnection("appArt", "postgres", "1234");

        productsList = FXCollections.observableArrayList();
        String selectProductsByTitleSQL = "SELECT a.price, a.title, a.description, a.creation_date, " +
                "a.artist_id, a.user_upload_id, ag.genre_id " +
                "FROM artworks a " +
                "LEFT JOIN artwork_genres ag ON a.artwork_id = ag.artwork_id " +
                "WHERE a.title ILIKE ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectProductsByTitleSQL)) {
            preparedStatement.setString(1, "%" + title + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double price = resultSet.getDouble("price");
                String productTitle = resultSet.getString("title");
                String description = resultSet.getString("description");
                Date creationDate = resultSet.getDate("creation_date");
                int artistId = resultSet.getInt("artist_id");
                int userUploadId = resultSet.getInt("user_upload_id");
                int genreId = resultSet.getInt("genre_id");

                String[] artistDetails = DatabaseFunctions.getArtistDetails(conn, artistId);

                String usernameUpload = DatabaseFunctions.getUserUploadDetails(conn, userUploadId);

                String genreName = DatabaseFunctions.getGenreDetails(conn, genreId);

                String artistName = artistDetails[0];
                String artistCountry = artistDetails[1];

                Products product = new Products(price, productTitle, description, artistName, artistCountry, genreName, usernameUpload, creationDate);
                productsList.add(product);
            }

            tableProducts.setItems(productsList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseFunctions.closeConnection();
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
