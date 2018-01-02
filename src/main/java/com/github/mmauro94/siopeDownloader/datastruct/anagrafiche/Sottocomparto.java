package com.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EqualsAndHashCode(of = {"codice"})
public final class Sottocomparto {

	public final static class Map extends AutoMap<String, Sottocomparto> {

		@NotNull
		@Override
		protected String getKey(@NotNull Sottocomparto value) {
			return value.getCodice();
		}
	}

	@Getter
	@NotNull
	private final String codice, nome;
	@Getter
	@NotNull
	private final Comparto comparto;

	private Sottocomparto(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto) {
		this.codice = codice;
		this.nome = nome;
		this.comparto = comparto;
	}

	@NotNull
	public static Sottocomparto parse(@NotNull CSVRecord record, @NotNull Comparto.Map comparti) {
		if (record.size() != 3) {
			throw new IllegalArgumentException("Record size != 3");
		} else {
			final String codice = record.get(0);
			final String nome = record.get(1);
			final String comparto = record.get(2);

			if (codice == null || codice.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid codice: " + codice);
			} else if (nome == null || nome.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid nome: " + nome);
			} else if (comparto == null || comparto.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid comparto: " + comparto);
			}
			return new Sottocomparto(codice.trim(), nome.trim(), comparti.get(comparto.trim()));
		}
	}

	@NotNull
	public static Sottocomparto.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Comparto.Map comparti) {
		return AutoMap.parse(records, x -> parse(x, comparti), Sottocomparto.Map::new);
	}
}
