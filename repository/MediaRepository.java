package repository;

import component.Media;
import java.util.List;

public interface MediaRepository {
    List<Media> findAll();
    List<Media> findByUserId(int userId);
    Media findById(int id);
    void save(String title, String description, String releaseDate, String type, String genre, String hashtags, int userId);
    void delete(int id);
    int getAuthorId(int mediaId);
    String getAuthorName(int mediaId);
}
