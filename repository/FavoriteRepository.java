package repository;

import component.Media;
import component.Character;

import java.util.List;

public interface FavoriteRepository {
    List<Media> findFavoriteMedia(int userId);
    List<Character> findFavoriteCharacters(int userId);
    void addFavorite(int userId, int itemId, boolean isMedia);
    void removeFavorite(int userId, int itemId, boolean isMedia);
    int countFavoriteMedia(int userId);
    int countFavoriteCharacters(int userId);
}
