package filter;

import component.Character;

import java.util.List;
import java.util.stream.Collectors;

public class BirthdayCharacterFilterStrategy implements FilterStrategy<Character> {
    private String birthday;

    public BirthdayCharacterFilterStrategy(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getBirthday() != null && c.getBirthday().contains(birthday))
                .collect(Collectors.toList());
    }
}