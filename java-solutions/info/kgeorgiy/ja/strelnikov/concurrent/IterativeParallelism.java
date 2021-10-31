package info.kgeorgiy.ja.strelnikov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements ListIP {
    private final ParallelMapper parallelMapper;

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    public IterativeParallelism() {
        parallelMapper = null;
    }

    /**
     * Join values to string.
     *
     * @param threads number of concurrent threads.
     * @param values  values to join.
     * @return list of joined result of {@link #toString()} call on each value.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return processWithThreads(
                threads,
                values,
                currentStream -> currentStream.map(Object::toString)
                        .collect(Collectors.joining()),
                currentStream -> currentStream.collect(Collectors.joining())
        );
    }

    /**
     * Filters values by predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to filter.
     * @param predicate filter predicate.
     * @return list of values satisfying given predicated. Order of values is preserved.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return processWithThreads(
                threads,
                values,
                currentStream -> currentStream.filter(predicate)
                        .collect(Collectors.toList()),
                currentStream -> currentStream.flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }


    /**
     * Maps values.
     *
     * @param threads number of concurrent threads.
     * @param values  values to filter.
     * @param f       mapper function.
     * @return list of values mapped by given function.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f) throws InterruptedException {
        return processWithThreads(
                threads,
                values,
                currentStream -> currentStream.map(f)
                        .collect(Collectors.toList()),
                currentStream -> currentStream.flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Returns maximum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get maximum of.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return maximum of given values
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        final Function<Stream<? extends T>, ? extends T> getMax =
                currentStream -> currentStream.max(comparator).orElse(null);
        return processWithThreads(
                threads,
                values,
                getMax,
                getMax
        );
    }

    /**
     * Returns minimum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get minimum of.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return minimum of given values
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator.reversed());
    }

    /**
     * Returns whether all values satisfies predicate.
     *
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return whether all values satisfies predicate or {@code true}, if no values are given
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return processWithThreads(
                threads,
                values,
                currentStream -> currentStream.allMatch(predicate),
                currentStream -> currentStream.allMatch(Boolean::booleanValue)
        );
    }

    /**
     * Returns whether any of values satisfies predicate.
     *
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return whether any value satisfies predicate or {@code false}, if no values are given
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, a -> !predicate.test(a));
    }

    private <T> List<Stream<? extends T>> getPartition(int threads, List<? extends T> values) {
        int partsCount = Math.min(threads, values.size());
        List<Stream<? extends T>> partition = new ArrayList<>();
        int partSize = values.size() / partsCount;
        int elementsLeft = values.size() % partsCount;
        int l = 0;
        for (int i = 0; i < partsCount; i++) {
            int r = l + partSize;
            if (elementsLeft > 0) {
                r++;
                elementsLeft--;
            }
            partition.add(values.subList(l, r).stream());
            l = r;
        }
        return partition;
    }

    private <T, U> U processWithThreads(int threads, List<? extends T> values,
                                        Function<Stream<? extends T>, ? extends U> threadFunction,
                                        Function<Stream<? extends U>, ? extends U> resultFunction) throws InterruptedException {
        assert threads >= 1;
        List<Stream<? extends T>> partition = getPartition(threads, values);
        List<U> results;
        if (parallelMapper == null) {
            results = new ArrayList<>();
            for (int i = 0; i < partition.size(); i++) {
                results.add(null);
            }
            ArrayList<Thread> threadsToRun = new ArrayList<>();
            for (int i = 0; i < partition.size(); i++) {
                final int currentIndex = i;
                Thread thread = new Thread(() -> results.set(currentIndex, threadFunction.apply(partition.get(currentIndex))));
                threadsToRun.add(thread);
                thread.start();
            }
            for (Thread thread : threadsToRun) {
                thread.join();
            }
        } else {
            results = parallelMapper.map(threadFunction, partition);
        }
        return resultFunction.apply(results.stream());
    }
}
