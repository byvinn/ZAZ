package service;

import component.Character;
import component.Media;
import repository.FavoriteRepository;

import java.util.List;

public class FavoriteService {
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
