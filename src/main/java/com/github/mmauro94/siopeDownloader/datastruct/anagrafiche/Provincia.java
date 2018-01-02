package com.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EqualsAndHashCode(of = {"codice"})
public final class Provincia {
	public static final class Map extends AutoMap<Integer, Provincia> {

		@NotNull
		@Override
		protected Integer getKey(@NotNull Provincia value) {
			return value.getCodice();
		}
	}

	@Getter
	private final int codice;
	@Getter
	@NotNull
	private final String nome;
	@Getter
	@NotNull
	private final Regione regione;

	private Provincia(int codice, @NotNull String nome, @NotNull Regione regione) {
		this.codice = codice;
		this.nome = nome;
		this.regione = regione;
	}

	@NotNull
	public static Provincia parse(@NotNull CSVRecord record, @NotNull Regione.Map regioni) {
		if (record.size() != 5) {
			throw new IllegalArgumentException("Record size != 5");
		} else {
			final int regione;
			try {
				regione = Integer.parseInt(record.get(1));
			} catch (NumberFormatException nfe) {
				throw new IllegalStateException("Invalid regione: " + record.get(1), nfe);
			}
			final int codice;
			try {
				codice = Integer.parseInt(record.get(3));
			} catch (NumberFormatException nfe) {
				throw new IllegalStateException("Invalid codice: " + record.get(3), nfe);
			}
			final String nome = record.get(4);

			if (codice <= 0) {
				throw new IllegalArgumentException("Invalid codice: " + codice);
			} else if (nome == null || nome.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid nome: " + nome);
			}
			return new Provincia(codice, nome.trim(), regioni.get(regione));
		}
	}

	@NotNull
	public static Provincia.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Regione.Map regioni) {
		return AutoMap.parse(records, x -> parse(x, regioni), Provincia.Map::new);
	}
}
