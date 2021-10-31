package info.kgeorgiy.ja.strelnikov.hello;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ServerPair {
    private ByteBuffer data;
    private SocketAddress address;

    public ServerPair(final ByteBuffer data, final SocketAddress address) {
        this.data = data;
        this.address = address;
    }

    public ByteBuffer getData() {
        return data;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public void setData(final ByteBuffer data) {
        this.data = data;
    }

    public void setAddress(final SocketAddress address) {
        this.address = address;
    }
}
