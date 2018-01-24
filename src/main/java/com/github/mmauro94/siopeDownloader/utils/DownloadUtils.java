package com.github.mmauro94.siopeDownloader.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class DownloadUtils {

	private DownloadUtils() {
		throw new IllegalStateException();
	}

	public static class DownloadResult {
		@NotNull
		private final InputStream inputStream;
		@Nullable
		private final Long length;

		private DownloadResult(@NotNull InputStream inputStream, @Nullable Long length) {
			this.inputStream = inputStream;
			this.length = length;
		}

		@NotNull
		public InputStream getInputStream() {
			return inputStream;
		}

		@Nullable
		public Long getLength() {
			return length;
		}
	}

	@NotNull
	public static DownloadResult download(@NotNull URL url, @Nullable OnProgressListener progressListener) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		final long length = urlConnection.getContentLengthLong();
		Long objLength = length < 0 ? null : length;
		if (urlConnection.getResponseCode() == 200) {
			final InputStream is;
			if (progressListener != null) {
				is = new InputStreamWithProgress(urlConnection.getInputStream(), progressListener, objLength);
			} else {
				is = urlConnection.getInputStream();
			}
			return new DownloadResult(new BufferedInputStream(is), objLength);
		} else {
			throw new IOException("Response code: " + urlConnection.getResponseCode() + ", " + urlConnection.getResponseMessage());
		}
	}
}
