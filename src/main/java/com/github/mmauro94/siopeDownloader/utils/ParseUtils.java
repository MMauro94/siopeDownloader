package com.github.mmauro94.siopeDownloader.utils;

import org.apache.commons.csv.CSVFormat;

import java.text.SimpleDateFormat;

public final class ParseUtils {
	private ParseUtils() {
		throw new IllegalStateException();
	}

	public static final CSVFormat CSV_FORMAT = CSVFormat.newFormat(',')
			.withIgnoreEmptyLines()
			.withQuote('"');


	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
}
