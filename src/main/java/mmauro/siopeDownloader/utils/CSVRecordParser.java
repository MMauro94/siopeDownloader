package mmauro.siopeDownloader.utils;

import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

public interface CSVRecordParser<T> {
    T parse(@NotNull CSVRecord record);
}
