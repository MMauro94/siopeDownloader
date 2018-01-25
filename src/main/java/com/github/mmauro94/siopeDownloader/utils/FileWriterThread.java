package com.github.mmauro94.siopeDownloader.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

public class FileWriterThread extends Thread {

	public enum Status {
		IDLE, WRITING, FINISHED, ERROR;
	}

	@NotNull
	private final InputStream inputStream;
	@NotNull
	private final File file;

	private final AtomicLong writtenBytes = new AtomicLong(0);
	@NotNull
	private Status status = Status.IDLE;
	@Nullable
	private Exception exception;

	public FileWriterThread(@NotNull InputStream inputStream, @NotNull File file) {
		this.inputStream = inputStream;
		this.file = file;
	}

	public long awaitBytes(long bytes) throws IOException, InterruptedException {
		while (writtenBytes.get() < bytes && status == Status.WRITING) {
			synchronized (this) {
				wait();
			}
		}
		if (status == Status.ERROR) {
			if (exception != null) {
				throw new IOException(exception.getMessage(), exception);
			} else {
				throw new IllegalStateException("Error without exception");
			}
		}
		final long toRead = Math.min(writtenBytes.get(), bytes);
		writtenBytes.addAndGet(-toRead);
		return toRead;
	}

	public void awaitStart() throws InterruptedException {
		while (status == Status.IDLE) {
			synchronized (this) {
				wait();
			}
		}
	}

	@NotNull
	public Status getStatus() {
		return status;
	}

	@Override
	public void run() {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			status = Status.WRITING;
			final byte[] buffer = new byte[1024 * 1024]; //1MB
			int r;
			while ((r = inputStream.read(buffer)) > 0) {
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				fos.write(buffer, 0, r);
				fos.flush();
				writtenBytes.addAndGet(r);
				synchronized (this) {
					notify();
				}
			}
			status = Status.FINISHED;
			synchronized (this) {
				notify();
			}
		} catch (IOException | InterruptedException e) {
			status = Status.ERROR;
			exception = e;
			synchronized (this) {
				notify();
			}
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				status = Status.ERROR;
				if (exception != null) {
					exception = e;
				}
			}
		}
	}
}
