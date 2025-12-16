package filter;

import component.Character;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityCharacterFilterStrategy implements FilterStrategy<Character> {
    private String activity;

    public ActivityCharacterFilterStrategy(String activity) {
        this.activity = activity;
    }

    @Override
    public List<Character> apply(List<Character> items) {
        return items.stream()
                .filter(c -> c.getActivity() != null && c.getActivity().toLowerCase().contains(activity.toLowerCase()))
                .collect(Collectors.toList());
    }
}
