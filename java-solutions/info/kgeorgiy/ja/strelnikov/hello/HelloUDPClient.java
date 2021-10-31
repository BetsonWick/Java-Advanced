package info.kgeorgiy.ja.strelnikov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static info.kgeorgiy.ja.strelnikov.hello.UDPUtils.*;

public class HelloUDPClient implements HelloClient {

    public static void main(final String[] args) {
        runClient(args, new HelloUDPClient());
    }

    /**
     * Runs Hello client.
     * This method should return when all requests completed.
     *
     * @param nameOrIp server host
     * @param port     server port
     * @param prefix   request prefix
     * @param threads  number of request threads
     * @param requests number of requests per thread.
     */
    @Override
    public void run(final String nameOrIp, final int port, final String prefix, final int threads, final int requests) {
        final SocketAddress address;
        if ((address = assignAddress(nameOrIp, port)) == null) {
            return;
        }
        final ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        IntStream.range(0, threads).forEach(currentThread -> threadPool.submit(() -> {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(SOCKET_TIMEOUT);
                final byte[] buffer = new byte[socket.getReceiveBufferSize()];
                final DatagramPacket packet = getPacket(buffer, address);
                IntStream.range(0, requests).forEach(currentRequest -> {
                    while (!socket.isClosed() && !Thread.interrupted()) {
                        try {
                            final String requestAsString = getDefaultClientMessage(
                                    prefix,
                                    currentThread,
                                    currentRequest
                            );
                            sendPacketWithString(requestAsString, packet, socket);
                            packet.setData(buffer);
                            packet.setLength(buffer.length);
                            receivePacket(packet, socket);
                            final String responseAsString = packetDataAsString(packet);
                            if (responseAsString.contains(requestAsString)) {
                                printMessage(requestAsString, responseAsString);
                                break;
                            }
                        } catch (final IOException e) {
                            System.err.println("Error while: " + e.getMessage());
                        }
                    }
                });
            } catch (final SocketException e) {
                System.err.println("Error while creating a socket: " + e.getMessage());
            }
        }));
        closeService(threadPool, threads * requests);
    }
}
