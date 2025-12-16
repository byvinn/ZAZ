package component;

public class User {
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
