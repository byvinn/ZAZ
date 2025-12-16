package service;

import filter.FilterStrategy;

import component.Media;
import repository.MediaRepository;
import repository.CharacterRepository;

import java.util.List;

public class MediaService {
    private MediaRepository mediaRepository;
    private CharacterRepository characterRepository;

    public MediaService(MediaRepository mediaRepository, CharacterRepository characterRepository) {
        this.mediaRepository = mediaRepository;
        this.characterRepository = characterRepository;
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public List<Media> getMediaByUser(int userId) {
        return mediaRepository.findByUserId(userId);
    }

    public Media getMediaById(int id) {
        return mediaRepository.findById(id);
    }

    public List<Media> getFilteredMedia(FilterStrategy<Media> filter) {
        return filter.apply(mediaRepository.findAll());
    }

    public void createMedia(String title, String description, String releaseDate, String type, String genre, String hashtags, int userId) {
        mediaRepository.save(title, description, releaseDate, type, genre, hashtags, userId);
    }

    public void deleteMedia(int id) {
        mediaRepository.delete(id);
    }

    public int getAuthorId(int mediaId) {
        return mediaRepository.getAuthorId(mediaId);
    }

    public String getAuthorName(int mediaId) {
        return mediaRepository.getAuthorName(mediaId);
    }
}
