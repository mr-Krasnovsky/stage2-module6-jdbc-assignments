package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname=?, lastname=?, age=? WHERE id=?";
    private static final String deleteUserSQL = "DELETE FROM myusers WHERE id=?";
    private static final String findUserByIdSQL = "SELECT*FROM myusers WHERE id=?";
    private static final String findUserByNameSQL = "SELECT*FROM myusers WHERE firstname=?";
    private static final String findAllUserSQL = "SELECT*FROM myusers";
    {
        try
        {
            connection = CustomDataSource.getInstance().getConnection();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public Long createUser(User user) {
        Long id = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                id = keys.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return id;
    }

    public User findUserById(Long userId) {
        User user = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(findUserByIdSQL);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                user = new User(id, firstname, lastname, age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try {
            PreparedStatement preparedStatement  = connection.prepareStatement(findUserByNameSQL);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                user = new User(id, firstname, lastname, age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return user;
    }

    public List<User> findAllUser() throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery(findAllUserSQL);
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(rs.getLong("id"), rs.getString("firstname"), rs.getString("lastname"),
                    rs.getInt("age")));
        }
        return users;
    }


    public User updateUser(User user) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateUserSQL);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.setLong(4, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return findUserById(user.getId());
    }

    public void deleteUser(Long userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteUserSQL);
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }


    private void closeResources() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
