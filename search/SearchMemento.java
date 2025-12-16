package search;

public class SearchMemento {
    private final String searchQuery;

    public SearchMemento(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}
