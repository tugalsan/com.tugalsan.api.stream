package com.tugalsan.api.stream.server;


import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TS_StreamUtils {

    public static <T> Stream<List<T>> slidingWindow(List<T> list, int size, boolean showEvenOnInsufficientInput) {
        if (size < 1) {
            size = 1;
        }
        if (size > list.size()) {
            if (showEvenOnInsufficientInput) {
                size = list.size();
            } else {
                return Stream.empty();
            }
        }
        var fSize = size;
        return IntStream.range(0, list.size() - fSize + 1)
                .mapToObj(start -> list.subList(start, start + fSize));
    }

    public static void transfer(InputStream src0, OutputStream dest0) {
        TGS_FuncMTCUtils.run(() -> {
            try (var src = src0; var dest = dest0; var inputChannel = Channels.newChannel(src); var outputChannel = Channels.newChannel(dest);) {
                transfer(inputChannel, outputChannel);
            }
        });
    }

    public static void transfer(ReadableByteChannel src0, WritableByteChannel dest0) {
        TGS_FuncMTCUtils.run(() -> {
            try (var src = src0; var dest = dest0;) {
                var buffer = ByteBuffer.allocateDirect(16 * 1024);
                while (src.read(buffer) != -1) {
                    buffer.flip();
                    dest.write(buffer);
                    buffer.compact();
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    dest.write(buffer);
                }
            }
        });
    }

    public static int readInt(InputStream is0) {
        return TGS_FuncMTCUtils.call(() -> {
            try (var is = is0) {
                var byte_array_4 = new byte[4];
                byte_array_4[0] = (byte) is.read();
                byte_array_4[1] = (byte) is.read();
                byte_array_4[2] = (byte) is.read();
                byte_array_4[3] = (byte) is.read();
                return ByteBuffer.wrap(byte_array_4).getInt();
            }
        });
    }
}
