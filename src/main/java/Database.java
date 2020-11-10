import org.json.simple.JSONObject;

import java.sql.*;
import java.util.Optional;

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

            exists = resultSet.next();

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

    public boolean authenticateUser(String email, String password) {
        boolean authenticated = false;
        try {
            PreparedStatement authenticate = connection.prepareStatement("SELECT (password = ?) AS match FROM shopping_app_user_details WHERE email = ?;");
            authenticate.setString(1, password);
            authenticate.setString(2, email);
            ResultSet resultSet = authenticate.executeQuery();
            resultSet.next();
            authenticated = resultSet.getBoolean(1);
            authenticate.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authenticated;
    }

    public JSONObject getUserDetails(int id) {
        JSONObject userDetails = new JSONObject();
        try {
            PreparedStatement getDetails = connection.prepareStatement("SELECT id, email, full_name, address, postcode, dob, EXTRACT(year FROM dob) as year, EXTRACT(month FROM dob) as month, EXTRACT(day FROM dob) as day FROM shopping_app_user_details WHERE id = ?;");
            getDetails.setInt(1, id);
            ResultSet resultSet = getDetails.executeQuery();
            while (resultSet.next()) {
                userDetails.put("address", resultSet.getString("address"));
                userDetails.put("day", resultSet.getInt("day"));
                userDetails.put("email", resultSet.getString("email"));
                userDetails.put("full_name", resultSet.getString("full_name"));
                userDetails.put("id", resultSet.getInt("id"));
                userDetails.put("month", resultSet.getInt("month"));
                userDetails.put("postcode", resultSet.getString("postcode"));
                userDetails.put("year", resultSet.getInt("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userDetails;
    }

    public void editUserDetails(int id, String field, String value) {
        try {
            PreparedStatement editDetail = connection.prepareStatement("UPDATE shopping_app_user_details SET " + field + " = ? WHERE id = ?;");
            checkFieldType(field, value, editDetail);
            editDetail.setInt(2, id);
            editDetail.execute();
            editDetail.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkFieldType(String field, String value, PreparedStatement statement) throws SQLException {
        if (field.equals("dob")) {
            Date date = java.sql.Date.valueOf(value);
            statement.setDate(1, date);
        } else {
            statement.setString(1, value);
        }
    }
}
