package info.kgeorgiy.ja.strelnikov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static info.kgeorgiy.ja.strelnikov.hello.UDPUtils.*;
import static java.nio.channels.SelectionKey.OP_READ;

public class HelloUDPNonblockingServer implements HelloServer {
    private Selector selector;
    private ServerData data;
    private ExecutorService mainService;
    private ExecutorService utilityService;

    public static void main(final String[] args) {
        runServer(args, new info.kgeorgiy.ja.strelnikov.hello.HelloUDPNonblockingServer());
    }

    @Override
    public void start(final int port, final int threads) {
        DatagramChannel channel = null;
        try {
            selector = Selector.open();
            channel = configureChannel().bind(new InetSocketAddress(port));
            channel.register(selector, OP_READ);
        } catch (final IOException e) {
            System.err.println("Cannot configure " + e.getMessage());
            closeChannel(channel);
            return;
        }
        data = new ServerData(threads);
        mainService = Executors.newSingleThreadExecutor();
        utilityService = Executors.newFixedThreadPool(threads);
        mainService.submit(this::process);
        mainService.shutdown();
    }

    private void process() {
        final DatagramChannel channel = (DatagramChannel) selector.keys()
                .iterator()
                .next()
                .channel();
        try {
            while (selector.isOpen()) {
                if (selector.select() == 0) {
                    continue;
                }
                final Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                final SelectionKey key = it.next();
                it.remove();
                try {
                    if (key.isReadable()) {
                        final ServerPair available = data.pollAvailable(key);
                        final ByteBuffer buffer = available.getData();
                        final SocketAddress address = receiveToBuffer(buffer, channel);
                        utilityService.submit(() -> {
                            available.setData(stringToBuffer(getDefaultServerMessage(bufferToTrimmedString(buffer))));
                            available.setAddress(address);
                            data.addProcessed(available, key, selector);
                        });
                    }
                    if (key.isWritable()) {
                        final ServerPair processed = data.pollProcessed(key);
                        final SocketAddress address = processed.getAddress();
                        final ByteBuffer buffer = processed.getData();
                        sendMessage(buffer, channel, address);
                        data.addAvailable(processed, key);
                    }
                } catch (final IOException e) {
                    System.err.println("Error while: " + e.getMessage());
                }
            }
        } catch (final IOException e) {
            System.err.println("Error while trying to select " + e.getMessage());
        }
    }

    @Override
    public void close() {
        closeSelectorWithChannels(selector);
        closeService(utilityService, SERVICE_TIMEOUT);
        closeService(mainService, SERVICE_TIMEOUT);
    }

}
