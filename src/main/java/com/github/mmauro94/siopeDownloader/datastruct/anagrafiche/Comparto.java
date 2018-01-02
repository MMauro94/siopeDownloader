package com.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EqualsAndHashCode(of = {"codice"})
public final class Comparto {

	public final static class Map extends AutoMap<String, Comparto> {

		@NotNull
		@Override
		protected String getKey(@NotNull Comparto value) {
			return value.getCodice();
		}
	}

	@NotNull
	@Getter
	private final String codice, nome;

	private Comparto(@NotNull String codice, @NotNull String nome) {
		this.codice = codice;
		this.nome = nome;
	}

	@NotNull
	public static Comparto parse(@NotNull CSVRecord record) {
		if (record.size() != 2) {
			throw new IllegalArgumentException("Record size != 2");
		} else {
			final String codice = record.get(0);
			final String nome = record.get(1);

			if (codice == null || codice.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid codice: " + codice);
			} else if (nome == null || nome.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid nome: " + nome);
			}
			return new Comparto(codice.trim(), nome.trim());
		}
	}

	@NotNull
	public static Map parseAll(@NotNull List<CSVRecord> records) {
		return AutoMap.parse(records, Comparto::parse, Map::new);
	}
}
