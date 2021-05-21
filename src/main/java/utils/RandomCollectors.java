package utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toCollection;

/**
 * From https://stackoverflow.com/a/54034748/9511702
 */
public final class RandomCollectors {

    private RandomCollectors() {
    }

    public static <T> Collector<T, ?, Stream<T>> toImprovedLazyShuffledStream() {
        return Collectors.collectingAndThen(
                toCollection(ArrayList::new),
                list -> !list.isEmpty()
                        ? StreamSupport.stream(new ImprovedRandomSpliterator<>(list, Random::new), false)
                        : Stream.empty());
    }
}