package filter;

import component.Media;

import java.util.List;
import java.util.stream.Collectors;

public class ReleaseDateFilterStrategy implements FilterStrategy<Media> {
    private String releaseDate;

    public ReleaseDateFilterStrategy(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public List<Media> apply(List<Media> items) {
        return items.stream()
                .filter(m -> m.getReleaseDate() != null && m.getReleaseDate().contains(releaseDate))
                .collect(Collectors.toList());
    }
}
