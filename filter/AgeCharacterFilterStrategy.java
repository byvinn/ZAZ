package filter;

import component.Character;

import java.util.List;
import java.util.stream.Collectors;

public class AgeCharacterFilterStrategy implements FilterStrategy<Character> {
    private String ageText;

    public AgeCharacterFilterStrategy(String ageText) {
        this.ageText = ageText;
    }

    @Override
    public List<Character> apply(List< Character > items) {
        return items.stream()
                .filter(c -> String.valueOf(c.getAge()).contains(ageText))
                .collect(Collectors.toList());
    }
}
