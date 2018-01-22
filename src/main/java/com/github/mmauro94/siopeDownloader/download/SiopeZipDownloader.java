package com.github.mmauro94.siopeDownloader.download;

import com.github.mmauro94.siopeDownloader.utils.InputStreamWithProgress;
import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipInputStream;

public class SiopeZipDownloader {

	@NotNull
	private final URL url;
	@Nullable
	private final OnProgressListener progressListener;

	public SiopeZipDownloader(@NotNull URL url, @Nullable OnProgressListener progressListener) {
		this.url = url;
		this.progressListener = progressListener;
	}

	@NotNull
	public ZipInputStream download() throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		final long length = urlConnection.getContentLengthLong();
		if (urlConnection.getResponseCode() == 200) {
			final InputStream is;
			if (progressListener != null) {
				is = new InputStreamWithProgress(urlConnection.getInputStream(), progressListener, length < 0 ? null : length);
			} else {
				is = urlConnection.getInputStream();
			}
			return new ZipInputStream(is);
		} else {
			throw new IOException("Response code: " + urlConnection.getResponseCode() + ", " + urlConnection.getResponseMessage());
		}
	}
}
