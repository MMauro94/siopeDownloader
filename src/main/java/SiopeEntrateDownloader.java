import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SiopeEntrateDownloader {

    private final static String BASE_URL = "https://www.siope.it/Siope2Web/documenti/siope2/open/last/";

    private final int year;

    public SiopeEntrateDownloader(int year) {
        if (year < 1990) {
            throw new IllegalArgumentException("Year must be >= 1990");
        }
        this.year = year;
    }

    @NotNull
    private URL getEntrateUrl() throws MalformedURLException {
        return new URL(BASE_URL + "SIOPE_USCITE." + year + ".zip");
    }

    @NotNull
    private URL getUsciteUrl() throws MalformedURLException {
        return new URL(BASE_URL + "SIOPE_USCITE." + year + ".zip");
    }

    @NotNull
    public SiopeInputStream downloadEntrate() throws IOException {
        return download(getEntrateUrl());
    }


    @NotNull
    public SiopeInputStream downloadUscite() throws IOException {
        return download(getUsciteUrl());
    }

    @NotNull
    private SiopeInputStream download(@NotNull URL url) throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection.getResponseCode() == 200) {
            final ZipInputStream zis = new ZipInputStream(urlConnection.getInputStream());
            final ZipEntry entry = zis.getNextEntry();
            if (!entry.getName().endsWith(".csv")) {
                throw new IllegalStateException("File contained in zip file is not a csv");
            } else {
                return new SiopeInputStream(zis);
            }
        } else {
            throw new IOException("Response code: " + urlConnection.getResponseCode() + ", " + urlConnection.getResponseMessage());
        }
    }
}
