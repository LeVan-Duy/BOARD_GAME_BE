
package org.example.board_game.utils;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class CollectionUtils {

    private CollectionUtils() {
    }

    public static <K, V> List<K> extractField(Collection<V> list, Function<V, K> fieldFunction) {
        return list.stream()
                .map(fieldFunction)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public static <K, V> Map<K, List<V>> group(Collection<V> list, Function<V, K> keyFunction) {
        return list.stream()
                .collect(groupingBy(keyFunction));
    }


    public static <K, V> Map<K, V> collectToMap(Collection<V> list, Function<V, K> keyFunction) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }
        return list.stream()
                .collect(Collectors.toMap(keyFunction, Function.identity(), (v1, v2) -> v1));
    }

    public static <T> boolean isListEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

}
