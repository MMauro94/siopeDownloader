package mmauro.siopeDownloader.utils;

import org.apache.commons.csv.CSVFormat;

public final class CSVUtils {
    private CSVUtils() {
        throw new IllegalStateException();
    }

    public static final CSVFormat CSV_FORMAT = CSVFormat.newFormat(',')
            .withIgnoreEmptyLines()
            .withQuote('"');
}
