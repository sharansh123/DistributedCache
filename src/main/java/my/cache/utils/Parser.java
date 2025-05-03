package my.cache.utils;

import my.cache.exceptions.InvalidCommand;
import my.cache.model.MessageGet;
import my.cache.model.MessageJoin;
import my.cache.model.MessageRemove;
import my.cache.model.MessageSet;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Parser {

    private static byte[] packBytes(ByteArrayOutputStream baos) throws IOException {
        byte[] b = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + b.length);
        buffer.putInt(b.length);
        buffer.put(b);
        return buffer.array();
    }

    public static byte[] marshall(Object request, String command) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(baos);
        packer.packString(command);
        switch (command){
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
            default: throw new InvalidCommand();
        }
    }

    public static Object unmarshall(byte[] response) throws IOException {
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
            default: throw new InvalidCommand();
        }
    }
}
