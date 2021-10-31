package info.kgeorgiy.ja.strelnikov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static info.kgeorgiy.ja.strelnikov.hello.UDPUtils.*;

public class HelloUDPServer implements HelloServer {
    private ExecutorService threadPool;
    private DatagramSocket socket;

    public static void main(final String[] args) {
        runServer(args, new HelloUDPServer());
    }

    /**
     * Starts a new Hello server.
     * This method should return immediately.
     *
     * @param port    server port.
     * @param threads number of working threads.
     */
    @Override
    public void start(final int port, final int threads) {
        try {
            socket = new DatagramSocket(port);
            threadPool = Executors.newFixedThreadPool(threads);
            IntStream.range(0, threads).forEach(i -> threadPool.submit(() -> {
                while (!socket.isClosed()) {
                    try {
                        final DatagramPacket packet = getPacket(socket.getReceiveBufferSize());
                        receivePacket(packet, socket);
                        sendPacketWithString(
                                getDefaultServerMessage(packetDataAsString(packet)),
                                packet,
                                socket
                        );
                    } catch (final IOException e) {
                        System.err.println("Error while: " + e.getMessage());
                    }
                }
            }));
        } catch (final SocketException e) {
            System.err.println("Error while creating a socket on port: " + port + ", reason: " + e.getMessage());
        }
    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        socket.close();
        closeService(threadPool, SERVICE_TIMEOUT);
    }
}
