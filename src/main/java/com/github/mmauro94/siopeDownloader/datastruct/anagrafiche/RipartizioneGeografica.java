package com.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@EqualsAndHashCode(of = {"nome"})
public final class RipartizioneGeografica {

	public final static class Map extends AutoMap<String, RipartizioneGeografica> {

		@NotNull
		@Override
		protected String getKey(@NotNull RipartizioneGeografica value) {
			return value.getNome();
		}
	}

	@NotNull
	@Getter
	private final String nome;

	private RipartizioneGeografica(@NotNull String nome) {
		this.nome = nome;
	}

	@NotNull
	public static RipartizioneGeografica parse(@NotNull CSVRecord record) {
		if (record.size() != 5) {
			throw new IllegalArgumentException("Record size != 5");
		} else {
			final String nome = record.get(0);

			if (nome == null || nome.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid nome: " + nome);
			}
			return new RipartizioneGeografica(nome.trim());
		}
	}

	@NotNull
	public static RipartizioneGeografica.Map parseAll(@NotNull List<CSVRecord> records) {
		return AutoMap.parse(records, RipartizioneGeografica::parse, RipartizioneGeografica.Map::new);
	}
}
