import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.skyscreamer.jsonassert.JSONAssert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest {

    private Database database;
    private Connection connection;
    private Statement statement;

    private final String createUserEmail = "now@email.exists";
    private final String createUserPassword = "authentify";
    private final String noSuchEmail = "state@is.fleeting";
    private final String noSuchPassword = "1234";

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
    public void queryUserExistsTest() {
        assertFalse(database.queryUserExists(noSuchEmail, 0));
    }

    @Test
    public void createUserTest() throws SQLException {
        database.createUser(createUserEmail, createUserPassword);
        assertEquals(1, countUsers());
        assertTrue(database.queryUserExists(createUserEmail, 0));
    }

    @Test
    public void deleteUserTest() {
        database.createUser(createUserEmail, createUserPassword);
        database.deleteUserByEmail(createUserEmail);
        assertFalse(database.queryUserExists(createUserEmail, 0));
    }

    @Test
    public void failAuthenticateUserTest() {
        database.createUser(createUserEmail, createUserPassword);
        boolean authenticated = database.authenticateUser(createUserEmail, noSuchPassword);
        assertFalse(authenticated);
    }

    @Test
    public void passAuthenticateUserTest() {
        database.createUser(createUserEmail, createUserPassword);
        boolean authenticated = database.authenticateUser(createUserEmail, createUserPassword);
        assertTrue(authenticated);
    }

    @Test
    public void getUserDetailsTest() throws JSONException {
        database.createUser(createUserEmail, createUserPassword);
        String actual = String.valueOf(database.getUserDetails(createUserEmail));

        JSONAssert.assertEquals("{address: null}", actual, false);
        JSONAssert.assertEquals("{full_name: null}", actual, false);
        JSONAssert.assertEquals("{month: 0}", actual, false);
        JSONAssert.assertEquals("{year: 0}", actual, false);
        JSONAssert.assertEquals("{postcode: null}", actual, false);
        JSONAssert.assertEquals("{day: 0}", actual, false);
        JSONAssert.assertEquals("{email: " + createUserEmail + "}", actual, false);
    }
}
