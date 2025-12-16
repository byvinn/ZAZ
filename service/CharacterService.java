package service;

import java.util.List;

import component.Character;
import builder.CharacterBuilder;
import repository.CharacterRepository;
import filter.FilterStrategy;


public class CharacterService {
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
