package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomConnector {
        public Connection connection = null;
    public Connection getConnection(String url) {
        try (Connection urlConnection = DriverManager.getConnection(url)){
           connection = urlConnection;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection(String url, String user, String password)  {
        try (Connection dataConnection = DriverManager.getConnection(url, user, password)){
            connection = dataConnection;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}
