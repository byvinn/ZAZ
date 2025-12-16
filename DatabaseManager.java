import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

interface DatabaseConnectionProvider{
    Connection getConnection() throws SQLException;
}

class SQLiteConnection implements DatabaseConnectionProvider{
    private static final String DB_URL = "jdbc:sqlite:zaz.db";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

class DatabaseInitializer {
    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void initialize() throws SQLException {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                is_admin INTEGER NOT NULL DEFAULT 0
            )
        """;

        String createMediaTable = """
            CREATE TABLE IF NOT EXISTS media (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                release_date TEXT,
                type TEXT NOT NULL,
                genre TEXT NOT NULL,
                hashtags TEXT,
                user_id INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """;

        String createCharactersTable = """
            CREATE TABLE IF NOT EXISTS characters (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                age INTEGER,
                birthday TEXT,
                gender TEXT,
                species TEXT,
                activity TEXT,
                description TEXT,
                hashtags TEXT,
                additional_fields TEXT,
                media_id INTEGER,
                FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
            )
        """;

        String createFavoritesTable = """
            CREATE TABLE IF NOT EXISTS favorites (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                media_id INTEGER,
                character_id INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
                FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createMediaTable);
            stmt.execute(createCharactersTable);
            stmt.execute(createFavoritesTable);

            String checkAdmin = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO users (username, email, password, is_admin) VALUES ('admin', 'admin@system.com', 'admin', 1)";
                stmt.execute(insertAdmin);
            }
        }
    }
}

class DatabaseManager {
    private Connection connection;

