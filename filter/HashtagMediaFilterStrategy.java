package filter;

import component.Media;

import java.util.List;
import java.util.stream.Collectors;

public class HashtagMediaFilterStrategy implements FilterStrategy<Media> {
    private String hashtag;

    public HashtagMediaFilterStrategy(String hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public List<Media> apply(List<Media> items) {
        return items.stream()
                .filter(m -> m.getHashtags() != null && m.getHashtags().toLowerCase().contains(hashtag.toLowerCase()))
                .collect(Collectors.toList());
    }
}
