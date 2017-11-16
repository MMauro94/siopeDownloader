package mmauro.siopeDownloader.download;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class SiopeZipDownloader {

    @NotNull
    private final URL url;

    public SiopeZipDownloader(@NotNull URL url) {
        this.url = url;
    }

    @NotNull
    public ZipInputStream download() throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection.getResponseCode() == 200) {
            return new ZipInputStream(urlConnection.getInputStream());
        } else {
            throw new IOException("Response code: " + urlConnection.getResponseCode() + ", " + urlConnection.getResponseMessage());
        }
    }
}
