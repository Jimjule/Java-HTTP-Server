import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.System.exit;

public class App {

    public static void main(String[] args) {
        startPostgres();
        runCreateDB();

        Connection connection = getConnection(Statements.CREATE_DB, Statements.PROD_DATABASE);

        closeConnection(connection);
    }

    public static void startPostgres() {
        try {
            String[] arguments = {"brew", "services", "start", "postgresql"};
            Process process = new ProcessBuilder(arguments).start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void runCreateDB() {
        try {
            String createDB = "createdb";
            Process process = new ProcessBuilder(createDB).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(String createDB, String databaseName) {
        Connection connection = connectLocalHost();

        createDatabase(connection, createDB);
        connection = connectDatabase(connection, Statements.LOCAL_CONNECTION, databaseName);
        createTable(connection);

        return connection;
    }

    public static Connection connectLocalHost() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(Statements.LOCAL_CONNECTION);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            exit(1);
        }
        return connection;
    }

    private static void createDatabase(Connection connection, String createDB) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(createDB);
        } catch (SQLException e) {
        }
    }

    private static Connection connectDatabase(Connection connection, String host, String dbName) {
        try {
            connection = DriverManager.getConnection(host + dbName);
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
        return connection;
    }

    public static void createTable(Connection connection) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(Statements.CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
