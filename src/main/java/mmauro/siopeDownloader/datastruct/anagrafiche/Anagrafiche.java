package mmauro.siopeDownloader.datastruct.anagrafiche;

import lombok.Getter;
import mmauro.siopeDownloader.download.SiopeZipDownloader;
import mmauro.siopeDownloader.utils.ParseUtils;
import mmauro.siopeDownloader.utils.ReaderUtils;
import mmauro.siopeDownloader.utils.URLUtils;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Anagrafiche {

    public static final String FILE_LOCATION = "/Siope2Web/documenti/siope2/open/last/SIOPE_ANAGRAFICHE.zip";

    @NotNull
    @Getter
    private final Comparto.Map comparti;
    @NotNull
    @Getter
    private final Sottocomparto.Map sottocomparti;
    @NotNull
    @Getter
    private final RipartizioneGeografica.Map ripartizioniGeografiche;
    @NotNull
    @Getter
    private final Regione.Map regioni;
    @NotNull
    @Getter
    private final Provincia.Map provincie;
    @NotNull
    @Getter
    private final Comune.Map comuni;
    @NotNull
    @Getter
    private final Ente.Map enti;
    @NotNull
    @Getter
    private final CodiceGestionaleEntrate.Map codiciGestionaliEntrate;
    @NotNull
    @Getter
    private final CodiceGestionaleUscite.Map codiciGestionaliUscite;

    private Anagrafiche(@NotNull Comparto.Map comparti, @NotNull Sottocomparto.Map sottocomparti, @NotNull RipartizioneGeografica.Map ripartizioniGeografiche, @NotNull Regione.Map regioni, @NotNull Provincia.Map provincie, @NotNull Comune.Map comuni, @NotNull Ente.Map enti, @NotNull CodiceGestionaleEntrate.Map codiciGestionaliEntrate, @NotNull CodiceGestionaleUscite.Map codiciGestionaliUscite) {
        this.comparti = comparti;
        this.sottocomparti = sottocomparti;
        this.ripartizioniGeografiche = ripartizioniGeografiche;
        this.regioni = regioni;
        this.provincie = provincie;
        this.comuni = comuni;
        this.enti = enti;
        this.codiciGestionaliEntrate = codiciGestionaliEntrate;
        this.codiciGestionaliUscite = codiciGestionaliUscite;
    }

    @NotNull
    public static Anagrafiche downloadAnagrafiche() throws IOException {
        ZipInputStream download = new SiopeZipDownloader(new URL(URLUtils.SIOPE_WEBSITE + FILE_LOCATION)).download();
        ZipEntry entry;


        List<CSVRecord> compartiRecords = null, sottocompartiRecords = null, regProvRecords = null, comuniRecords = null, entiRecords = null, codGestEntRecords = null, codGestUscRecords = null;
        while ((entry = download.getNextEntry()) != null) {
            final List<CSVRecord> records = CSVParser.parse(ReaderUtils.readAll(new InputStreamReader(download)), ParseUtils.CSV_FORMAT).getRecords();

            switch (entry.getName().substring(0, entry.getName().indexOf('.'))) {
                case "ANAG_COMPARTI":
                    compartiRecords = records;
                    break;
                case "ANAG_SOTTOCOMPARTI":
                    sottocompartiRecords = records;
                    break;
                case "ANAG_REG_PROV":
                    regProvRecords = records;
                    break;
                case "ANAG_COMUNI":
                case "ANAGRAFE_COMUNI":
                    comuniRecords = records;
                    break;
                case "ANAG_ENTI_SIOPE":
                    entiRecords = records;
                    break;
                case "ANAG_CODGEST_ENTRATE":
                    codGestEntRecords = records;
                    break;
                case "ANAG_CODGEST_USCITE":
                    codGestUscRecords = records;
                    continue;
                default:
                    throw new IllegalStateException("Unexpected file: " + entry.getName());
            }
        }
        if (compartiRecords == null) {
            throw new IllegalStateException("File ANAG_COMPARTI not found");
        } else if (sottocompartiRecords == null) {
            throw new IllegalStateException("File ANAG_SOTTOCOMPARTI not found");
        } else if (regProvRecords == null) {
            throw new IllegalStateException("File ANAG_REG_PROV not found");
        } else if (comuniRecords == null) {
            throw new IllegalStateException("File ANAGRAFE_COMUNI/ANAG_COMUNI not found");
        }else if (entiRecords == null) {
            throw new IllegalStateException("File ANAG_ENTI_SIOPE not found");
        } else if(codGestEntRecords == null) {
            throw new IllegalStateException("File ANAG_CODGEST_ENTRATE not found");
        } else if(codGestUscRecords == null) {
            throw new IllegalStateException("File ANAG_CODGEST_USCITE not found");
        }

        final Comparto.Map comparti = Comparto.parseAll(compartiRecords);
        final Sottocomparto.Map sottocomparti = Sottocomparto.parseAll(sottocompartiRecords, comparti);
        final RipartizioneGeografica.Map ripartizioniGeografiche = RipartizioneGeografica.parseAll(regProvRecords);
        final Regione.Map regioni = Regione.parseAll(regProvRecords, ripartizioniGeografiche);
        final Provincia.Map provincie = Provincia.parseAll(regProvRecords, regioni);
        final Comune.Map comuni = Comune.parseAll(comuniRecords, provincie);
        final Ente.Map enti = Ente.parseAll(entiRecords, comuni, provincie, sottocomparti);
        final CodiceGestionaleEntrate.Map codiciGestionaliEntrate = CodiceGestionaleEntrate.parseAll(codGestEntRecords, comparti);
        final CodiceGestionaleUscite.Map codiciGestionaliUscite = CodiceGestionaleUscite.parseAll(codGestUscRecords, comparti);

        return new Anagrafiche(comparti, sottocomparti, ripartizioniGeografiche, regioni, provincie, comuni, enti, codiciGestionaliEntrate, codiciGestionaliUscite);
    }
}
