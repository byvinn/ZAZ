package filter;

import component.Character;

import java.util.List;
import java.util.stream.Collectors;

public class SpeciesCharacterFilterStrategy implements FilterStrategy<Character> {
    private String species;

    public SpeciesCharacterFilterStrategy(String species) {
        this.species = species;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getSpecies() != null && c.getSpecies().toLowerCase().contains(species.toLowerCase()))
                .collect(Collectors.toList());
    }
}