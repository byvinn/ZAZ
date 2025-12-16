package db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(DatabaseConnectionProvider database) {
        try {
            connection = database.getConnection();
            new DatabaseInitializer(connection).initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
