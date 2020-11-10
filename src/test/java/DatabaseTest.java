import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.skyscreamer.jsonassert.JSONAssert;

import java.sql.*;

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

    private int countUsers() throws SQLException {
        String getCount = "SELECT * FROM shopping_app_user_details;";
        ResultSet countAll = statement.executeQuery(getCount);
        countAll.next();
        return countAll.getRow();
    }

    private int getUserId(String email) throws SQLException {
        PreparedStatement getID = connection.prepareStatement("SELECT ID FROM shopping_app_user_details WHERE email = ?");
        getID.setString(1, email);
        ResultSet resultSet = getID.executeQuery();
        resultSet.next();
        int id = resultSet.getInt(1);
        getID.close();
        return id;
    }

    private String getUserDetails(int id) {
        return String.valueOf(database.getUserDetails(id));
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
    public void getUserDetailsTest() throws JSONException, SQLException {
        database.createUser(createUserEmail, createUserPassword);
        int id = getUserId(createUserEmail);
        String actual = getUserDetails(id);
        JSONAssert.assertEquals("{address: null}", actual, false);
        JSONAssert.assertEquals("{full_name: null}", actual, false);
        JSONAssert.assertEquals("{month: 0}", actual, false);
        JSONAssert.assertEquals("{year: 0}", actual, false);
        JSONAssert.assertEquals("{id: " + getUserId(createUserEmail) + "}", actual, false);
        JSONAssert.assertEquals("{postcode: null}", actual, false);
        JSONAssert.assertEquals("{day: 0}", actual, false);
        JSONAssert.assertEquals("{email: " + createUserEmail + "}", actual, false);
    }

    @Test
    public void editUserNameTest() throws SQLException, JSONException {
        database.createUser(createUserEmail, createUserPassword);
        int id = getUserId(createUserEmail);
        database.editUserDetails(id, "full_name", "Cromslor Pinskydan");
        String actual = getUserDetails(id);
        JSONAssert.assertEquals("{full_name: Cromslor Pinskydan}", actual, false);
    }

    @Test
    public void editUserEmailTest() throws SQLException, JSONException {
        database.createUser(createUserEmail, createUserPassword);
        int id = getUserId(createUserEmail);
        database.editUserDetails(id, "email", "test@edit.email");
        String actual = getUserDetails(id);
        JSONAssert.assertEquals("{email: test@edit.email}", actual, false);
    }

    @Test
    public void editUserDOBTest() throws SQLException, JSONException {
        database.createUser(createUserEmail, createUserPassword);
        int id = getUserId(createUserEmail);
        database.editUserDetails(id, "dob", "1999-02-01");
        String actual = getUserDetails(id);
        JSONAssert.assertEquals("{month: 2}", actual, false);
        JSONAssert.assertEquals("{day: 1}", actual, false);
        JSONAssert.assertEquals("{year: 1999}", actual, false);
    }
}
