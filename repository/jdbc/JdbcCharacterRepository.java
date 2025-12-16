package repository.jdbc;

import component.Character;
import repository.CharacterRepository;
import builder.CharacterBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcCharacterRepository implements CharacterRepository {
    private Connection conn;

    public JdbcCharacterRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Character> findAll() {
        List<Character> characters = new ArrayList<>();
        try {
            String sql = "SELECT * FROM characters ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                characters.add(parseCharacter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return characters;
    }

    @Override
    public List<Character> findByMediaId(int mediaId) {
        List<Character> characters = new ArrayList<>();
        try {
            String sql = "SELECT * FROM characters WHERE media_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, mediaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                characters.add(parseCharacter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return characters;
    }

    @Override
    public List<Character> findByUserId(int userId) {
        List<Character> characters = new ArrayList<>();
        try {
            String sql = """
                SELECT c.* FROM characters c
                JOIN media m ON c.media_id = m.id
                WHERE m.user_id = ?
                ORDER BY c.name
            """;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                characters.add(parseCharacter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return characters;
    }

    @Override
    public Character findById(int id) {
        try {
            String sql = "SELECT * FROM characters WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return parseCharacter(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(CharacterBuilder builder) {
        try {
            String additionalFieldsJson = builder.getAdditionalFields().isEmpty() ? ""
                    : builder.getAdditionalFields().entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .collect(Collectors.joining(";"));

            String sql = "INSERT INTO characters (name, age, birthday, gender, species, activity, description, hashtags, additional_fields, media_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, builder.getName());
            pstmt.setInt(2, builder.getAge());
            pstmt.setString(3, builder.getBirthday());
            pstmt.setString(4, builder.getGender());
            pstmt.setString(5, builder.getSpecies());
            pstmt.setString(6, builder.getActivity());
            pstmt.setString(7, builder.getDescription());
            pstmt.setString(8, builder.getHashtags());
            pstmt.setString(9, additionalFieldsJson);
            pstmt.setInt(10, builder.getMediaId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            String sql = "DELETE FROM characters WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Character parseCharacter(ResultSet rs) throws SQLException {
        Map<String, String> additionalFields = new HashMap<>();
        String fieldsStr = rs.getString("additional_fields");
        if (fieldsStr != null && !fieldsStr.isEmpty()) {
            for (String pair : fieldsStr.split(";")) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    additionalFields.put(kv[0], kv[1]);
                }
            }
        }

        return new Character(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("birthday"),
                rs.getString("gender"),
                rs.getString("species"),
                rs.getString("activity"),
                rs.getString("description"),
                rs.getString("hashtags"),
                additionalFields,
                rs.getInt("media_id")
        );
    }
}
