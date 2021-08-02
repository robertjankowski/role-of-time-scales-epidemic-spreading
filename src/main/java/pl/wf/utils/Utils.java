package pl.wf.utils;

import java.util.*;

public class Utils {
    private static Random r = new Random();

    public static <E> E getRandomSetElement(Set<E> set) {
        return set.stream()
                .skip(new Random().nextInt(set.size()))
                .findFirst()
                .orElse(null);
    }

    public static <E> E getRandomListElement(List<E> list) {
        return list.stream()
                .skip(new Random().nextInt(list.size()))
                .findFirst()
                .orElse(null);
    }

    public static <T> Set<T> setDifference(final Set<T> setOne, final Set<T> setTwo) {
        Set<T> result = new HashSet<T>(setOne);
        result.removeIf(setTwo::contains);
        return result;
    }

    public static double nextGaussian(double mean, double std) {
        return r.nextGaussian() * std + mean;
    }
}
