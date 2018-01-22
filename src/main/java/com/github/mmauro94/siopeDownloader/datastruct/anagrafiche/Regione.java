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

@EqualsAndHashCode(of = {"codice"})
public final class Regione implements GeoItem {

	public static final class Map extends AutoMap<Integer, Regione> {

		@NotNull
		@Override
		protected Integer getKey(@NotNull Regione value) {
			return value.getCodice();
		}
	}

	@Getter
	private final int codice;
	@NotNull
	@Getter
	private final String nome;
	@NotNull
	@Getter
	private final RipartizioneGeografica ripartizioneGeografica;
	private final ArrayList<@NotNull Provincia> provincie = new ArrayList<>();

	public Regione(int codice, @NotNull String nome, @NotNull RipartizioneGeografica ripartizioneGeografica) {
		this.codice = codice;
		this.nome = nome;
		this.ripartizioneGeografica = ripartizioneGeografica;
		ripartizioneGeografica.addRegione(this);
	}

	void addProvincia(@NotNull Provincia p) {
		provincie.add(p);
	}

	@NotNull
	public static Regione parse(@NotNull CSVRecord record, @NotNull RipartizioneGeografica.Map ripartizioniGeografiche) {
		if (record.size() != 5) {
			throw new IllegalArgumentException("Record size != 5");
		} else {
			final String ripartizioneGeografica = record.get(0);
			final int codice;
			try {
				codice = Integer.parseInt(record.get(1));
			} catch (NumberFormatException nfe) {
				throw new IllegalStateException("Invalid codice: " + record.get(1), nfe);
			}
			final String nome = record.get(2);

			if (codice <= 0) {
				throw new IllegalArgumentException("Invalid codice: " + codice);
			} else if (nome == null || nome.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid nome: " + nome);
			}
			return new Regione(codice, nome.trim(), ripartizioniGeografiche.get(ripartizioneGeografica));
		}
	}

	@NotNull
	public static Regione.Map parseAll(@NotNull List<CSVRecord> records, @NotNull RipartizioneGeografica.Map ripartizioniGeografiche) {
		return AutoMap.parse(records, x -> parse(x, ripartizioniGeografiche), Regione.Map::new);
	}

	@Override
	@NotNull
	public RipartizioneGeografica getParent() {
		return ripartizioneGeografica;
	}

	@Override
	@NotNull
	public Collection<@NotNull Provincia> getChildren() {
		return getProvincie();
	}

	@NotNull
	public List<@NotNull Provincia> getProvincie() {
		return Collections.unmodifiableList(provincie);
	}
}
