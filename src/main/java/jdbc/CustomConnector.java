package jdbc;

import java.sql.*;

public class CustomConnector {
    public Connection getConnection(String url) {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e){
            e.printStackTrace();
            return  null;
        }
    }
    public Connection getConnection(String url, String user, String password)  {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
