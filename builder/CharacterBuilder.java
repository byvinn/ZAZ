package builder;

import component.Character;

import java.util.HashMap;
import java.util.Map;

public class CharacterBuilder {
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
