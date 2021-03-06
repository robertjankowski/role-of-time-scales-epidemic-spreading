package pl.wf.utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** From https://stackoverflow.com/a/54034748/9511702 to improve speed in shuffling list with `limit`
 * @param <T>
 * @param <LIST>
 */
public class ImprovedRandomSpliterator<T, LIST extends RandomAccess & List<T>> implements Spliterator<T> {

    private final Random random;
    private final List<T> source;
    private int size;

    ImprovedRandomSpliterator(LIST source, Supplier<? extends Random> random) {
        Objects.requireNonNull(source, "source can't be null");
        Objects.requireNonNull(random, "random can't be null");

        this.source = source;
        this.random = random.get();
        this.size = this.source.size();
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (size > 0) {
            int nextIdx = random.nextInt(size);
            int lastIdx = --size;

            T last = source.get(lastIdx);
            T elem = source.set(nextIdx, last);
            action.accept(elem);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return source.size();
    }

    @Override
    public int characteristics() {
        return SIZED;
    }
}