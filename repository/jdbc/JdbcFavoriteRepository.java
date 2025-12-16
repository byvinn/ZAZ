package repository.jdbc;

import component.Media;
import component.Character;
import repository.FavoriteRepository;
import repository.CharacterRepository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcFavoriteRepository implements FavoriteRepository {
    private Connection conn;
    private CharacterRepository charRepo;

    public JdbcFavoriteRepository(Connection conn, CharacterRepository charRepo) {
        this.conn = conn;
        this.charRepo = charRepo;
    }

    @Override
    public List<Media> findFavoriteMedia(int userId) {
        List<Media> favorites = new ArrayList<>();
        try {
            String sql = """
                SELECT m.* FROM media m
                JOIN favorites f ON m.id = f.media_id
                WHERE f.user_id = ? AND f.media_id IS NOT NULL
                ORDER BY m.title
            """;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                favorites.add(new Media(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("release_date"),
                        rs.getString("type"),
                        rs.getString("genre"),
                        rs.getString("hashtags")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }

    @Override
    public List<Character> findFavoriteCharacters(int userId) {
        List<Character> favorites = new ArrayList<>();
        try {
            String sql = """
                SELECT c.id FROM characters c
                JOIN favorites f ON c.id = f.character_id
                WHERE f.user_id = ? AND f.character_id IS NOT NULL
                ORDER BY c.name
            """;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                favorites.add(charRepo.findById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }

    @Override
    public void addFavorite(int userId, int itemId, boolean isMedia) {
        try {
            String checkSql = isMedia
                    ? "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND media_id = ?"
                    : "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND character_id = ?";

            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, itemId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Already in favorites!");
                return;
            }

            String sql = isMedia
                    ? "INSERT INTO favorites (user_id, media_id) VALUES (?, ?)"
                    : "INSERT INTO favorites (user_id, character_id) VALUES (?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
            System.out.println("Added to favorites!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFavorite(int userId, int itemId, boolean isMedia) {
        try {
            String sql = isMedia
                    ? "DELETE FROM favorites WHERE user_id = ? AND media_id = ?"
                    : "DELETE FROM favorites WHERE user_id = ? AND character_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
            System.out.println("Removed from favorites!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countFavoriteMedia(int userId) {
        try {
            String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND media_id IS NOT NULL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countFavoriteCharacters(int userId) {
        try {
            String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND character_id IS NOT NULL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
