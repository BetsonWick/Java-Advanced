package info.kgeorgiy.ja.strelnikov.hello;

import java.nio.ByteBuffer;

import static info.kgeorgiy.ja.strelnikov.hello.UDPUtils.allocateBuffer;

public class ClientData {
    private final ByteBuffer buffer;
    private final int threadId;
    private String message;
    private int requestId;

    public ClientData(final int threadId) {
        this.threadId = threadId;
        this.requestId = 0;
        buffer = allocateBuffer();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void incrementRequestId() {
        requestId++;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
