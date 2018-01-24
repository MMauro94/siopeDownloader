package com.github.mmauro94.siopeDownloader.datastruct.operazioni;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.utils.AbstractDownloader;
import com.github.mmauro94.siopeDownloader.utils.ClosableIterator;
import com.github.mmauro94.siopeDownloader.utils.ParseUtils;
import com.github.mmauro94.siopeDownloader.utils.URLUtils;
import lombok.Getter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Operazione<CG extends CodiceGestionale> {

	public interface Creator<CG extends CodiceGestionale, T extends Operazione<CG>> {
		T create(@NotNull Anagrafiche anagrafiche, @NotNull Ente ente, int year, int month, @NotNull String codiceGestionale, long amount);
	}

	@NotNull
	@Getter
	private final Ente ente;
	@Getter
	private final int year;
	@Getter
	private final int month;
	@NotNull
	@Getter
	private final CG codiceGestionale;
	/**
	 * The cents euros of the operation
	 */
	@Getter
	private final long amount;

	protected Operazione(@NotNull Ente ente, int year, int month, @NotNull CG codiceGestionale, long amount) {
		this.ente = ente;
		this.year = year;
		this.month = month;
		this.codiceGestionale = codiceGestionale;
		this.amount = amount;
	}

	@NotNull
	public static <CG extends CodiceGestionale, T extends Operazione<CG>> T parse(@NotNull CSVRecord record, @NotNull Anagrafiche anagrafiche, @NotNull Creator<CG, T> creator) {
		if (record.size() != 5) {
			throw new IllegalArgumentException("Record size != 5");
		} else {
			final Ente ente = anagrafiche.getEnti().get(record.get(0));
			final int year = Integer.parseInt(record.get(1));
			final int month = Integer.parseInt(record.get(2));

			final String codiceGestionale = record.get(3);
			final long amount = Long.parseLong(record.get(4));

			return creator.create(anagrafiche, ente, year, month, codiceGestionale, amount);
		}
	}

	public abstract static class Downloader<CG extends CodiceGestionale, T extends Operazione<CG>, ME extends Downloader<CG, T, ME>> extends AbstractDownloader<ClosableIterator<T>, ME> {

		private final int year;
		@NotNull
		private final Anagrafiche anagrafiche;
		private final String zipName;
		@NotNull
		private final Creator<CG, T> creator;

		protected Downloader(int year, @NotNull Anagrafiche anagrafiche, String zipName, @NotNull Creator<CG, T> creator) {
			this.year = year;
			this.anagrafiche = anagrafiche;
			this.zipName = zipName;
			this.creator = creator;
		}

		@NotNull
		protected URL getURL() throws MalformedURLException {
			return new URL(URLUtils.SIOPE_WEBSITE + "/Siope2Web/documenti/siope2/open/last/" + zipName + "." + year + ".zip");
		}

		@NotNull
		@Override
		protected ClosableIterator<T> fromInputStream(@NotNull InputStream inputStream) throws IOException {
			final ZipInputStream zis = new ZipInputStream(inputStream);
			final ZipEntry entry = zis.getNextEntry();
			if (!entry.getName().endsWith(".csv")) {
				throw new IllegalStateException("File contained in zip file is not a csv");
			} else {
				final Iterator<CSVRecord> iterator = CSVParser.parse(new InputStreamReader(zis), ParseUtils.CSV_FORMAT).iterator();
				return new ClosableIterator<T>() {

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public T next() {
						return parse(iterator.next(), anagrafiche, creator);
					}

					@Override
					public void close() throws IOException {
						zis.close();
					}
				};
			}
		}
	}
}
