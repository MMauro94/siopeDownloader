package com.github.mmauro94.siopeDownloader.datastruct.operazioni;

import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;

public final class Entrata extends Operazione<CodiceGestionaleEntrate> {

	public Entrata(@NotNull Ente ente, int year, int month, @NotNull CodiceGestionaleEntrate codiceGestionale, long amount) {
		super(ente, year, month, codiceGestionale, amount);
	}

	private final static Operazione.Creator<CodiceGestionaleEntrate, Entrata> CREATOR = (Creator<CodiceGestionaleEntrate, Entrata>) (anagrafiche, ente, year, month, codiceGestionale, amount) -> new Entrata(ente, year, month, anagrafiche.getCodiciGestionaliEntrate().get(codiceGestionale), amount);


	public static class Downloader extends Operazione.Downloader<CodiceGestionaleEntrate, Entrata, Downloader> {

		public Downloader(int year, @NotNull Anagrafiche anagrafiche) {
			super(year, anagrafiche, "SIOPE_ENTRATE", CREATOR);
		}

		@Override
		protected Downloader me() {
			return this;
		}
	}

}
