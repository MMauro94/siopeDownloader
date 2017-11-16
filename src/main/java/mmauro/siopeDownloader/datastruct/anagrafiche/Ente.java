package mmauro.siopeDownloader.datastruct.anagrafiche;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mmauro.siopeDownloader.datastruct.AutoMap;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(of = {"codice"})
public class Ente {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static class Map extends AutoMap<String, Ente> {

        @NotNull
        @Override
        protected String getKey(@NotNull Ente value) {
            return value.getCodice();
        }
    }

    @NotNull
    @Getter
    private final String codice;
    @NotNull
    @Getter
    private final Date dataInclusione;
    @Nullable
    private final Date dataEsclusione;
    @NotNull
    @Getter
    private final String codiceFiscale;
    @NotNull
    @Getter
    private final String nome;
    @NotNull
    @Getter
    private final Comune comune;
    @Nullable
    private final Integer numeroAbitanti;
    @NotNull
    @Getter
    private final Sottocomparto sottocomparto;

    private Ente(@NotNull String codice, @NotNull Date dataInclusione, @Nullable Date dataEsclusione, @NotNull String codiceFiscale, @NotNull String nome, @NotNull Comune comune, @Nullable Integer numeroAbitanti, @NotNull Sottocomparto sottocomparto) {
        this.codice = codice;
        this.dataInclusione = dataInclusione;
        this.dataEsclusione = dataEsclusione;
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.comune = comune;
        this.numeroAbitanti = numeroAbitanti;
        this.sottocomparto = sottocomparto;
    }


    @Nullable
    @Contract(pure = true)
    public Date optDataEsclusione() {
        return dataEsclusione;
    }

    @Contract(pure = true)
    public boolean hasDataEsclusione() {
        return dataEsclusione != null;
    }

    @NotNull
    @Contract(pure = true)
    public Date getDataEsclusione() {
        if (dataEsclusione == null) {
            throw new IllegalStateException("dataEsclusione == null");
        }
        return dataEsclusione;
    }

    @Nullable
    @Contract(pure = true)
    public Integer optNumeroAbitanti() {
        return numeroAbitanti;
    }

    @Contract(pure = true)
    public boolean hasNumeroAbitanti() {
        return numeroAbitanti != null;
    }

    @Contract(pure = true)
    public int getNumeroAbitanti() {
        if (numeroAbitanti == null) {
            throw new IllegalStateException("numeroAbitanti == null");
        }
        return numeroAbitanti;
    }

    @NotNull
    public static Ente parse(@NotNull CSVRecord record, @NotNull Comune.Map comuni, @NotNull Sottocomparto.Map sottocomparti) {
        if (record.size() != 9) {
            throw new IllegalArgumentException("Record size != 9");
        } else {
            String codice = record.get(0);

            Date dataInclusione, dataEsclusione;
            synchronized (DATE_FORMAT) {
                try {
                    dataInclusione = DATE_FORMAT.parse(record.get(1));
                } catch (ParseException e) {
                    throw new IllegalStateException("Illegal inserimento date: " + record.get(1), e);
                }
                try {
                    dataEsclusione = DATE_FORMAT.parse(record.get(2));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dataEsclusione);
                    if (cal.get(Calendar.YEAR) == 9999 && cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) == 31) {
                        dataEsclusione = null;
                    }
                } catch (ParseException e) {
                    throw new IllegalStateException("Illegal inserimento date: " + record.get(2), e);
                }
            }
            final String codiceFiscale = record.get(3);
            final String nome = record.get(4);
            final Comune comune;
            try {
                comune = comuni.get(Integer.parseInt(record.get(5)));
            } catch (NumberFormatException nfe) {
                throw new IllegalStateException("Invalid codice comune: " + record.get(5), nfe);
            }
            try {
                final int provincia = Integer.parseInt(record.get(6));
                if (provincia != comune.getProvincia().getCodice()) {
                    System.err.println("provincia mismatch: comune " + comune.getNome() + " has provincia " + comune.getProvincia().getNome() + " (" + comune.getProvincia().getCodice() + ") but found " + provincia);
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalStateException("Invalid codice provincia: " + record.get(6), nfe);
            }
            final Integer numAbitanti;
            try {
                String str = record.get(7);
                if (str == null || str.trim().isEmpty()) {
                    numAbitanti = null;
                } else {
                    numAbitanti = Integer.parseInt(str);
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalStateException("Invalid numAbitanti: " + record.get(7), nfe);
            }
            final Sottocomparto sottocomparto = sottocomparti.get(record.get(8));

            if (codice == null || codice.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid codice: " + codice);
            } else if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid codiceFiscale: " + codiceFiscale);
            } else if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid nome: " + nome);
            }

            return new Ente(codice, dataInclusione, dataEsclusione, codiceFiscale, nome, comune, numAbitanti, sottocomparto);
        }
    }

    @NotNull
    public static Ente.Map parseAll(@NotNull List<CSVRecord> records, @NotNull Comune.Map comuni, @NotNull Sottocomparto.Map sottocomparti) {
        return AutoMap.parse(records, x -> parse(x, comuni, sottocomparti), Ente.Map::new);
    }
}
