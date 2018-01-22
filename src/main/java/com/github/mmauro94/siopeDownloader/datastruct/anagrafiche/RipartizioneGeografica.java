package com.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@EqualsAndHashCode(of = {"nome"})
public final class RipartizioneGeografica implements GeoItem {


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
	private final ArrayList<@NotNull Regione> regioni = new ArrayList<>();

	public RipartizioneGeografica(@NotNull String nome) {
		this.nome = nome;
	}

	void addRegione(@NotNull Regione regione) {
		regioni.add(regione);
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


	@Override
	@Nullable
	public GeoItem getParent() {
		return null;
	}

	@Override
	@NotNull
	public Collection<@NotNull Regione> getChildren() {
		return getRegioni();
	}

	@NotNull
	public List<@NotNull Regione> getRegioni() {
		return Collections.unmodifiableList(regioni);
	}
}
