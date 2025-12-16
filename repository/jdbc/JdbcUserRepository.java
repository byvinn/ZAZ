package repository.jdbc;

import component.User;
import repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserRepository implements UserRepository {
    private Connection conn;

    public JdbcUserRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public User findByCredentials(String username, String password) {
        try {
            String sql = "SELECT id, username, email, is_admin FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("is_admin") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User create(String username, String email, String password) {
        try {
            String sql = "INSERT INTO users (username, email, password, is_admin) VALUES (?, ?, ?, 0)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return new User(rs.getInt(1), username, email, false);
            }
        } catch (SQLException e) {
            System.out.println("Username already exists!");
        }
        return null;
    }

    @Override
    public List<User> findAllExcept(int userId) {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT id, username, is_admin FROM users WHERE is_admin = 0 OR (is_admin = 1 AND id != ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        null,
                        rs.getInt("is_admin") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void delete(int userId) {
        try {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAdmin(int userId) {
        try {
            String sql = "SELECT is_admin FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("is_admin") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
