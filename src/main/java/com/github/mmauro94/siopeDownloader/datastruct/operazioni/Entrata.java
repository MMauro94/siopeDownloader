package com.github.mmauro94.siopeDownloader.datastruct.operazioni;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;

public final class Entrata extends Operazione<CodiceGestionaleEntrate> {

	private Entrata(@NotNull Ente ente, int year, int month, @NotNull CodiceGestionaleEntrate codiceGestionale, @NotNull BigDecimal amount) {
		super(ente, year, month, codiceGestionale, amount);
	}

	private final static Operazione.Creator<CodiceGestionaleEntrate, Entrata> CREATOR = (Creator<CodiceGestionaleEntrate, Entrata>) (anagrafiche, ente, year, month, codiceGestionale, amount) -> new Entrata(ente, year, month, anagrafiche.getCodiciGestionaliEntrate().get(codiceGestionale), amount);

	@NotNull
	public static Iterable<Entrata> downloadEntrate(int year, @NotNull Anagrafiche anagrafiche) throws IOException {
		return download(year, anagrafiche, "SIOPE_ENTRATE", CREATOR);
	}
}
