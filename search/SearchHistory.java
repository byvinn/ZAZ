package search;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchHistory {
    private LinkedList<SearchMemento> history = new LinkedList<>();
    private static final int MAX_HISTORY = 5;

    public void addSearch(String query) {
        history.removeIf(m -> m.getSearchQuery().equalsIgnoreCase(query));

        if (history.size() >= MAX_HISTORY) {
            history.removeFirst();
        }
        history.addLast(new SearchMemento(query));
    }

    public List<String> getHistory() {
        return history.stream()
                .map(SearchMemento::getSearchQuery)
                .collect(Collectors.toList());
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }
}
