package component;

import java.util.HashMap;
import java.util.Map;

public class Character {
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
