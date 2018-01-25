package com.github.mmauro94.siopeDownloader.datastruct.operazioni;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleUscite;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;

public final class Uscita extends Operazione<CodiceGestionaleUscite> {

	public Uscita(@NotNull Ente ente, int year, int month, @NotNull CodiceGestionaleUscite codiceGestionale, long amount) {
		super(ente, year, month, codiceGestionale, amount);
	}

	private final static Creator<CodiceGestionaleUscite, Uscita> CREATOR = (Creator<CodiceGestionaleUscite, Uscita>) (anagrafiche, ente, year, month, codiceGestionale, amount) -> new Uscita(ente, year, month, anagrafiche.getCodiciGestionaliUscite().get(codiceGestionale), amount);

	@NotNull
	public static Uscita parse(@NotNull CSVRecord record, @NotNull Anagrafiche anagrafiche) {
		return Operazione.parse(record, anagrafiche, CREATOR);
	}


	public static class Downloader extends Operazione.Downloader<CodiceGestionaleUscite, Uscita, Downloader> {

		public Downloader(int year, @NotNull Anagrafiche anagrafiche) {
			super(year, anagrafiche, "SIOPE_USCITE", CREATOR);
		}

		@Override
		protected Downloader me() {
			return this;
		}
	}
}
