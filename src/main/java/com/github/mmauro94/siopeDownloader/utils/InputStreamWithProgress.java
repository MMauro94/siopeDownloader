package com.github.mmauro94.siopeDownloader.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;


public class InputStreamWithProgress extends InputStream {

	@NotNull
	private final InputStream inputStream;
	@NotNull
	private final OnProgressListener progressListener;
	@Nullable
	private final Long length;

	public InputStreamWithProgress(@NotNull InputStream inputStream, @NotNull OnProgressListener progressListener, @Nullable Long length) {
		this.inputStream = inputStream;
		this.progressListener = progressListener;
		this.length = length;
	}

	private long read = 0;
	private long lastFire = 0;

	@Override
	public int read(@NotNull byte[] b, int off, int len) throws IOException {
		final int r = inputStream.read(b, off, len);
		if (r > 0) {
			read += r;
		}
		fire();
		return r;
	}

	@Override
	public int read() throws IOException {
		final int r = inputStream.read();
		if (r > 0) {
			read += 1;
		}
		fire();
		return r;
	}

	private void fire() {
		final long now = System.currentTimeMillis();
		if (now - lastFire >= 16) {
			lastFire = now;
			if (length != null) {
				progressListener.onProgress(read / (float) length);
			} else {
				if (read >= 0) {
					progressListener.onProgress(0);
				} else {
					progressListener.onProgress(1);
				}
			}
		}
	}
}
