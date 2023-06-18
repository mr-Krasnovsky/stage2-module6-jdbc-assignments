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

    private static final String createUserSQL = "INSERT INTO MYUSERS (FIRSTNAME, LASTNAME, AGE) VALUES (?,?,?)";
    private static final String updateUserSQL = "UPDATE MYUSERS SET FIRSTNAME=?, LASTNAME=?, AGE=? WHERE ID=?";
    private static final String deleteUserSQL = "DELETE FROM MYUSERS WHERE ID=?";
    private static final String findUserByIdSQL = "SELECT*FROM MYUSERS WHERE ID=?";
    private static final String findUserByNameSQL = "SELECT*FROM MYUSERS WHERE FIRSTNAME=?";
    private static final String findAllUserSQL = "SELECT*FROM MYUSERS";


    public Long createUser(User user) {
        Long id = null;
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()){
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
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                user = build(resultSet);
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
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                user = build(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            st = connection.createStatement();
            ResultSet resultSet = st.executeQuery(findAllUserSQL);

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                User user = new User(id, firstname, lastname, age);
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return userList;
    }


    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setLong(4, user.getId());
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return findUserById(user.getId());
    }

    public void deleteUser(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUserSQL);
            ps.setLong(1, userId);
            ps.executeUpdate();
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

    private User build(ResultSet rs) throws SQLException {
        return User.builder().
                id(rs.getLong("id"))
                .firstName(rs.getString("firstname"))
                .lastName(rs.getString("lastname"))
                .age(rs.getInt("age"))
                .build();
    }
}
