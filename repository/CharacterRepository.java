package repository;

import builder.CharacterBuilder;
import component.Character;

import java.util.List;

public interface CharacterRepository {
    List<Character> findAll();
    List<Character> findByMediaId(int mediaId);
    List<Character> findByUserId(int userId);
    Character findById(int id);
    void save(CharacterBuilder builder);
    void delete(int id);
}
