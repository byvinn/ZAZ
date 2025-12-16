package component;

import java.util.ArrayList;
import java.util.List;

public class Media {
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
