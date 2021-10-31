package info.kgeorgiy.ja.strelnikov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;
import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class UDPUtils {
    public static final int BUFFER_SIZE = 2048;
    public static final int SELECTOR_TIMEOUT = 200;
    public static final int SOCKET_TIMEOUT = 100;
    public static final int SERVICE_TIMEOUT = 1;

    public static void validateArguments(final String[] args, final int required) throws IllegalArgumentException {
        if (args == null) {
            throw new IllegalArgumentException("Arguments array should not be null!");
        }
        if (args.length < required) {
            throw new IllegalArgumentException("Arguments number: " + args.length + ", required: " + required);
        }
        IntStream.range(0, args.length).forEach(i -> {
            if (args[i] == null) {
                throw new IllegalArgumentException("Arguments should not be null! Null is argument with index: " + i);
            }
        });
    }

    public static String getDefaultClientMessage(final String prefix,
                                                 final int currentThread,
                                                 final int currentRequest) {
        return String.format("%s%s_%s",
                prefix,
                currentThread,
                currentRequest);
    }

    public static String getDefaultServerMessage(final String suffix) {
        return String.format("Hello, %s", suffix);
    }

    public static void sendMessage(final ByteBuffer buffer,
                                   final DatagramChannel channel,
                                   final SocketAddress address) throws IOException {
        try {
            channel.send(buffer, address);
        } catch (final IOException e) {
            throw new IOException("Sending, caused by: " + e.getMessage());
        }
    }

    public static void sendStringMessage(final String message,
                                         final DatagramChannel channel,
                                         final SocketAddress address) throws IOException {
        final ByteBuffer responseBuffer = stringToBuffer(message);
        sendMessage(responseBuffer, channel, address);
    }

    public static SocketAddress receiveToBuffer(final ByteBuffer buffer,
                                                final DatagramChannel channel) throws IOException {
        buffer.clear();
        try {
            final SocketAddress address = channel.receive(buffer);
            buffer.flip();
            return address;
        } catch (final IOException e) {
            throw new IOException("Receiving, caused by: " + e.getMessage());
        }
    }

    public static void closeChannel(final SelectableChannel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (final IOException e) {
                System.err.println("Error during closing a channel: " + e.getMessage());
            }
        }
    }

    public static void closeSelectorWithChannels(final Selector selector) {
        selector.keys().forEach(currentKey -> closeChannel(currentKey.channel()));
        try {
            selector.close();
        } catch (final IOException e) {
            System.err.println("Error during closing a selector: " + e.getMessage());
        }
    }

    public static void receivePacket(final DatagramPacket packet, final DatagramSocket socket) throws IOException {
        try {
            socket.receive(packet);
        } catch (final IOException e) {
            throw new IOException("Receiving, caused by: " + e.getMessage());
        }
    }

    public static void sendPacketWithString(final String data,
                                            final DatagramPacket packet,
                                            final DatagramSocket socket) throws IOException {
        packet.setData(data.getBytes(StandardCharsets.UTF_8));
        packet.setLength(data.length());
        try {
            socket.send(packet);
        } catch (final IOException e) {
            throw new IOException("Sending, caused by: " + e.getMessage());
        }
    }

    public static DatagramPacket getPacket(final byte[] buffer, final SocketAddress address) {
        return new DatagramPacket(buffer, buffer.length, address);
    }

    public static DatagramPacket getPacket(final int bufferSize) {
        return new DatagramPacket(new byte[bufferSize], bufferSize);
    }

    public static String packetDataAsString(final DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    public static ByteBuffer allocateBuffer() {
        return ByteBuffer.allocate(BUFFER_SIZE);
    }

    public static void printMessage(final String request, final String response) {
        System.out.printf("\"%s\" -> \"%s\"%s",
                request,
                response,
                System.lineSeparator()
        );
    }

    public static int parseInt(final String[] args, final int index) throws IllegalArgumentException {
        try {
            return Integer.parseInt(args[index]);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Error while parsing to int argument with index: " + index);
        }
    }

    public static String bufferToTrimmedString(final ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString().trim();
    }

    public static ByteBuffer stringToBuffer(final String string) {
        return ByteBuffer.wrap(string.getBytes(StandardCharsets.UTF_8));
    }

    public static DatagramChannel configureChannel() throws IOException {
        return (DatagramChannel) DatagramChannel.open()
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .configureBlocking(false);
    }

    public static SocketAddress assignAddress(final String nameOrIp, final int port) {
        try {
            return new InetSocketAddress(InetAddress.getByName(nameOrIp), port);
        } catch (final UnknownHostException e) {
            System.err.println("Host name or IP is invalid: " + e.getMessage());
            return null;
        }
    }

    public static void closeService(final ExecutorService service, final int timeSeconds) {
        service.shutdown();
        try {
            if (!service.awaitTermination(timeSeconds, TimeUnit.SECONDS)) {
                service.shutdownNow();
                if (!service.awaitTermination(timeSeconds, TimeUnit.SECONDS))
                    System.err.println("Service termination was not complete.");
            }
        } catch (final InterruptedException e) {
            System.err.println("Interrupted during closing a service: " + e.getMessage());
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void runClient(final String[] args, final HelloClient client) {
        try {
            validateArguments(args, 5);
        } catch (final IllegalArgumentException e) {
            System.err.println("Arguments are invalid: " + e.getMessage());
        }
        final String nameOrIp = args[0];
        final String prefix = args[2];
        try {
            final int port = parseInt(args, 1);
            final int threads = parseInt(args, 3);
            final int requests = parseInt(args, 4);
            client.run(nameOrIp, port, prefix, threads, requests);
        } catch (final IllegalArgumentException e) {
            System.err.println("Arguments are invalid: " + e.getMessage());
        }
    }

    public static void runServer(final String[] args, final HelloServer server) {
        try {
            validateArguments(args, 2);
        } catch (final IllegalArgumentException e) {
            System.err.println("Arguments are invalid: " + e.getMessage());
        }
        try {
            final int port = parseInt(args, 0);
            final int threads = parseInt(args, 1);
            server.start(port, threads);
        } catch (final IllegalArgumentException e) {
            System.err.println("Arguments are invalid: " + e.getMessage());
        }
    }
}
