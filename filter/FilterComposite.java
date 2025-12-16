package filter;

import java.util.ArrayList;
import java.util.List;

public class FilterComposite<T> implements FilterStrategy<T>{
    private List<FilterStrategy<T>> filters = new ArrayList<>();

    public void add(FilterStrategy<T> filter) {
        filters.add(filter);
    }

    @Override
    public List<T> apply(List<T> items) {
        List<T> result = items;
        for (FilterStrategy<T> filter : filters) {
            result = filter.apply(result);
        }
        return result;
    }
}
