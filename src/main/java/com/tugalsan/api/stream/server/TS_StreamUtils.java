package com.tugalsan.api.stream.server;

import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
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

    public static TGS_UnionExcuseVoid transfer(InputStream src0, OutputStream dest0) {
        try (var src = src0; var dest = dest0; var inputChannel = Channels.newChannel(src); var outputChannel = Channels.newChannel(dest);) {
            return transfer(inputChannel, outputChannel);
        } catch (IOException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuseVoid transfer(ReadableByteChannel src0, WritableByteChannel dest0) {
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
            return TGS_UnionExcuseVoid.ofVoid();
        } catch (IOException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<Integer> readInt(InputStream is0) {
        try (var is = is0) {
            var byte_array_4 = new byte[4];
            byte_array_4[0] = (byte) is.read();
            byte_array_4[1] = (byte) is.read();
            byte_array_4[2] = (byte) is.read();
            byte_array_4[3] = (byte) is.read();
            return TGS_UnionExcuse.of(ByteBuffer.wrap(byte_array_4).getInt());
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }
}
