package mmauro.siopeDownloader.datastruct.operazioni;

import lombok.Getter;
import mmauro.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import mmauro.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import mmauro.siopeDownloader.datastruct.anagrafiche.Ente;
import mmauro.siopeDownloader.download.SiopeZipDownloader;
import mmauro.siopeDownloader.utils.ParseUtils;
import mmauro.siopeDownloader.utils.URLUtils;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Operazione<CG extends CodiceGestionale> {

	public interface Creator<CG extends CodiceGestionale, T extends Operazione<CG>> {
		T create(@NotNull Anagrafiche anagrafiche, @NotNull Ente ente, int year, int month, @NotNull String codiceGestionale, @NotNull BigDecimal amount);
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
	@NotNull
	@Getter
	private final BigDecimal amount;

	protected Operazione(@NotNull Ente ente, int year, int month, @NotNull CG codiceGestionale, @NotNull BigDecimal amount) {
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
			final BigDecimal amount = new BigDecimal(record.get(4)).divide(new BigDecimal(100), 2, RoundingMode.UNNECESSARY);

			return creator.create(anagrafiche, ente, year, month, codiceGestionale, amount);
		}
	}

	@NotNull
	public static <CG extends CodiceGestionale, T extends Operazione<CG>> Iterable<T> download(int year, @NotNull Anagrafiche anagrafiche, @NotNull String zipName, @NotNull Creator<CG, T> creator) throws IOException {
		final ZipInputStream zis = new SiopeZipDownloader(new URL(URLUtils.SIOPE_WEBSITE + "/Siope2Web/documenti/siope2/open/last/" + zipName + "." + year + ".zip")).download();
		final ZipEntry entry = zis.getNextEntry();
		if (!entry.getName().endsWith(".csv")) {
			throw new IllegalStateException("File contained in zip file is not a csv");
		} else {
			final Iterator<CSVRecord> iterator = CSVParser.parse(new InputStreamReader(zis), ParseUtils.CSV_FORMAT).iterator();
			return () -> new Iterator<T>() {

				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public T next() {
					return parse(iterator.next(), anagrafiche, creator);
				}
			};
		}
	}
}
