package com.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class CodiceGestionaleEntrate extends CodiceGestionale {

	public static class Map extends CodiceGestionale.Map<CodiceGestionaleEntrate> {

	}

	public CodiceGestionaleEntrate(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita) {
		super(codice, nome, comparto, inizioValidita, fineValidita);
	}

	protected static final CodiceGestionale.Creator<CodiceGestionaleEntrate, Map> CREATOR = new CodiceGestionale.Creator<CodiceGestionaleEntrate, Map>() {
		@Override
		public CodiceGestionaleEntrate create(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita) {
			return new CodiceGestionaleEntrate(codice, nome, comparto, inizioValidita, fineValidita);
		}

		@Override
		public Map createMap() {
			return new Map();
		}
	};

	@NotNull
	public static CodiceGestionaleEntrate.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Comparto.Map comparti) {
		return parseAll(records, CREATOR, comparti);
	}

}