    public DatabaseManager(DatabaseConnectionProvider database) {
        try {
            connection = database.getConnection();
            new DatabaseInitializer(connection).initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Model Classes
class User {
    private int id;
    private String username;
    private String email;
    private boolean isAdmin;

    public User(int id, String username, String email, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isAdmin() { return isAdmin; }
}

class Character {
    private int id;
    private String name;
    private int age;
    private String birthday;
    private String gender;
    private String species;
    private String activity;
    private String description;
    private String hashtags;
    private Map<String, String> additionalFields;
    private int mediaId;

    public Character(int id, String name, int age, String birthday, String gender,
                     String species, String activity, String description, String hashtags,
                     Map<String, String> additionalFields, int mediaId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.birthday = birthday;
        this.gender = gender;
        this.species = species;
        this.activity = activity;
        this.description = description;
        this.hashtags = hashtags;
        this.additionalFields = additionalFields != null ? additionalFields : new HashMap<>();
        this.mediaId = mediaId;
    }

    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Age: ").append(age).append("\n");
        sb.append("Birthday: ").append(birthday).append("\n");
        sb.append("Gender: ").append(gender).append("\n");
        sb.append("Species: ").append(species).append("\n");
        sb.append("Activity: ").append(activity).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Hashtags: ").append(hashtags).append("\n");

        if (!additionalFields.isEmpty()) {
            additionalFields.forEach((key, value) ->
                    sb.append("  ").append(key).append(": ").append(value).append("\n"));
        }
        return sb.toString();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getBirthday() { return birthday; }
    public String getSpecies() { return species; }
    public String getActivity() { return activity; }
    public String getHashtags() { return hashtags; }
    public int getMediaId() { return mediaId; }
}

class CharacterBuilder {
    private String name;
    private int age;
    private String birthday;
    private String gender;
    private String species;
    private String activity;
    private String description;
    private String hashtags;
    private Map<String, String> additionalFields = new HashMap<>();
    private int mediaId;

    public CharacterBuilder setName(String name) { this.name = name; return this; }
    public CharacterBuilder setAge(int age) { this.age = age; return this; }
    public CharacterBuilder setBirthday(String birthday) { this.birthday = birthday; return this; }
    public CharacterBuilder setGender(String gender) { this.gender = gender; return this; }
    public CharacterBuilder setSpecies(String species) { this.species = species; return this; }
    public CharacterBuilder setActivity(String activity) { this.activity = activity; return this; }
    public CharacterBuilder setDescription(String description) { this.description = description; return this; }
    public CharacterBuilder setHashtags(String hashtags) { this.hashtags = hashtags; return this; }
    public CharacterBuilder addAdditionalField(String key, String value) { this.additionalFields.put(key, value); return this; }
    public CharacterBuilder setMediaId(int mediaId) { this.mediaId = mediaId; return this; }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getBirthday() { return birthday; }
    public String getGender() { return gender; }
    public String getSpecies() { return species; }
    public String getActivity() { return activity; }
    public String getDescription() { return description; }
    public String getHashtags() { return hashtags; }
    public Map<String, String> getAdditionalFields() { return additionalFields; }
    public int getMediaId() { return mediaId; }

    public Character build(int id) {
        return new Character(id, name, age, birthday, gender, species, activity,
                description, hashtags, additionalFields, mediaId);
    }
}

class Media {
    private int id;
    private String title;
    private String description;
    private String releaseDate;
    private String type;
    private String genre;
    private String hashtags;
    private List<Character> characters;

    public Media(int id, String title, String description, String releaseDate,
                 String type, String genre, String hashtags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.type = type;
        this.genre = genre;
        this.hashtags = hashtags;
        this.characters = new ArrayList<>();
    }

    public void addCharacter(Character character) {
        characters.add(character);
    }

    public void display() {
        System.out.printf("%s | %s | Characters: %d\n", title, type, characters.size());
    }

    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(title).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Release Date: ").append(releaseDate).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Genre: ").append(genre).append("\n");
        sb.append("Hashtags: ").append(hashtags).append("\n");
        sb.append("Characters:\n");
        for (Character ch : characters) {
            sb.append("  - ").append(ch.getName()).append("\n");
        }
        return sb.toString();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getReleaseDate() { return releaseDate; }
    public String getType() { return type; }
    public String getGenre() { return genre; }
    public String getHashtags() { return hashtags; }
    public List<Character> getCharacters() { return characters; }
}

// ============================================================================
// REPOSITORY INTERFACES (SOLID - Dependency Inversion)
// ============================================================================

interface MediaRepository {
    List<Media> findAll();
    List<Media> findByUserId(int userId);
    Media findById(int id);
    void save(String title, String description, String releaseDate, String type, String genre, String hashtags, int userId);
    void delete(int id);
    int getAuthorId(int mediaId);
    String getAuthorName(int mediaId);
}

interface CharacterRepository {
    List<Character> findAll();
    List<Character> findByMediaId(int mediaId);
    List<Character> findByUserId(int userId);
    Character findById(int id);
    void save(CharacterBuilder builder);
    void delete(int id);
}

interface UserRepository {
    User findByCredentials(String username, String password);
    User create(String username, String email, String password);
    List<User> findAllExcept(int userId);
    void delete(int userId);
    boolean isAdmin(int userId);
}

interface FavoriteRepository {
    List<Media> findFavoriteMedia(int userId);
    List<Character> findFavoriteCharacters(int userId);
    void addFavorite(int userId, int itemId, boolean isMedia);
    void removeFavorite(int userId, int itemId, boolean isMedia);
    int countFavoriteMedia(int userId);
    int countFavoriteCharacters(int userId);
}

// ============================================================================
// REPOSITORY IMPLEMENTATIONS
// ============================================================================

class JdbcMediaRepository implements MediaRepository {
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

class JdbcCharacterRepository implements CharacterRepository {
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

class JdbcUserRepository implements UserRepository {
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

class JdbcFavoriteRepository implements FavoriteRepository {
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

// ============================================================================
// STRATEGY PATTERN FOR FILTERS (replaces Decorator)
// ============================================================================

interface FilterStrategy<T> {
    List<T> apply(List<T> items);
}

// Composite filter to combine multiple strategies
class FilterComposite<T> implements FilterStrategy<T> {
    private List<FilterStrategy<T>> filters = new ArrayList<>();

    public void add(FilterStrategy<T> filter) {
        filters.add(filter);
    }

    @Override
    public List<T> apply(List<T> items) {
        List<T> result = items;
        for (FilterStrategy<T> filter : filters) {
            result = filter.apply(result);
        }
        return result;
    }
}

// Media Filter Strategies
class TypeFilterStrategy implements FilterStrategy<Media> {
    private Set<String> types;

    public TypeFilterStrategy(Set<String> types) {
        this.types = types;
    }

    @Override
    public List<Media> apply(List<Media> items) {
        return items.stream()
                .filter(m -> types.contains(m.getType()))
                .collect(Collectors.toList());
    }
}

class GenreFilterStrategy implements FilterStrategy<Media> {
    private Set<String> genres;

    public GenreFilterStrategy(Set<String> genres) {
        this.genres = genres;
    }

    @Override
    public List<Media> apply(List<Media> items) {
        return items.stream()
                .filter(m -> genres.contains(m.getGenre()))
                .collect(Collectors.toList());
    }
}

class ReleaseDateFilterStrategy implements FilterStrategy<Media> {
    private String releaseDate;

    public ReleaseDateFilterStrategy(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public List<Media> apply(List<Media> items) {
        return items.stream()
                .filter(m -> m.getReleaseDate() != null && m.getReleaseDate().contains(releaseDate))
                .collect(Collectors.toList());
    }
}

class HashtagMediaFilterStrategy implements FilterStrategy<Media> {
    private String hashtag;

    public HashtagMediaFilterStrategy(String hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public List<Media> apply(List<Media> items) {
        return items.stream()
                .filter(m -> m.getHashtags() != null && m.getHashtags().toLowerCase().contains(hashtag.toLowerCase()))
                .collect(Collectors.toList());
    }
}

// Character Filter Strategies
class AgeCharacterFilterStrategy implements FilterStrategy<Character> {
    private String ageText;

    public AgeCharacterFilterStrategy(String ageText) {
        this.ageText = ageText;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> String.valueOf(c.getAge()).contains(ageText))
                .collect(Collectors.toList());
    }
}

class BirthdayCharacterFilterStrategy implements FilterStrategy<Character> {
    private String birthday;

    public BirthdayCharacterFilterStrategy(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getBirthday() != null && c.getBirthday().contains(birthday))
                .collect(Collectors.toList());
    }
}

class SpeciesCharacterFilterStrategy implements FilterStrategy<Character> {
    private String species;

    public SpeciesCharacterFilterStrategy(String species) {
        this.species = species;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getSpecies() != null && c.getSpecies().toLowerCase().contains(species.toLowerCase()))
                .collect(Collectors.toList());
    }
}

class ActivityCharacterFilterStrategy implements FilterStrategy<Character> {
    private String activity;

    public ActivityCharacterFilterStrategy(String activity) {
        this.activity = activity;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getActivity() != null && c.getActivity().toLowerCase().contains(activity.toLowerCase()))
                .collect(Collectors.toList());
    }
}

class HashtagCharacterFilterStrategy implements FilterStrategy<Character> {
    private String hashtag;

    public HashtagCharacterFilterStrategy(String hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getHashtags() != null && c.getHashtags().toLowerCase().contains(hashtag.toLowerCase()))
                .collect(Collectors.toList());
    }
}

// ============================================================================
// SERVICE LAYER
// ============================================================================

class MediaService {
    private MediaRepository mediaRepository;
    private CharacterRepository characterRepository;

    public MediaService(MediaRepository mediaRepository, CharacterRepository characterRepository) {
        this.mediaRepository = mediaRepository;
        this.characterRepository = characterRepository;
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public List<Media> getMediaByUser(int userId) {
        return mediaRepository.findByUserId(userId);
    }

    public Media getMediaById(int id) {
        return mediaRepository.findById(id);
    }

    public List<Media> getFilteredMedia(FilterStrategy<Media> filter) {
        return filter.apply(mediaRepository.findAll());
    }

    public void createMedia(String title, String description, String releaseDate, String type, String genre, String hashtags, int userId) {
        mediaRepository.save(title, description, releaseDate, type, genre, hashtags, userId);
    }

    public void deleteMedia(int id) {
        mediaRepository.delete(id);
    }

    public int getAuthorId(int mediaId) {
        return mediaRepository.getAuthorId(mediaId);
    }

    public String getAuthorName(int mediaId) {
        return mediaRepository.getAuthorName(mediaId);
    }
}

class CharacterService {
    private CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public List<Character> getAllCharacters() {
        return characterRepository.findAll();
    }

    public List<Character> getCharactersByUser(int userId) {
        return characterRepository.findByUserId(userId);
    }

    public Character getCharacterById(int id) {
        return characterRepository.findById(id);
    }

    public List<Character> getFilteredCharacters(FilterStrategy<Character> filter) {
        return filter.apply(characterRepository.findAll());
    }

    public void createCharacter(CharacterBuilder builder) {
        characterRepository.save(builder);
    }

    public void deleteCharacter(int id) {
        characterRepository.delete(id);
    }
}

class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        return userRepository.findByCredentials(username, password);
    }

    public User register(String username, String email, String password) {
        return userRepository.create(username, email, password);
    }

    public List<User> getAllUsersExcept(int userId) {
        return userRepository.findAllExcept(userId);
    }

    public void deleteUser(int userId) {
        userRepository.delete(userId);
    }

    public boolean isAdmin(int userId) {
        return userRepository.isAdmin(userId);
    }
}

class FavoriteService {
    private FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public List<Media> getFavoriteMedia(int userId) {
        return favoriteRepository.findFavoriteMedia(userId);
    }

    public List<Character> getFavoriteCharacters(int userId) {
        return favoriteRepository.findFavoriteCharacters(userId);
    }

    public void addFavorite(int userId, int itemId, boolean isMedia) {
        favoriteRepository.addFavorite(userId, itemId, isMedia);
    }

    public void removeFavorite(int userId, int itemId, boolean isMedia) {
        favoriteRepository.removeFavorite(userId, itemId, isMedia);
    }

    public int countFavoriteMedia(int userId) {
        return favoriteRepository.countFavoriteMedia(userId);
    }

    public int countFavoriteCharacters(int userId) {
        return favoriteRepository.countFavoriteCharacters(userId);
    }
}

// ============================================================================
// FACADE PATTERN
// ============================================================================

class MediaManagementFacade {
    private MediaService mediaService;
    private CharacterService characterService;
    private UserService userService;
    private FavoriteService favoriteService;

    public MediaManagementFacade(MediaService mediaService, CharacterService characterService,
                                 UserService userService, FavoriteService favoriteService) {
        this.mediaService = mediaService;
        this.characterService = characterService;
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    // Media operations
    public List<Media> browseMedia(FilterStrategy<Media> filter) {
        return mediaService.getFilteredMedia(filter);
    }

    public Media viewMediaDetails(int mediaId) {
        return mediaService.getMediaById(mediaId);
    }

    public void createMedia(String title, String description, String releaseDate, String type, String genre, String hashtags, int userId) {
        mediaService.createMedia(title, description, releaseDate, type, genre, hashtags, userId);
    }

    public void deleteMedia(int id) {
        mediaService.deleteMedia(id);
    }

    // Character operations
    public List<Character> browseCharacters(FilterStrategy<Character> filter) {
        return characterService.getFilteredCharacters(filter);
    }

    public Character viewCharacterDetails(int characterId) {
        return characterService.getCharacterById(characterId);
    }

    public void createCharacter(CharacterBuilder builder) {
        characterService.createCharacter(builder);
    }

    public void deleteCharacter(int id) {
        characterService.deleteCharacter(id);
    }

    // User operations
    public User login(String username, String password) {
        return userService.login(username, password);
    }

    public User register(String username, String email, String password) {
        return userService.register(username, email, password);
    }

    public void deleteUser(int userId) {
        userService.deleteUser(userId);
    }

    // Favorite operations
    public List<Media> getFavoriteMedia(int userId) {
        return favoriteService.getFavoriteMedia(userId);
    }

    public List<Character> getFavoriteCharacters(int userId) {
        return favoriteService.getFavoriteCharacters(userId);
    }

    public void addToFavorites(int userId, int itemId, boolean isMedia) {
        favoriteService.addFavorite(userId, itemId, isMedia);
    }

    public void removeFromFavorites(int userId, int itemId, boolean isMedia) {
        favoriteService.removeFavorite(userId, itemId, isMedia);
    }

    // Profile information
    public List<Media> getUserMedia(int userId) {
        return mediaService.getMediaByUser(userId);
    }

    public List<Character> getUserCharacters(int userId) {
        return characterService.getCharactersByUser(userId);
    }

    public String getMediaAuthorName(int mediaId) {
        return mediaService.getAuthorName(mediaId);
    }

    public int getMediaAuthorId(int mediaId) {
        return mediaService.getAuthorId(mediaId);
    }
}

// ============================================================================
// MEMENTO PATTERN FOR SEARCH HISTORY
// ============================================================================

class SearchMemento {
    private final String searchQuery;

    public SearchMemento(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}

class SearchHistory {
    private LinkedList<SearchMemento> history = new LinkedList<>();
    private static final int MAX_HISTORY = 5;

    public void addSearch(String query) {
        history.removeIf(m -> m.getSearchQuery().equalsIgnoreCase(query));

        if (history.size() >= MAX_HISTORY) {
            history.removeFirst();
        }
        history.addLast(new SearchMemento(query));
    }

    public List<String> getHistory() {
        return history.stream()
                .map(SearchMemento::getSearchQuery)
                .collect(Collectors.toList());
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }
}

// ============================================================================
// CONSTANTS
// ============================================================================

class MediaType {
    public static final String[] TYPES = {"CARTOON", "GAME", "ANIME", "SERIES", "MOVIE", "COMICS"};
}

class MediaGenre {
    public static final String[] GENRES = {"Adventure", "Romance", "Fiction", "Crime", "Drama",
            "Fantasy", "Monsters", "Biography", "Horror", "Thriller"};
}

// ============================================================================
// UI/CONTROLLER LAYER
// ============================================================================

class MediaManagementSystem {
    private DatabaseConnectionProvider provider;
    private DatabaseManager dbManager;
    private Scanner scanner;
    private User currentUser;
    private SearchHistory searchHistory;
    private MediaManagementFacade facade;

    public MediaManagementSystem() {
        provider = new SQLiteConnection();
        dbManager = new DatabaseManager(provider);
        scanner = new Scanner(System.in);
        searchHistory = new SearchHistory();
        initializeFacade();
    }

    private void initializeFacade() {
        Connection conn = dbManager.getConnection();

        CharacterRepository charRepo = new JdbcCharacterRepository(conn);
        MediaRepository mediaRepo = new JdbcMediaRepository(conn, charRepo);
        UserRepository userRepo = new JdbcUserRepository(conn);
        FavoriteRepository favRepo = new JdbcFavoriteRepository(conn, charRepo);

        MediaService mediaService = new MediaService(mediaRepo, charRepo);
        CharacterService characterService = new CharacterService(charRepo);
        UserService userService = new UserService(userRepo);
        FavoriteService favoriteService = new FavoriteService(favRepo);

        facade = new MediaManagementFacade(mediaService, characterService, userService, favoriteService);
    }

    public void start() {
        System.out.println("\n" +
                "                                                     \n" +
                " ██████████████████ ██████████████ ██████████████████\n" +
                " ██░░░░░░░░░░░░░░██ ██░░░░░░░░░░██ ██░░░░░░░░░░░░░░██\n" +
                " ████████████░░░░██ ██░░██████░░██ ████████████░░░░██\n" +
                "         ████░░████ ██░░██  ██░░██         ████░░████\n" +
                "       ████░░████   ██░░██████░░██       ████░░████\n" +
                "     ████░░████     ██░░░░░░░░░░██     ████░░████ \n" +
                "   ████░░████       ██░░██████░░██   ████░░████\n" +
                " ████░░████         ██░░██  ██░░██ ████░░████         \n" +
                " ██░░░░████████████ ██░░██  ██░░██ ██░░░░████████████ \n" +
                " ██░░░░░░░░░░░░░░██ ██░░██  ██░░██ ██░░░░░░░░░░░░░░██ \n" +
                " ██████████████████ ██████  ██████ ██████████████████\n" +
                "                                                        ");

        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("\nChoose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    if (login()) {
                        if (currentUser.isAdmin()) {
                            adminMenu();
                        } else {
                            userMenu();
                        }
                    }
                }
                case 2 -> register();
                case 3 -> {
                    dbManager.close();
                    System.out.println("Goodbye!");
                    return;
                }
            }
        }
    }

    private boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = facade.login(username, password);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome " + username + "!");
            return true;
        } else {
            System.out.println("Invalid credentials!");
            return false;
        }
    }

    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = facade.register(username, email, password);
        if (currentUser != null) {
            System.out.println("Registration successful! Welcome " + username + "!");
            userMenu();
        }
    }

    private void userMenu() {
        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ User Menu ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Profile");
            System.out.println("2. Create media");
            System.out.println("3. Create character");
            System.out.println("4. Browse media");
            System.out.println("5. Browse characters");
            System.out.println("6. Search");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> showProfile();
                case 2 -> createMedia();
                case 3 -> createCharacter();
                case 4 -> browseMedia();
                case 5 -> browseCharacters();
                case 6 -> search();
                case 7 -> { return; }
            }
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Admin Menu ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Profile");
            System.out.println("2. Create media");
            System.out.println("3. Create character");
            System.out.println("4. Manage content");
            System.out.println("5. Manage users");
            System.out.println("6. Browse media");
            System.out.println("7. Browse characters");
            System.out.println("8. Search");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> showProfile();
                case 2 -> createMedia();
                case 3 -> createCharacter();
                case 4 -> manageContent();
                case 5 -> manageUsers();
                case 6 -> browseMedia();
                case 7 -> browseCharacters();
                case 8 -> search();
                case 9 -> { return; }
            }
        }
    }

    private void showProfile() {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Profile ▀▄▀▄▀▄▀▄▀▄▀▄");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());

        if (!currentUser.isAdmin()) {
            System.out.println("\n1. My favorites");
            System.out.println("2. My media");
            System.out.println("3. My characters");
            System.out.println("4. Go back");
        } else {
            System.out.println("\n1. My media");
            System.out.println("2. My characters");
            System.out.println("3. Go back");
        }

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (!currentUser.isAdmin()) {
            switch (choice) {
                case 1 -> showFavorites();
                case 2 -> showMyMedia();
                case 3 -> showMyCharacters();
            }
        } else {
            switch (choice) {
                case 1 -> showMyMedia();
                case 2 -> showMyCharacters();
            }
        }
    }

    private void showFavorites() {
        System.out.println("\n1. Favorite Media");
        System.out.println("2. Favorite Characters");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            showFavoriteMedia();
        } else if (choice == 2) {
            showFavoriteCharacters();
        }
    }

    private void showFavoriteMedia() {
        List<Media> favorites = facade.getFavoriteMedia(currentUser.getId());

        if (favorites.isEmpty()) {
            System.out.println("No favorite media.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Favorite Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < favorites.size(); i++) {
            System.out.println(i + 1 + ". " + favorites.get(i).getTitle() + " | " + favorites.get(i).getType());
        }

        System.out.print("\nChoose media to remove (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= favorites.size()) {
            facade.removeFromFavorites(currentUser.getId(), favorites.get(choice - 1).getId(), true);
        } else {
            showProfile();
        }
    }

    private void showFavoriteCharacters() {
        List<Character> favorites = facade.getFavoriteCharacters(currentUser.getId());

        if (favorites.isEmpty()) {
            System.out.println("No favorite characters.");
            return;
        }

        System.out.println("\n=▀▄▀▄▀▄▀▄▀▄▀▄ Favorite Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < favorites.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, favorites.get(i).getName());
        }

        System.out.print("\nChoose character to remove (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= favorites.size()) {
            facade.removeFromFavorites(currentUser.getId(), favorites.get(choice - 1).getId(), false);
        } else {
            showProfile();
        }
    }

    private void showMyMedia() {
        List<Media> myMedia = facade.getUserMedia(currentUser.getId());

        if (myMedia.isEmpty()) {
            System.out.println("You haven't created any media yet.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ My Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < myMedia.size(); i++) {
            System.out.printf("%d. %s | %s\n", i + 1, myMedia.get(i).getTitle(), myMedia.get(i).getType());
        }

        System.out.print("\nChoose media to delete (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= myMedia.size()) {
            facade.deleteMedia(myMedia.get(choice - 1).getId());
            System.out.println("Media deleted successfully!");
        } else {
            showProfile();
        }
    }

    private void showMyCharacters() {
        List<Character> myCharacters = facade.getUserCharacters(currentUser.getId());

        if (myCharacters.isEmpty()) {
            System.out.println("You haven't created any characters yet.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ My Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < myCharacters.size(); i++) {
            System.out.println((i + 1) + ". " + myCharacters.get(i).getName());
        }

        System.out.print("\nChoose character to delete (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= myCharacters.size()) {
            facade.deleteCharacter(myCharacters.get(choice - 1).getId());
            System.out.println("Character deleted successfully!");
        } else {
            showProfile();
        }
    }

    private void createMedia() {
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Release Date (YYYY-MM-DD): ");
        String releaseDate = scanner.nextLine();

        System.out.println("\nAvailable types:");
        for (int i = 0; i < MediaType.TYPES.length; i++) {
            System.out.printf("%d. %s\n", i + 1, MediaType.TYPES[i]);
        }
        System.out.print("Choose type: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();
        String type = (typeChoice > 0 && typeChoice <= MediaType.TYPES.length) ? MediaType.TYPES[typeChoice - 1] : "OTHER";

        System.out.println("\nAvailable genres:");
        for (int i = 0; i < MediaGenre.GENRES.length; i++) {
            System.out.printf("%d. %s\n", i + 1, MediaGenre.GENRES[i]);
        }
        System.out.print("Choose genre: ");
        int genreChoice = scanner.nextInt();
        scanner.nextLine();
        String genre = (genreChoice > 0 && genreChoice <= MediaGenre.GENRES.length) ? MediaGenre.GENRES[genreChoice - 1] : "Other";

        System.out.print("Hashtags (comma-separated): ");
        String hashtags = scanner.nextLine();

        facade.createMedia(title, description, releaseDate, type, genre, hashtags, currentUser.getId());
        System.out.println("Media created successfully!");
    }

    private void createCharacter() {
        List<Media> mediaList = facade.browseMedia(items -> items);
        if (mediaList.isEmpty()) {
            System.out.println("No media available. Create media first!");
            return;
        }

        System.out.println("\nAvailable Media:");
        for (int i = 0; i < mediaList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, mediaList.get(i).getTitle());
        }

        System.out.print("Choose media number: ");
        int mediaChoice = scanner.nextInt();
        scanner.nextLine();

        if (mediaChoice < 1 || mediaChoice > mediaList.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Media selectedMedia = mediaList.get(mediaChoice - 1);
        CharacterBuilder builder = new CharacterBuilder();

        System.out.print("Character Name: ");
        builder.setName(scanner.nextLine());

        System.out.print("Age: ");
        builder.setAge(scanner.nextInt());
        scanner.nextLine();

        System.out.print("Birthday (YYYY-MM-DD): ");
        builder.setBirthday(scanner.nextLine());

        System.out.print("Gender: ");
        builder.setGender(scanner.nextLine());

        System.out.print("Species: ");
        builder.setSpecies(scanner.nextLine());

        System.out.print("Activity: ");
        builder.setActivity(scanner.nextLine());

        System.out.print("Add additional fields? (yes/no): ");
        String addFields = scanner.nextLine();

        if (addFields.equalsIgnoreCase("yes")) {
            while (true) {
                System.out.print("Field name (0 to finish): ");
                String fieldName = scanner.nextLine();
                if (fieldName.equals("0")) break;

                System.out.print("Field value: ");
                String fieldValue = scanner.nextLine();
                builder.addAdditionalField(fieldName, fieldValue);
            }
        }

        System.out.print("Description: ");
        builder.setDescription(scanner.nextLine());

        System.out.print("Hashtags (comma-separated): ");
        builder.setHashtags(scanner.nextLine());

        builder.setMediaId(selectedMedia.getId());

        facade.createCharacter(builder);
        System.out.println("Character created successfully!");
    }

    private void browseMedia() {
        FilterComposite<Media> filter = new FilterComposite<>();
        List<Media> mediaList = facade.browseMedia(filter);
        browseMediaWithFilter(mediaList, filter);
    }

    private void browseMediaWithFilter(List<Media> mediaList, FilterComposite<Media> currentFilter) {
        if (mediaList.isEmpty()) {
            System.out.println("No media available.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ All Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < mediaList.size(); i++) {
            System.out.printf("%d. ", i + 1);
            mediaList.get(i).display();
        }

        System.out.println("\n1. Show details");
        System.out.println("2. Edit filters");
        System.out.println("3. Go back to menu");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Choose media number: ");
                int mediaChoice = scanner.nextInt();
                scanner.nextLine();
                if (mediaChoice > 0 && mediaChoice <= mediaList.size()) {
                    showMediaDetails(mediaList.get(mediaChoice - 1));
                }
            }
            case 2 -> editMediaFilters(currentFilter);
        }
    }

    private void showMediaDetails(Media media) {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Media Details ▀▄▀▄▀▄▀▄▀▄▀▄");
        System.out.println(media.getDetails());

        String authorName = facade.getMediaAuthorName(media.getId());
        int authorId = facade.getMediaAuthorId(media.getId());
        System.out.println("Author: " + authorName);

        System.out.println("\n1. Add to favorites");
        System.out.println("2. Show all characters");
        System.out.println("3. View author's profile");
        System.out.println("4. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                facade.addToFavorites(currentUser.getId(), media.getId(), true);
                showMediaDetails(media);
            }
            case 2 -> {
                showMediaCharacters(media);
                showMediaDetails(media);
            }
            case 3 -> viewAuthorProfile(authorId, authorName);
        }
    }

    private void showMediaCharacters(Media media) {
        if (media.getCharacters().isEmpty()) {
            System.out.println("No characters in this media.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < media.getCharacters().size(); i++) {
            System.out.printf("%d. %s\n", i + 1, media.getCharacters().get(i).getName());
        }

        System.out.print("\nView character details (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= media.getCharacters().size()) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println(media.getCharacters().get(choice - 1).getDetails());
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void editMediaFilters(FilterComposite<Media> currentFilter) {
        FilterComposite<Media> newFilter = new FilterComposite<>();

        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Filters ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Add Type filter");
            System.out.println("2. Add Genre filter");
            System.out.println("3. Add Release date filter");
            System.out.println("4. Add Hashtag filter");
            System.out.println("0. Apply filters");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                List<Media> filtered = facade.browseMedia(newFilter);
                browseMediaWithFilter(filtered, newFilter);
                return;
            }

            switch (choice) {
                case 1 -> {
                    System.out.println("\nTypes:");
                    for (int i = 0; i < MediaType.TYPES.length; i++) {
                        System.out.printf("%d. %s\n", i + 1, MediaType.TYPES[i]);
                    }
                    System.out.print("Choose types (comma-separated): ");
                    String input = scanner.nextLine();
                    Set<String> types = Arrays.stream(input.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .filter(i -> i > 0 && i <= MediaType.TYPES.length)
                            .map(i -> MediaType.TYPES[i - 1])
                            .collect(Collectors.toSet());
                    if (!types.isEmpty()) {
                        newFilter.add(new TypeFilterStrategy(types));
                    }
                }
                case 2 -> {
                    System.out.println("\nGenres:");
                    for (int i = 0; i < MediaGenre.GENRES.length; i++) {
                        System.out.printf("%d. %s\n", i + 1, MediaGenre.GENRES[i]);
                    }
                    System.out.print("Choose genres (comma-separated): ");
                    String input = scanner.nextLine();
                    Set<String> genres = Arrays.stream(input.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .filter(i -> i > 0 && i <= MediaGenre.GENRES.length)
                            .map(i -> MediaGenre.GENRES[i - 1])
                            .collect(Collectors.toSet());
                    if (!genres.isEmpty()) {
                        newFilter.add(new GenreFilterStrategy(genres));
                    }
                }
                case 3 -> {
                    System.out.print("Enter release date (YYYY or YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    newFilter.add(new ReleaseDateFilterStrategy(date));
                }
                case 4 -> {
                    System.out.print("Enter hashtag: ");
                    String hashtag = scanner.nextLine();
                    newFilter.add(new HashtagMediaFilterStrategy(hashtag));
                }
            }
        }
    }

    private void browseCharacters() {
        FilterComposite<Character> filter = new FilterComposite<>();
        List<Character> characters = facade.browseCharacters(filter);
        browseCharactersWithFilter(characters, filter);
    }

    private void browseCharactersWithFilter(List<Character> characters, FilterComposite<Character> currentFilter) {
        if (characters.isEmpty()) {
            System.out.println("No characters available.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ All Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < characters.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, characters.get(i).getName());
        }

        System.out.println("\n1. Show details");
        System.out.println("2. Edit filters");
        System.out.println("3. Go back to menu");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Choose character number: ");
                int charChoice = scanner.nextInt();
                scanner.nextLine();
                if (charChoice > 0 && charChoice <= characters.size()) {
                    showCharacterDetails(characters.get(charChoice - 1));
                }
            }
            case 2 -> editCharacterFilters(currentFilter);
        }
    }

    private void showCharacterDetails(Character character) {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
        System.out.println(character.getDetails());

        Media media = facade.viewMediaDetails(character.getMediaId());
        if (media != null) {
            System.out.println("From Media: " + media.getTitle());
        }

        String authorName = facade.getMediaAuthorName(character.getMediaId());
        int authorId = facade.getMediaAuthorId(character.getMediaId());
        System.out.println("Author: " + authorName);

        System.out.println("\n1. Add to favorites");
        System.out.println("2. View author's profile");
        System.out.println("3. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                facade.addToFavorites(currentUser.getId(), character.getId(), false);
                showCharacterDetails(character);
            }
            case 2 -> viewAuthorProfile(authorId, authorName);
        }
    }

    private void editCharacterFilters(FilterComposite<Character> currentFilter) {
        FilterComposite<Character> newFilter = new FilterComposite<>();

        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Filters ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Add Age filter");
            System.out.println("2. Add Birthday filter");
            System.out.println("3. Add Species filter");
            System.out.println("4. Add Activity filter");
            System.out.println("5. Add Hashtag filter");
            System.out.println("0. Apply filters");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                List<Character> filtered = facade.browseCharacters(newFilter);
                browseCharactersWithFilter(filtered, newFilter);
                return;
            }

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter age: ");
                    String age = scanner.nextLine();
                    newFilter.add(new AgeCharacterFilterStrategy(age));
                }
                case 2 -> {
                    System.out.print("Enter birthday: ");
                    String birthday = scanner.nextLine();
                    newFilter.add(new BirthdayCharacterFilterStrategy(birthday));
                }
                case 3 -> {
                    System.out.print("Enter species: ");
                    String species = scanner.nextLine();
                    newFilter.add(new SpeciesCharacterFilterStrategy(species));
                }
                case 4 -> {
                    System.out.print("Enter activity: ");
                    String activity = scanner.nextLine();
                    newFilter.add(new ActivityCharacterFilterStrategy(activity));
                }
                case 5 -> {
                    System.out.print("Enter hashtag: ");
                    String hashtag = scanner.nextLine();
                    newFilter.add(new HashtagCharacterFilterStrategy(hashtag));
                }
            }
        }
    }

    private void search() {
        if (searchHistory.hasHistory()) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Search History ▀▄▀▄▀▄▀▄▀▄▀▄");
            List<String> history = searchHistory.getHistory();
            for (int i = 0; i < history.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, history.get(i));
            }
        }

        System.out.print("\nChoose from search history (1-5) or enter new search: ");
        String input = scanner.nextLine();

        String searchQuery;
        try {
            int historyChoice = Integer.parseInt(input);
            if (historyChoice > 0 && historyChoice <= searchHistory.getHistory().size()) {
                searchQuery = searchHistory.getHistory().get(historyChoice - 1);
            } else {
                searchQuery = input;
            }
        } catch (NumberFormatException e) {
            searchQuery = input;
        }

        searchHistory.addSearch(searchQuery);
        performSearch(searchQuery);
    }

    private void performSearch(String query) {
        String lowerQuery = query.toLowerCase();

        List<Media> allMedia = facade.browseMedia(items -> items);
        List<Media> foundMedia = allMedia.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        List<Character> allCharacters = facade.browseCharacters(items -> items);
        List<Character> foundCharacters = allCharacters.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Search Results ▀▄▀▄▀▄▀▄▀▄▀▄");
        boolean found = false;

        if (!foundMedia.isEmpty()) {
            found = true;
            for (int i = 0; i < foundMedia.size(); i++) {
                System.out.printf("[Media %d] %s | %s\n", i + 1, foundMedia.get(i).getTitle(), foundMedia.get(i).getType());
            }
        }

        if (!foundCharacters.isEmpty()) {
            found = true;
            for (int i = 0; i < foundCharacters.size(); i++) {
                System.out.printf("[Character %d] %s\n", i + 1, foundCharacters.get(i).getName());
            }
        }

        if (!found) {
            System.out.println("No exact matches found.");
            return;
        }

        System.out.println("\n1. View media details");
        System.out.println("2. View character details");
        System.out.println("3. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1 && !foundMedia.isEmpty()) {
            System.out.print("Choose media number: ");
            int mediaChoice = scanner.nextInt();
            scanner.nextLine();
            if (mediaChoice > 0 && mediaChoice <= foundMedia.size()) {
                Media selectedMedia = facade.viewMediaDetails(foundMedia.get(mediaChoice - 1).getId());
                showMediaDetails(selectedMedia);
            }
        } else if (choice == 2 && !foundCharacters.isEmpty()) {
            System.out.print("Choose character number: ");
            int charChoice = scanner.nextInt();
            scanner.nextLine();
            if (charChoice > 0 && charChoice <= foundCharacters.size()) {
                showCharacterDetails(foundCharacters.get(charChoice - 1));
            }
        }
    }

    private void manageContent() {
        System.out.println("\n1. Delete media");
        System.out.println("2. Delete character");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            List<Media> mediaList = facade.browseMedia(items -> items);
            if (mediaList.isEmpty()) return;

            for (int i = 0; i < mediaList.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, mediaList.get(i).getTitle());
            }
            System.out.print("Choose media to delete: ");
            int del = scanner.nextInt();
            scanner.nextLine();

            if (del > 0 && del <= mediaList.size()) {
                facade.deleteMedia(mediaList.get(del - 1).getId());
                System.out.println("Media deleted successfully!");
            }
        } else if (choice == 2) {
            List<Character> characters = facade.browseCharacters(items -> items);
            if (characters.isEmpty()) return;

            for (int i = 0; i < characters.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, characters.get(i).getName());
            }
            System.out.print("Choose character to delete: ");
            int del = scanner.nextInt();
            scanner.nextLine();

            if (del > 0 && del <= characters.size()) {
                facade.deleteCharacter(characters.get(del - 1).getId());
                System.out.println("Character deleted successfully!");
            }
        }
    }

    private void manageUsers() {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ All Users ▀▄▀▄▀▄▀▄▀▄▀▄");
        // Note: This would need a method in facade to get all users
        System.out.println("User management functionality - implement getUserList in facade");
    }

    private void viewAuthorProfile(int authorId, String authorName) {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + " ▀▄▀▄▀▄▀▄▀▄▀▄");

        List<Media> authorMedia = facade.getUserMedia(authorId);
        System.out.println("\nMedia created: " + authorMedia.size());

        List<Character> authorCharacters = facade.getUserCharacters(authorId);
        System.out.println("Characters created: " + authorCharacters.size());

        // Show favorites count if not admin
        try {
            Connection conn = dbManager.getConnection();
            UserRepository userRepo = new JdbcUserRepository(conn);
            boolean isAuthorAdmin = userRepo.isAdmin(authorId);

            if (!isAuthorAdmin) {
                CharacterRepository charRepo = new JdbcCharacterRepository(conn);
                FavoriteRepository favRepo = new JdbcFavoriteRepository(conn, charRepo);
                FavoriteService favService = new FavoriteService(favRepo);

                int favMediaCount = favService.countFavoriteMedia(authorId);
                int favCharCount = favService.countFavoriteCharacters(authorId);
                System.out.println("Favorites: " + (favMediaCount + favCharCount) + " (" + favMediaCount + " media, " + favCharCount + " characters)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n1. View author's media");
        System.out.println("2. View author's characters");
        System.out.println("3. View author's favorites");
        System.out.println("4. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> viewAuthorMedia(authorId, authorName, authorMedia);
            case 2 -> viewAuthorCharacters(authorId, authorName, authorCharacters);
            case 3 -> viewAuthorFavorites(authorId, authorName);
        }
    }

    private void viewAuthorMedia(int authorId, String authorName, List<Media> mediaList) {
        if (mediaList.isEmpty()) {
            System.out.println("No media created by this author.");
            System.out.print("Press Enter to go back...");
            scanner.nextLine();
            viewAuthorProfile(authorId, authorName);
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + "'s Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < mediaList.size(); i++) {
            System.out.printf("%d. %s | %s\n", i + 1, mediaList.get(i).getTitle(), mediaList.get(i).getType());
        }

        System.out.print("\nChoose media for details (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= mediaList.size()) {
            Media selected = facade.viewMediaDetails(mediaList.get(choice - 1).getId());

            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Media Details ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println(selected.getDetails());
            System.out.println("Author: " + authorName);

            System.out.println("\n1. Add to favorites");
            System.out.println("2. Show all characters");
            System.out.println("3. Go back to author's profile");
            System.out.print("Choose option: ");
            int detailChoice = scanner.nextInt();
            scanner.nextLine();

            switch (detailChoice) {
                case 1 -> {
                    facade.addToFavorites(currentUser.getId(), selected.getId(), true);
                    viewAuthorMedia(authorId, authorName, mediaList);
                }
                case 2 -> {
                    showMediaCharacters(selected);
                    viewAuthorMedia(authorId, authorName, mediaList);
                }
                case 3 -> viewAuthorProfile(authorId, authorName);
            }
        } else {
            viewAuthorProfile(authorId, authorName);
        }
    }

    private void viewAuthorCharacters(int authorId, String authorName, List<Character> characterList) {
        if (characterList.isEmpty()) {
            System.out.println("No characters created by this author.");
            System.out.print("Press Enter to go back...");
            scanner.nextLine();
            viewAuthorProfile(authorId, authorName);
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + "'s Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < characterList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, characterList.get(i).getName());
        }

        System.out.print("\nChoose character for details (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= characterList.size()) {
            Character selected = characterList.get(choice - 1);

            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println(selected.getDetails());

            Media media = facade.viewMediaDetails(selected.getMediaId());
            if (media != null) {
                System.out.println("From Media: " + media.getTitle());
            }
            System.out.println("Author: " + authorName);

            System.out.println("\n1. Add to favorites");
            System.out.println("2. Go back to author's profile");
            System.out.print("Choose option: ");
            int detailChoice = scanner.nextInt();
            scanner.nextLine();

            if (detailChoice == 1) {
                facade.addToFavorites(currentUser.getId(), selected.getId(), false);
            }

            viewAuthorProfile(authorId, authorName);
        } else {
            viewAuthorProfile(authorId, authorName);
        }
    }

    private void viewAuthorFavorites(int authorId, String authorName) {
        List<Media> favoriteMedia = facade.getFavoriteMedia(authorId);
        List<Character> favoriteCharacters = facade.getFavoriteCharacters(authorId);

        if (favoriteMedia.isEmpty() && favoriteCharacters.isEmpty()) {
            System.out.println("No favorites.");
            System.out.print("Press Enter to go back...");
            scanner.nextLine();
            viewAuthorProfile(authorId, authorName);
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + "'s Favorites ▀▄▀▄▀▄▀▄▀▄▀▄");

        if (!favoriteMedia.isEmpty()) {
            System.out.println("\nFavorite Media:");
            for (int i = 0; i < favoriteMedia.size(); i++) {
                System.out.printf("M%d. %s | %s\n", i + 1, favoriteMedia.get(i).getTitle(), favoriteMedia.get(i).getType());
            }
        }

        if (!favoriteCharacters.isEmpty()) {
            System.out.println("\nFavorite Characters:");
            for (int i = 0; i < favoriteCharacters.size(); i++) {
                System.out.printf("C%d. %s\n", i + 1, favoriteCharacters.get(i).getName());
            }
        }

        System.out.print("\nChoose item for details (M1, C2, etc.) or 0 to go back: ");
        String choiceStr = scanner.nextLine();

        if (choiceStr.equals("0")) {
            viewAuthorProfile(authorId, authorName);
            return;
        }

        if (choiceStr.toUpperCase().startsWith("M")) {
            try {
                int index = Integer.parseInt(choiceStr.substring(1)) - 1;
                if (index >= 0 && index < favoriteMedia.size()) {
                    Media selected = facade.viewMediaDetails(favoriteMedia.get(index).getId());

                    System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Media Details ▀▄▀▄▀▄▀▄▀▄▀▄");
                    System.out.println(selected.getDetails());

                    String mediaAuthorName = facade.getMediaAuthorName(selected.getId());
                    System.out.println("Author: " + mediaAuthorName);

                    System.out.println("\n1. Add to favorites");
                    System.out.println("2. Show all characters");
                    System.out.println("3. Go back to author's profile");
                    System.out.print("Choose option: ");
                    int detailChoice = scanner.nextInt();
                    scanner.nextLine();

                    switch (detailChoice) {
                        case 1 -> {
                            facade.addToFavorites(currentUser.getId(), selected.getId(), true);
                            viewAuthorFavorites(authorId, authorName);
                        }
                        case 2 -> {
                            showMediaCharacters(selected);
                            viewAuthorFavorites(authorId, authorName);
                        }
                        case 3 -> viewAuthorProfile(authorId, authorName);
                    }
                }
            } catch (NumberFormatException e) {
                viewAuthorFavorites(authorId, authorName);
            }
        } else if (choiceStr.toUpperCase().startsWith("C")) {
            try {
                int index = Integer.parseInt(choiceStr.substring(1)) - 1;
                if (index >= 0 && index < favoriteCharacters.size()) {
                    Character selected = favoriteCharacters.get(index);

                    System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
                    System.out.println(selected.getDetails());

                    Media media = facade.viewMediaDetails(selected.getMediaId());
                    if (media != null) {
                        System.out.println("From Media: " + media.getTitle());
                    }

                    String charAuthorName = facade.getMediaAuthorName(selected.getMediaId());
                    System.out.println("Author: " + charAuthorName);

                    System.out.println("\n1. Add to favorites");
                    System.out.println("2. Go back to author's profile");
                    System.out.print("Choose option: ");
                    int detailChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (detailChoice == 1) {
                        facade.addToFavorites(currentUser.getId(), selected.getId(), false);
                    }

                    viewAuthorProfile(authorId, authorName);
                }
            } catch (NumberFormatException e) {
                viewAuthorFavorites(authorId, authorName);
            }
        }
    }

    public static void main(String[] args) {
        MediaManagementSystem system = new MediaManagementSystem();
        system.start();
    }
}