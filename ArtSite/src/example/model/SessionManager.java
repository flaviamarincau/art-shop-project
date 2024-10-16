package example.model;

import java.sql.*;

public class SessionManager {
    private static SessionManager instance;
    private int userId;
    private String username;

    private SessionManager() { }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void retrieveUserIdFromDatabase(String username) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
        String jdbcUser = "postgres";
        String jdbcPassword = "admin";

        String selectUserIdSQL = "SELECT user_id FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(selectUserIdSQL)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setUserId(resultSet.getInt("user_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}