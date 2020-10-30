import java.sql.*;

public class Database {

    private Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    public boolean queryUserExists(String email, int id) {
        boolean exists;
        try {
            PreparedStatement queryEmail = connection.prepareStatement("select 1 from shopping_app_user_details where email = ? AND id != ?");
            queryEmail.setString(1, email);
            queryEmail.setInt(2, id);
            ResultSet resultSet = queryEmail.executeQuery();

            if(resultSet.next()) {
                exists = true;
            } else {
                exists = false;
            }

            queryEmail.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void createUser(String email, String password) {
        try {
            PreparedStatement createUser = connection.prepareStatement("INSERT INTO shopping_app_user_details (email, password) VALUES(?, ?) RETURNING *");
            createUser.setString(1, email);
            createUser.setString(2, password);
            createUser.executeQuery();
            createUser.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUserByEmail(String email) {
        try {
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM shopping_app_user_details WHERE email = ?");
            deleteUser.setString(1, email);
            deleteUser.execute();
            deleteUser.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
