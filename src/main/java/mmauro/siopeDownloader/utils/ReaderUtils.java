package mmauro.siopeDownloader.utils;

import org.jetbrains.annotations.NotNull;
import sun.misc.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Scanner;

public final class ReaderUtils {

    private ReaderUtils() {
        throw new IllegalStateException();
    }

    @NotNull
    public static String readAll(@NotNull final Reader reader) throws IOException {
        final char[] buffer = new char[1024];
        final StringBuilder out = new StringBuilder();
        while (true) {
            int rsz = reader.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }


}
