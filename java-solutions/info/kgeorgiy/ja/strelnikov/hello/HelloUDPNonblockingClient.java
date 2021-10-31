package info.kgeorgiy.ja.strelnikov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import static info.kgeorgiy.ja.strelnikov.hello.UDPUtils.*;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public class HelloUDPNonblockingClient implements HelloClient {

    public static void main(final String[] args) {
        runClient(args, new HelloUDPNonblockingClient());
    }

    @Override
    public void run(final String nameOrIp,
                    final int port,
                    final String prefix,
                    final int threads,
                    final int requests) {
        try (final Selector selector = Selector.open()) {
            try {
                final SocketAddress address = assignAddress(nameOrIp, port);
                for (int i = 0; i < threads; i++) {
                    DatagramChannel channel = null;
                    try {
                        channel = configureChannel().connect(address);
                        channel.register(selector, OP_WRITE, new ClientData(i));
                    } catch (final IOException e) {
                        System.err.println("Error during configuration of a channel: " + e.getMessage());
                        closeChannel(channel);
                    }
                }
                process(prefix, requests, selector);
            } finally {
                closeSelectorWithChannels(selector);
            }
        } catch (final IOException e) {
            System.err.println("Error during creating a selector: " + e.getMessage());
        }
    }

    private void process(final String prefix, final int requests, final Selector selector) {
        while (!Thread.interrupted() && !selector.keys().isEmpty()) {
            try {
                selector.select(SELECTOR_TIMEOUT);
            } catch (final IOException e) {
                System.err.println("Selection process failure: " + e.getMessage());
            }
            if (selector.selectedKeys().isEmpty()) {
                selector.keys().forEach(key -> key.interestOps(OP_WRITE));
            }
            for (final Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                final SelectionKey key = it.next();
                it.remove();
                try {
                    if (key.isValid()) {
                        final DatagramChannel channel = (DatagramChannel) key.channel();
                        final ClientData data = (ClientData) key.attachment();

                        if (key.isReadable()) {
                            final ByteBuffer buffer = data.getBuffer();
                            receiveToBuffer(buffer, channel);
                            final String responseAsString = bufferToTrimmedString(buffer);
                            if (responseAsString.contains(data.getMessage())) {
                                printMessage(data.getMessage(), responseAsString);
                                data.incrementRequestId();
                                if (data.getRequestId() >= requests) {
                                    closeChannel(channel);
                                    continue;
                                }
                                key.interestOps(OP_WRITE);
                            }
                        }

                        if (key.isWritable()) {
                            final String message = getDefaultClientMessage(
                                    prefix,
                                    data.getThreadId(),
                                    data.getRequestId()
                            );
                            sendStringMessage(message, channel, channel.getRemoteAddress());
                            data.setMessage(message);
                            key.interestOps(OP_READ);
                        }
                    }
                } catch (final IOException e) {
                    System.err.println("Error while: " + e.getMessage());
                }
            }
        }
    }
}
