package org.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import org.github.mmauro94.siopeDownloader.utils.ParseUtils;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(of = {"codice"})
public abstract class CodiceGestionale {

	public static abstract class Map<T extends CodiceGestionale> extends AutoMap<String, T> {

		@NotNull
		@Override
		protected String getKey(@NotNull CodiceGestionale value) {
			return value.getCodice();
		}
	}

	public interface Creator<T extends CodiceGestionale, M extends Map<T>> {
		T create(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita);

		M createMap();
	}

	@NotNull
	@Getter
	private final String codice;
	@NotNull
	@Getter
	private final String nome;
	@NotNull
	@Getter
	private final Comparto comparto;
	@NotNull
	@Getter
	private final Date inizioValidita;
	@Nullable
	@Getter
	private final Date fineValidita;

	protected CodiceGestionale(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita) {
		this.codice = codice;
		this.nome = nome;
		this.comparto = comparto;
		this.inizioValidita = inizioValidita;
		this.fineValidita = fineValidita;
	}

	@NotNull
	public static <T extends CodiceGestionale> T parse(@NotNull CSVRecord record, @NotNull Creator<T, ?> creator, @NotNull Comparto.Map comparti) {
		if (record.size() != 5) {
			throw new IllegalArgumentException("Record size != 5");
		} else {
			final String codice = record.get(0);
			final String codiceCategoriaComparto = record.get(1);
			final String nome = record.get(2);
			Date dataInizio, dataFine;
			synchronized (ParseUtils.DATE_FORMAT) {
				try {
					dataInizio = ParseUtils.DATE_FORMAT.parse(record.get(3));
				} catch (ParseException e) {
					throw new IllegalStateException("Illega l inizio validità date: " + record.get(1), e);
				}
				try {
					dataFine = ParseUtils.DATE_FORMAT.parse(record.get(4));
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataFine);
					if (cal.get(Calendar.YEAR) == 9999 && cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) == 31) {
						dataFine = null;
					}
				} catch (ParseException e) {
					throw new IllegalStateException("Illegal inserimento date: " + record.get(2), e);
				}
			}
			return creator.create(codice, nome, comparti.get(codiceCategoriaComparto), dataInizio, dataFine);
		}
	}

	@NotNull
	public static <T extends CodiceGestionale, M extends Map<T>> M parseAll(@NotNull List<CSVRecord> records, @NotNull Creator<T, M> creator, @NotNull Comparto.Map comparti) {
		return AutoMap.parse(records, x -> parse(x, creator, comparti), creator::createMap);
	}
}
