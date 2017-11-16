package mmauro.siopeDownloader.datastruct.anagrafiche;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mmauro.siopeDownloader.datastruct.AutoMap;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(of = {"codice"})
public abstract class CodiceGestionale {

    public static class Map extends AutoMap<String, CodiceGestionale> {

        @NotNull
        @Override
        protected String getKey(@NotNull CodiceGestionale value) {
            return value.getCodice();
        }
    }

    public interface Creator {
        CodiceGestionale create();
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
    private final Date inizioValidita, fineValidita;

    public CodiceGestionale(@NotNull String codice, @NotNull String nome, @NotNull Comparto comparto, @NotNull Date inizioValidita, @NotNull Date fineValidita) {
        this.codice = codice;
        this.nome = nome;
        this.comparto = comparto;
        this.inizioValidita = inizioValidita;
        this.fineValidita = fineValidita;
    }

    @NotNull
    public static Comune parse(@NotNull CSVRecord record, @NotNull Creator creator, @NotNull Comparto.Map comparti) {
        if (record.size() != 3) {
            throw new IllegalArgumentException("Record size != 3");
        } else {
            String codice = record.get(0);

        }
    }

    @NotNull
    public static Comune.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Creator creator, @NotNull Comparto.Map comparti) {
        return AutoMap.parse(records, x -> parse(x, creator, comparti), Comune.Map::new);
    }
}
