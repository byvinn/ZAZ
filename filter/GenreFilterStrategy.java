package filter;

import component.Media;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GenreFilterStrategy implements FilterStrategy<Media> {
    private Set<String> genres;

    public GenreFilterStrategy(Set<String> genres) {
        this.genres = genres;
    }

    @Override
    public List<Media> apply(List< Media > items) {
        return items.stream()
                .filter(m -> genres.contains(m.getGenre()))
                .collect(Collectors.toList());
    }
}
