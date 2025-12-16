package filter;

import component.Media;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeFilterStrategy implements FilterStrategy<Media> {
    private Set<String> types;

    public TypeFilterStrategy(Set<String> types) {
        this.types = types;
    }

    @Override
    public List<Media> apply(List<Media> items) {
        return items.stream()
                .filter(m -> types.contains(m.getType()))
                .collect(Collectors.toList());
    }
}
