package mmauro.siopeDownloader.datastruct.anagrafiche;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mmauro.siopeDownloader.datastruct.AutoMap;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EqualsAndHashCode(of = {"codice"})
public final class Comune {

    public static final String COMUNE_DI = "COMUNE DI ";

    public static final class Map extends AutoMap<Integer, Comune> {

        @NotNull
        @Override
        protected Integer getKey(@NotNull Comune value) {
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
    private final Provincia provincia;

    private Comune(int codice, @NotNull String nome, @NotNull Provincia provincia) {
        this.codice = codice;
        this.nome = nome;
        this.provincia = provincia;
    }

    @NotNull
    public static Comune parse(@NotNull CSVRecord record, @NotNull Provincia.Map provincie) {
        if (record.size() != 3) {
            throw new IllegalArgumentException("Record size != 3");
        } else {
            final int codice;
            try {
                codice = Integer.parseInt(record.get(0));
            } catch (NumberFormatException nfe) {
                throw new IllegalStateException("Invalid codice: " + record.get(1), nfe);
            }
            String nome = record.get(1);
            final int provincia;
            try {
                provincia = Integer.parseInt(record.get(2));
            } catch (NumberFormatException nfe) {
                throw new IllegalStateException("Invalid provincia: " + record.get(2), nfe);
            }

            if (codice <= 0) {
                throw new IllegalArgumentException("Invalid codice: " + codice);
            } else if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid nome: " + nome);
            }
            if (nome.startsWith(COMUNE_DI)) {
                nome = nome.substring(COMUNE_DI.length(), nome.length());
            }
            return new Comune(codice, nome.trim(), provincie.get(provincia));
        }
    }

    @NotNull
    public static Comune.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Provincia.Map provincie) {
        return AutoMap.parse(records, x -> parse(x, provincie), Comune.Map::new);
    }
}
