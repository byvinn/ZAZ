package managment;

import builder.CharacterBuilder;
import component.*;
import component.Character;
import filter.FilterStrategy;
import service.CharacterService;
import service.FavoriteService;
import service.MediaService;
import service.UserService;

import java.util.List;

public class ManagementSystem {
    private MediaService mediaService;
    private CharacterService characterService;
    private UserService userService;
    private FavoriteService favoriteService;

    public ManagementSystem(MediaService mediaService, CharacterService characterService,
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
    public List<User> getAllUsers() {
        return userService.getAllUsersExcept(-1);
    }

    public User login(String username, String password) {
        return userService.login(username, password);
    }

    public User register(String username, String email, String password) {
        return userService.register(username, email, password);
    }

    public void deleteUser(int userId) {
        userService.deleteUser(userId);
    }

    public boolean isUserAdmin(int userId) {
        return userService.isAdmin(userId);
    }

    public int countUserFavoriteMedia(int userId) {
        return favoriteService.countFavoriteMedia(userId);
    }

    public int countUserFavoriteCharacters(int userId) {
        return favoriteService.countFavoriteCharacters(userId);
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
