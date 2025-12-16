package filter;

import component.Character;

import java.util.List;
import java.util.stream.Collectors;

public class HashtagCharacterFilterStrategy implements FilterStrategy<Character> {
    private String hashtag;

    public HashtagCharacterFilterStrategy(String hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getHashtags() != null && c.getHashtags().toLowerCase().contains(hashtag.toLowerCase()))
                .collect(Collectors.toList());
    }
}
