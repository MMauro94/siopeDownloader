package org.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class CodiceGestionaleUscite extends CodiceGestionale {

	public static class Map extends CodiceGestionale.Map<CodiceGestionaleUscite> {

	}

	protected CodiceGestionaleUscite(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita) {
		super(codice, nome, comparto, inizioValidita, fineValidita);
	}

	protected static final Creator<CodiceGestionaleUscite, Map> CREATOR = new Creator<CodiceGestionaleUscite, Map>() {
		@Override
		public CodiceGestionaleUscite create(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita) {
			return new CodiceGestionaleUscite(codice, nome, comparto, inizioValidita, fineValidita);
		}

		@Override
		public Map createMap() {
			return new Map();
		}
	};

	@NotNull
	public static CodiceGestionaleUscite.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Comparto.Map comparti) {
		return parseAll(records, CREATOR, comparti);
	}

}
