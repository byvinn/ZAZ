package repository.jdbc;

import component.Media;
import component.Character;
import repository.CharacterRepository;
import repository.MediaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcMediaRepository implements MediaRepository {
    private Connection conn;
    private CharacterRepository charRepo;

    public JdbcMediaRepository(Connection conn, CharacterRepository charRepo) {
        this.conn = conn;
        this.charRepo = charRepo;
    }

    @Override
    public List<Media> findAll() {
        List<Media> mediaList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM media ORDER BY title";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Media media = createFromResultSet(rs);
                List<Character> characters = charRepo.findByMediaId(media.getId());
                characters.forEach(media::addCharacter);
                mediaList.add(media);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mediaList;
    }

    @Override
    public List<Media> findByUserId(int userId) {
        List<Media> mediaList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM media WHERE user_id = ? ORDER BY title";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                mediaList.add(createFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mediaList;
    }

    @Override
    public Media findById(int id) {
        try {
            String sql = "SELECT * FROM media WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Media media = createFromResultSet(rs);
                charRepo.findByMediaId(id).forEach(media::addCharacter);
                return media;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(String title, String description, String releaseDate, String type, String genre, String hashtags, int userId) {
        try {
            String sql = "INSERT INTO media (title, description, release_date, type, genre, hashtags, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, releaseDate);
            pstmt.setString(4, type);
            pstmt.setString(5, genre);
            pstmt.setString(6, hashtags);
            pstmt.setInt(7, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            String sql = "DELETE FROM media WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAuthorId(int mediaId) {
        try {
            String sql = "SELECT u.id FROM users u JOIN media m ON u.id = m.user_id WHERE m.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, mediaId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String getAuthorName(int mediaId) {
        try {
            String sql = "SELECT u.username FROM users u JOIN media m ON u.id = m.user_id WHERE m.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, mediaId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Media createFromResultSet(ResultSet rs) throws SQLException {
        return new Media(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("release_date"),
                rs.getString("type"),
                rs.getString("genre"),
                rs.getString("hashtags")
        );
    }
}
