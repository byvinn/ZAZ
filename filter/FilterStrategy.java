package filter;

import java.util.List;

public interface FilterStrategy<T> {
    List<T> apply(List<T> items);
}
