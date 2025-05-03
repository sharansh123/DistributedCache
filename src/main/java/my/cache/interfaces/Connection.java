package my.cache.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface Connection{
    static Socket connect(String host, int port) throws IOException {
        return new Socket(host, port);
    }
    Object read(InputStream inputStream) throws IOException;
    void write(Object data, OutputStream outputStream, String command) throws IOException;
    Object unmarshall(byte[] response) throws IOException;
    byte[] marshall(Object request, String command) throws IOException;

}
