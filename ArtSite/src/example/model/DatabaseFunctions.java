package example.model;

import java.sql.*;


public class DatabaseFunctions {
    public static Connection databaseLink;

    public static Connection getConnection(String dbname, String user, String pass) {
        Connection conn = null;
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname, user, pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else {
                System.out.println("Connection Failed");
            }
        }catch (Exception e) {
            System.out.println(e);
        }
        return conn;
    }

    public static String[] getArtistDetails(Connection conn, int artistId) {
        String selectArtistSQL = "SELECT artist_name, country FROM artists WHERE artist_id = ?";
        try (PreparedStatement artistStatement = conn.prepareStatement(selectArtistSQL)) {
            artistStatement.setInt(1, artistId);
            ResultSet artistResultSet = artistStatement.executeQuery();
            if (artistResultSet.next()) {
                String artistName = artistResultSet.getString("artist_name");
                String artistCountry = artistResultSet.getString("country");
                return new String[]{artistName, artistCountry};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[]{"Unknown Artist", ""};
    }


    public static void deleteProduct(String title) throws SQLException {
        Connection conn = DatabaseFunctions.getConnection("appArt", "postgres", "1234");

        try {
            int artworkId;
            String selectArtworkIdSQL = "SELECT artwork_id FROM artworks WHERE title = ?";
            try (PreparedStatement selectArtworkIdStatement = conn.prepareStatement(selectArtworkIdSQL)) {
                selectArtworkIdStatement.setString(1, title);
                ResultSet resultSet = selectArtworkIdStatement.executeQuery();
                if (resultSet.next()) {
                    artworkId = resultSet.getInt("artwork_id");
                } else {
                    return;
                }
            }

            int artistId;
            String selectArtistIdSQL = "SELECT artist_id FROM artworks WHERE title = ?";
            try (PreparedStatement selectArtistIdStatement = conn.prepareStatement(selectArtistIdSQL)) {
                selectArtistIdStatement.setString(1, title);
                ResultSet resultSet = selectArtistIdStatement.executeQuery();
                if (resultSet.next()) {
                    artistId = resultSet.getInt("artist_id");
                } else {
                    return;
                }
            }

            int genreId;
            String selectGenreIdSQL = "SELECT genre_id FROM artwork_genres WHERE artwork_id = ?";
            try (PreparedStatement selectGenreIdStatement = conn.prepareStatement(selectGenreIdSQL)) {
                selectGenreIdStatement.setInt(1, artworkId);
                ResultSet resultSet = selectGenreIdStatement.executeQuery();
                if (resultSet.next()) {
                    genreId = resultSet.getInt("genre_id");
                } else {
                    return;
                }
            }

            String deleteGenresSQL = "DELETE FROM artwork_genres WHERE artwork_id = ?";
            try (PreparedStatement deleteGenresStatement = conn.prepareStatement(deleteGenresSQL)) {
                deleteGenresStatement.setInt(1, artworkId);
                deleteGenresStatement.executeUpdate();
            }

            String deleteProductSQL = "DELETE FROM artworks WHERE title = ?";
            try (PreparedStatement deleteStatement = conn.prepareStatement(deleteProductSQL)) {
                deleteStatement.setString(1, title);
                deleteStatement.executeUpdate();
            }

            String deleteArtistSQL = "DELETE FROM artists WHERE artist_id = ?";
            try (PreparedStatement deleteArtistStatement = conn.prepareStatement(deleteArtistSQL)) {
                deleteArtistStatement.setInt(1, artistId);
                deleteArtistStatement.executeUpdate();
            }

            String deleteGenreSQL = "DELETE FROM genres WHERE genre_id = ?";
            try (PreparedStatement deleteGenreStatement = conn.prepareStatement(deleteGenreSQL)) {
                deleteGenreStatement.setInt(1, genreId);
                deleteGenreStatement.executeUpdate();
            }

        } finally {
            DatabaseFunctions.closeConnection();
        }
    }

    public static void closeConnection() {
        if (databaseLink != null) {
            try {
                databaseLink.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertCredentials(Connection conn, String firstName, String lastName, String username, String email, String password) {
        Statement statement;
        try{
            String query = String.format("insert into users(username, email, password, first_name, last_name) values ('%s','%s','%s','%s','%s')", username, email, password, firstName, lastName);
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row added");
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void insertArtwork(Connection conn, String title, String description, int price, Date date, int userId, int artistId) {
        Statement statement;
        try {
            String query = String.format("INSERT INTO artworks(title, description, price, creation_date, user_upload_id, artist_id) VALUES ('%s', '%s', %s, '%s', %s, %s)",
                    title, description, price, date, userId, artistId);
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row added to artworks");
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static int insertArtist(Connection conn, String name, String country) {
        try {
            String query = String.format("insert into artists(artist_name, country) values ('%s','%s')", name, country);
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }

            System.out.println("Row added");
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }


    public static void insertGenre(Connection conn, String genre) {
        Statement statement;
        try{
            String query = String.format("insert into genres(genre_name) values ('%s')", genre);
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row added");
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void insertArtworkGenre(Connection conn, int artworkId, int genreId) {
        Statement statement;
        try {
            String query = String.format("INSERT INTO artwork_genres(artwork_id, genre_id) VALUES (%s, %s)", artworkId, genreId);
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row added to artwork_genres");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static int getGenreId(Connection conn, String genre) {
        try {
            String query = String.format("SELECT genre_id FROM genres WHERE genre_name = '%s'", genre);
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("genre_id");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }

    public static String getUserUploadDetails(Connection conn, int userUploadId) {
        String selectUserUploadSQL = "SELECT username FROM users WHERE user_id = ?";
        try (PreparedStatement userUploadStatement = conn.prepareStatement(selectUserUploadSQL)) {
            userUploadStatement.setInt(1, userUploadId);
            ResultSet userUploadResultSet = userUploadStatement.executeQuery();
            if (userUploadResultSet.next()) {
                return userUploadResultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown User";
    }

    public static String getGenreDetails(Connection conn, int genreId) {
        String selectGenreSQL = "SELECT genre_name FROM genres WHERE genre_id = ?";
        try (PreparedStatement genreStatement = conn.prepareStatement(selectGenreSQL)) {
            genreStatement.setInt(1, genreId);
            ResultSet genreResultSet = genreStatement.executeQuery();
            if (genreResultSet.next()) {
                return genreResultSet.getString("genre_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Genre";
    }

    public static void updateProduct(String oldTitle, String oldArtistName, String oldGenre, Products updatedProduct) throws SQLException {
        Connection connection = null;
        PreparedStatement pst = null;
        PreparedStatement pst2 = null;
        PreparedStatement pst3 = null;

        try {
            connection = getConnection("appArt", "postgres", "1234");

            String updateSQL = "UPDATE artworks SET title=?, description=?, price=?, creation_date=? WHERE title=?";
            String updateSQL2 = "UPDATE artists SET artist_name=?, country=? WHERE artist_name=?";
            String updateSQL3 = "UPDATE genres SET genre_name=? WHERE genre_name=?";
            pst = connection.prepareStatement(updateSQL);
            pst2 = connection.prepareStatement(updateSQL2);
            pst3 = connection.prepareStatement(updateSQL3);
            pst.setString(1, updatedProduct.getTitle());
            pst.setString(2, updatedProduct.getDescription());
            pst.setDouble(3, updatedProduct.getPrice());
            pst.setDate(4, updatedProduct.getCreationDate());
            pst.setString(5, oldTitle);

            pst2.setString(1, updatedProduct.getArtistName());
            pst2.setString(2, updatedProduct.getArtistCountry());
            pst2.setString(3, oldArtistName);

            pst3.setString(1, updatedProduct.getGenre());
            pst3.setString(2, oldGenre);

            int rowsAffected = pst.executeUpdate();
            int rowsAffected2 = pst2.executeUpdate();
            int rowsAffected3 = pst3.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update in artworks table successful");
            } else {
                System.out.println("No rows updated in artworks table");
            }
            if(rowsAffected2 > 0) {
                System.out.println("Update in artists table successful");
            } else {
                System.out.println("No rows updated in artists table");
            }
            if(rowsAffected3> 0) {
                System.out.println("Update in genres table successful");
            } else {
                System.out.println("No rows updated in genres table");
            }

        } finally {
            closeStatement(pst);
            closeStatement(pst2);
        }
    }

    private static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
