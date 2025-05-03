package my.cache.logic;

import my.cache.exceptions.InvalidCommand;
import my.cache.interfaces.Connection;
import my.cache.interfaces.MessageHandler;
import my.cache.model.*;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteConnection implements Connection {
    @Override
    public Object read(InputStream inputStream) throws IOException {
        byte[] lengthBytes = new byte[4];
        int val = inputStream.read(lengthBytes);
        if(val == -1) return null;
        ByteBuffer buffer = ByteBuffer.wrap(lengthBytes);
        int messageLength = buffer.getInt();
        byte[] messageBytes = new byte[messageLength];
        int bytesRead = 0;
        while(bytesRead < messageLength) {
            int read = inputStream.read(messageBytes, bytesRead, messageLength - bytesRead);
            if(read == -1) throw new RuntimeException("Connection Closed");
            bytesRead += read;
        }
        return unmarshall(messageBytes);
    }

    @Override
    public void write(Object data, OutputStream outputStream, String command) throws IOException {
        outputStream.write(marshall(data, command));
    }

    @Override
    public byte[] marshall(Object request, String command) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(baos);
        packer.packString(command);
        switch (command) {
            case "SET": {
                MessageSet set = (MessageSet) request;
                packer.packString(new String(set.getKey()));
                packer.packString(new String(set.getValue()));
                packer.packInt(set.getTtl());
                packer.flush();
                return packBytes(baos);
            }
            case "GET": {
                MessageGet get = (MessageGet) request;
                packer.packString(new String(get.getKey()));
                packer.flush();
                return packBytes(baos);
            }
            case "JOIN": {
                MessageJoin join = (MessageJoin) request;
                packer.packString(join.getClient());
                packer.flush();
                return packBytes(baos);
            }
            case "REMOVE": {
                MessageRemove remove = (MessageRemove) request;
                packer.packString(new String(remove.getKey()));
                packer.flush();
                return packBytes(baos);
            }
            case "STATUS": {
                MessageStatus status = (MessageStatus) request;
                packer.packString(status.getStatus());
                packer.packString(status.getVal());
                packer.flush();
                return packBytes(baos);
            }
            default:
                throw new InvalidCommand();
        }
    }

    @Override
    public Object unmarshall(byte[] response) throws IOException {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(response);
        switch (unpacker.unpackString()) {
            case "SET": {
                return new MessageSet(unpacker.unpackString().getBytes(StandardCharsets.ISO_8859_1),
                        unpacker.unpackString().getBytes(StandardCharsets.ISO_8859_1),
                        unpacker.unpackInt());
            }
            case "GET": {
                return new MessageGet(unpacker.unpackString().getBytes(StandardCharsets.ISO_8859_1));
            }
            case "REMOVE": {
                return new MessageRemove(unpacker.unpackString().getBytes(StandardCharsets.ISO_8859_1));
            }
            case "JOIN": {
                return new MessageJoin(unpacker.unpackString());
            }
            case "STATUS": {
                return new MessageStatus(unpacker.unpackString(), unpacker.unpackString());
            }
            default:
                throw new InvalidCommand();
        }
    }

    private static byte[] packBytes(ByteArrayOutputStream baos) throws IOException {
        byte[] b = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + b.length);
        buffer.putInt(b.length);
        buffer.put(b);
        return buffer.array();
    }
}
