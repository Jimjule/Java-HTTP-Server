import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    private Database database;
    private Connection connection;
    private Statement statement;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = App.getConnection(Statements.CREATE_TEST_DB, Statements.TEST_DATABASE);
        database = new Database(connection);
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        String sql = "DELETE FROM shopping_app_user_details";
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.execute(sql);
    }

    public int countUsers() throws SQLException {
        String getCount = "SELECT * FROM shopping_app_user_details;";
        ResultSet countAll = statement.executeQuery(getCount);
        countAll.next();
        return countAll.getRow();
    }

    @Test
    public void countUsersTest() throws SQLException {
        assertEquals(0, countUsers());
    }

    @Test
    public void queryUserExistsTest() throws SQLException {
        assertFalse(database.queryUserExists("no@such.email", 0));
    }

    @Test
    public void createUserTest() throws SQLException {
        database.createUser("now@email.exists", "pinskydan");
        assertEquals(1, countUsers());
        assertTrue(database.queryUserExists("now@email.exists", 0));
    }

    @Test
    public void deleteUserTest() throws SQLException {
        database.createUser("state@is.fleeting", "cromslor");
        database.deleteUserByEmail("state@is.fleeting");
        assertFalse(database.queryUserExists("state@is.fleeting", 0));
    }

    @Test
    public void failAuthenticateUserTest() throws SQLException {
        database.createUser("test@here.today", "aw beans");
        boolean authenticated;
        authenticated = database.authenticateUser("test@here.today", "password?");
        assertFalse(authenticated);
        database.deleteUserByEmail("test@here.today");
    }

    @Test
    public void passAuthenticateUserTest() throws SQLException {
        database.createUser("test@gone.tomorrow", "huzzah");
        boolean authenticated;
        authenticated = database.authenticateUser("test@gone.tomorrow", "huzzah");
        assertTrue(authenticated);
        database.deleteUserByEmail("test@gone.tomorrow");
    }
}
