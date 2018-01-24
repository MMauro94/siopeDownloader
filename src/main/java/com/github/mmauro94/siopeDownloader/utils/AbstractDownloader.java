package com.github.mmauro94.siopeDownloader.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractDownloader<T, ME extends AbstractDownloader<T, ME>> {

	@Nullable
	protected OnProgressListener onProgressListener;

	protected AbstractDownloader() {
	}

	public ME setOnProgressListener(@Nullable OnProgressListener onProgressListener) {
		this.onProgressListener = onProgressListener;
		return me();
	}

	protected abstract ME me();


	@NotNull
	protected abstract URL getURL() throws MalformedURLException;

	@NotNull
	protected abstract T fromInputStream(@NotNull final InputStream inputStream) throws IOException;

	@NotNull
	public T download() throws IOException {
		return fromInputStream(DownloadUtils.download(getURL(), onProgressListener).getInputStream());
	}

	@NotNull
	public T download(@NotNull File tempFile) throws IOException, InterruptedException {
		final DownloadUtils.DownloadResult download = DownloadUtils.download(getURL(), null);
		final FileWriterThread fwt = new FileWriterThread(download.getInputStream(), tempFile);
		fwt.setName("SIOPE AbstractDownloader file writer");
		fwt.start();
		fwt.awaitStart();
		final FileInputStream fis = new FileInputStream(tempFile);
		final InputStream is = new InputStream() {

			private long readBytes = 0;
			private long lastProgress = 0;
			private boolean over = false;

			private long retardedAwaitBytes(long bytes) throws IOException {
				long toReadBytes;
				try {
					toReadBytes = fwt.awaitBytes(bytes);
				} catch (InterruptedException e) {
					throw new IOException(e.getMessage(),e);
				}
				return toReadBytes;
			}

			@Override
			public int read() throws IOException {
				long toReadBytes = 0;
				toReadBytes = retardedAwaitBytes(1);
				if (toReadBytes > 0) {
					final int r = fis.read();
					if (r != -1) {
						readBytes++;
					} else {
						over = true;
					}
					progress();
					return r;
				}
				return -1;
			}

			@Override
			public int read(@NotNull byte[] b) throws IOException {
				long toReadBytes = retardedAwaitBytes(b.length);
				if (toReadBytes > 0) {
					int r = fis.read(b, 0, Math.min((int) toReadBytes, b.length));
					if (r != -1) {
						readBytes += r;
					} else {
						over = true;
					}
					progress();
					return r;
				}
				return -1;
			}

			@Override
			public int read(@NotNull byte[] b, int off, int len) throws IOException {
				long toReadBytes = retardedAwaitBytes(len);
				if (toReadBytes > 0) {
					final int r = fis.read(b, off, Math.min((int) toReadBytes, len));
					if (r != -1) {
						readBytes += r;
					} else {
						over = true;
					}
					progress();
					return r;
				}
				return -1;
			}

			private void progress() {
				if (onProgressListener != null) {
					final long now = System.currentTimeMillis();
					if (now - lastProgress > 16) {
						if (over) {
							onProgressListener.onProgress(1f);
						} else {
							final Long length = download.getLength();
							if (length != null) {
								onProgressListener.onProgress(readBytes / (float) length);
							} else {
								onProgressListener.onProgress(0f);
							}
						}
					}
				}
			}

			@Override
			public void close() throws IOException {
				fwt.interrupt();
				fis.close();
				//noinspection ResultOfMethodCallIgnored
				tempFile.delete();
			}
		};
		return fromInputStream(is);
	}
}
