import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Uscita;
import com.github.mmauro94.siopeDownloader.utils.ClosableIterator;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Example {

	public static final NumberFormat EURO_FORMAT = NumberFormat.getCurrencyInstance(Locale.ITALY);

	public static void main(String[] args) throws IOException {

		//Download anagrafiche
		final Anagrafiche a = new Anagrafiche.Downloader().download();

		//Download uscite e raggruppamento per provincia
		final Map<Provincia, Long> map = new HashMap<>();
		try (ClosableIterator<Uscita> it = new Uscita.Downloader(2017, a).setOnProgressListener(System.out::println).download()) {
			while (it.hasNext()) {
				final Uscita u = it.next();
				final Provincia p = u.getEnte().getComune().getProvincia();
				map.put(p, map.getOrDefault(p, 0L) + u.getAmount());
			}
		}

		//Ordinamento risultati
		final ArrayList<Map.Entry<Provincia, Long>> entries = new ArrayList<>(map.entrySet());
		Comparator<Map.Entry<Provincia, Long>> comparing = Comparator.comparing(Map.Entry::getValue);
		entries.sort(comparing.reversed());

		//Print a schermo risultati
		for (Map.Entry<Provincia, Long> entry : entries) {
			final BigDecimal euros = new BigDecimal(entry.getValue()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_UNNECESSARY);
			System.out.println(entry.getKey().getNome() + ": " + EURO_FORMAT.format(euros));
		}
	}
}