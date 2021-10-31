package info.kgeorgiy.ja.strelnikov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> availableThreads;
    private final ProcessQueue processQueue;

    /**
     * Constructor of {@link ParallelMapperImpl} which creates a {@link ProcessQueue} unit.
     * {@link ProcessQueue} contains {@link Runnable} processes which are then passed to
     * available threads.
     *
     * @param threads an amount of threads to generate.
     */
    public ParallelMapperImpl(final int threads) {
        processQueue = new ProcessQueue();
        Runnable process = () -> {
            try {
                while (!Thread.interrupted()) {
                    processQueue.pollProcess().run();
                }
            } catch (InterruptedException ignored) {
            }
        };
        availableThreads = Stream.generate(() -> new Thread(process))
                .limit(threads)
                .collect(Collectors.toList());
        availableThreads.forEach(Thread::start);
    }

    /**
     * Maps function {@code f} over specified {@code args}.
     * Mapping for each element performs in parallel.
     *
     * @throws InterruptedException if calling thread was interrupted
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        final int resultSize = args.size();
        ProcessSolver<R> solver = new ProcessSolver<>(resultSize);
        IntStream.range(0, resultSize)
                .forEach((currentIndex) -> processQueue.addProcess(
                        () -> solver.setSolution(
                                currentIndex,
                                f.apply(args.get(currentIndex)))));
        return solver.getSolutions();
    }

    /**
     * Stops all threads. All unfinished mappings leave in undefined state.
     */
    @Override
    public void close() {
        availableThreads.forEach(Thread::interrupt);
        for (Thread thread : availableThreads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static class ProcessQueue {
        private final ArrayDeque<Runnable> processes;

        private ProcessQueue() {
            processes = new ArrayDeque<>();
        }

        private void addProcess(final Runnable process) {
            synchronized (processes) {
                processes.add(process);
                processes.notify();
            }
        }

        private Runnable pollProcess() throws InterruptedException {
            Runnable process;
            synchronized (processes) {
                while (processes.isEmpty()) {
                    processes.wait();
                }
                process = processes.poll();
            }
            return process;
        }
    }

    private static class ProcessSolver<R> {

        private final List<R> solvedProcesses;
        private int remainingCount;

        private ProcessSolver(final int size) {
            solvedProcesses = new ArrayList<>(Collections.nCopies(size, null));
            remainingCount = size;
        }

        private synchronized void setSolution(final int index, final R solution) {
            solvedProcesses.set(index, solution);
            remainingCount--;
            if (remainingCount == 0) {
                notify();
            }
        }

        private synchronized List<R> getSolutions() throws InterruptedException {
            while (remainingCount != 0) {
                wait();
            }
            return solvedProcesses;
        }
    }

}
