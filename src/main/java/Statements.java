public final class Statements {
    public static final String LOCAL_CONNECTION = "jdbc:postgresql://localhost:5432/";

    public static final String PROD_DATABASE = "prod_shopping_app";

    public static final String TEST_DATABASE = "test_shopping_app";

    public static final String CREATE_DB = "CREATE DATABASE " + Statements.PROD_DATABASE;

    public static final String CREATE_TEST_DB = "CREATE DATABASE " + Statements.TEST_DATABASE;

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS shopping_app_user_details(" +
            "ID SERIAL," +
            "email TEXT NOT NULL UNIQUE," +
            "full_name TEXT," +
            "address VARCHAR(50)," +
            "postcode VARCHAR(12)," +
            "dob DATE," +
            "password TEXT NOT NULL" +
            ")";
}
