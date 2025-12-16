package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection implements DatabaseConnectionProvider{
    private static final String DB_URL = "jdbc:sqlite:zaz.db";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
