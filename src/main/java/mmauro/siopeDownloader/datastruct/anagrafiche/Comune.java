package mmauro.siopeDownloader.datastruct.anagrafiche;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mmauro.siopeDownloader.datastruct.AutoMap;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EqualsAndHashCode(of = {"comuneId"})
public final class Comune {

    public static final String COMUNE_DI = "COMUNE DI ";

    public static final class Map extends AutoMap<ComuneId, Comune> {

        @NotNull
        @Override
        protected ComuneId getKey(@NotNull Comune value) {
            return value.getComuneId();
        }
    }

    @EqualsAndHashCode(of = {"codice", "provincia"})
    public static class ComuneId {
        @Getter
        private final int codice;
        @Getter
        @NotNull
        private final Provincia provincia;

        public ComuneId(int codice, @NotNull Provincia provincia) {
            this.codice = codice;
            this.provincia = provincia;
        }
    }

    @Getter
    private final ComuneId comuneId;

    @Getter
    @NotNull
    private final String nome;


    public Comune(ComuneId comuneId, @NotNull String nome) {
        this.comuneId = comuneId;
        this.nome = nome;
    }

    @NotNull
    public Provincia getProvincia() {
        return comuneId.getProvincia();
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
            return new Comune(new ComuneId(codice, provincie.get(provincia)), nome.trim());
        }
    }

    @NotNull
    public static Comune.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Provincia.Map provincie) {
        return AutoMap.parse(records, x -> parse(x, provincie), Comune.Map::new);
    }
}
