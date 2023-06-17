package jdbc;


import lombok.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname=?, lastname=?, age=? WHERE id=?";
    private static final String deleteUserSQL = "DELETE FROM myusers WHERE id=?";
    private static final String findUserByIdSQL = "SELECT*FROM myusers WHERE id=?";
    private static final String findUserByNameSQL = "SELECT*FROM myusers WHERE firstname=?";
    private static final String findAllUserSQL = "SELECT*FROM myusers";

    {
        try {
            connection = CustomDataSource.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public Long createUser(User user) {
        Long id = null;
        try {
            ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                id = keys.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @SneakyThrows
    public User findUserById(Long userId) {
        User user = null;
        try {
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                user = new User(id, firstname, lastname, age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @SneakyThrows
    public User findUserByName(String userName) {
        User user = null;
        try {
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                user = new User(id, firstname, lastname, age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @SneakyThrows
    public List<User> findAllUser() throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery(findAllUserSQL);
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(rs.getLong("id"), rs.getString("firstname"), rs.getString("lastname"),
                    rs.getInt("age")));
        }
        return users;
    }

    @SneakyThrows
    public User updateUser(User user) {
        ps = connection.prepareStatement(updateUserSQL);
        ps.setLong(4, user.getId());
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setInt(3, user.getAge());
        return findUserById(user.getId());
    }

    public void deleteUser(Long userId) {
        try {
            ps = connection.prepareStatement(deleteUserSQL);
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
