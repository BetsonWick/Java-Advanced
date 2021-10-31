package info.kgeorgiy.ja.strelnikov.hello;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayDeque;
import java.util.stream.IntStream;

import static info.kgeorgiy.ja.strelnikov.hello.UDPUtils.allocateBuffer;

public class ServerData {
    private final ArrayDeque<ServerPair> available;
    private final ArrayDeque<ServerPair> processed;

    public ServerData(final int threads) {
        available = new ArrayDeque<>();
        processed = new ArrayDeque<>();
        IntStream.range(0, threads).forEach(i -> available.add(
                new ServerPair(allocateBuffer(),
                        null))
        );
    }

    public synchronized void addAvailable(final ServerPair pair, final SelectionKey key) {
        available.add(pair);
        key.interestOpsOr(SelectionKey.OP_READ);
    }

    public synchronized void addProcessed(final ServerPair pair, final SelectionKey key, final Selector selector) {
        processed.add(pair);
        key.interestOpsOr(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    public synchronized ServerPair pollAvailable(final SelectionKey key) {
        final ServerPair pair = available.poll();
        if (available.isEmpty()) {
            key.interestOpsAnd(~SelectionKey.OP_READ);
        }
        return pair;
    }

    public synchronized ServerPair pollProcessed(final SelectionKey key) {
        final ServerPair pair = processed.poll();
        if (processed.isEmpty()) {
            key.interestOpsAnd(~SelectionKey.OP_WRITE);
        }
        return pair;
    }


}
