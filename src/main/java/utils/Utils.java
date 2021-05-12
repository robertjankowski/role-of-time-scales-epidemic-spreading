package utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Utils {
    public static <E> E getRandomSetElement(Set<E> set) {
        return set.stream()
                .skip(new Random().nextInt(set.size()))
                .findFirst()
                .orElse(null);
    }

    public static <T> Set<T> setDifference(final Set<T> setOne, final Set<T> setTwo) {
        Set<T> result = new HashSet<T>(setOne);
        result.removeIf(setTwo::contains);
        return result;
    }
}
