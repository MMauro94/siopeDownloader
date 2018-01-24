package com.github.mmauro94.siopeDownloader.utils;

import java.io.IOException;
import java.util.Iterator;

public interface ClosableIterator<T> extends Iterator<T>, AutoCloseable {

	@Override
	void close() throws IOException;
}
