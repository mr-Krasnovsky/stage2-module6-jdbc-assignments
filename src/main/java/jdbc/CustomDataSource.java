package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;


@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;
    private final Connection connection;
    private static final SQLException MY_EXCEPTION = new SQLException();


    private CustomDataSource(String driver, String url, String password, String name) {
        connection = new CustomConnector().getConnection(url, name, password);
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
    }

    @SneakyThrows
    public static CustomDataSource getInstance() {
        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {
                    try {
                    Properties properties = new Properties();
                    properties.load(CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties"));
                    instance = new CustomDataSource(
                            properties.getProperty("postgres.driver"),
                            properties.getProperty("postgres.url"),
                            properties.getProperty("postgres.password"),
                            properties.getProperty("postgres.name"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection(){
        return new CustomConnector().getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password){
        return new CustomConnector().getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw MY_EXCEPTION;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw MY_EXCEPTION;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw MY_EXCEPTION;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw MY_EXCEPTION;
    }

       @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw MY_EXCEPTION;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw MY_EXCEPTION;
    }
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
