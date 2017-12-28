package org.github.mmauro94.siopeDownloader.datastruct.operazioni;

import org.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import org.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleUscite;
import org.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;

public final class Uscita extends Operazione<CodiceGestionaleUscite> {

	private Uscita(@NotNull Ente ente, int year, int month, @NotNull CodiceGestionaleUscite codiceGestionale, @NotNull BigDecimal amount) {
		super(ente, year, month, codiceGestionale, amount);
	}

	private final static Creator<CodiceGestionaleUscite, Uscita> CREATOR = (Creator<CodiceGestionaleUscite, Uscita>) (anagrafiche, ente, year, month, codiceGestionale, amount) -> new Uscita(ente, year, month, anagrafiche.getCodiciGestionaliUscite().get(codiceGestionale), amount);

	@NotNull
	public static Uscita parse(@NotNull CSVRecord record, @NotNull Anagrafiche anagrafiche) {
		return Operazione.parse(record, anagrafiche, CREATOR);
	}

	@NotNull
	public static Iterable<Uscita> downloadUscite(int year, @NotNull Anagrafiche anagrafiche) throws IOException {
		return download(year, anagrafiche, "SIOPE_USCITE", CREATOR);
	}
}
