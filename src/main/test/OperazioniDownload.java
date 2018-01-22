import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Entrata;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Uscita;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OperazioniDownload {

	public static final NumberFormat PERCENT_FORMAT = new DecimalFormat("0.00%");

	public static void main(String[] args) throws IOException {
		System.out.print("Download anagrafiche...");
		final Anagrafiche a = Anagrafiche.downloadAnagrafiche();
		System.out.println("OK");
		System.out.println("--------------------------------------------------");
		System.out.println("Download entrate...");
		for(Entrata e : Entrata.downloadEntrate(2017, a, progress -> System.out.println(PERCENT_FORMAT.format(progress)))) {
			//iterate
		}
		System.out.println("Entrate OK");
		System.out.println("--------------------------------------------------");
		System.out.println("Download uscite...");
		for(Uscita u: Uscita.downloadUscite(2017, a, progress -> System.out.println(PERCENT_FORMAT.format(progress)))) {
			//iterate
		}
		System.out.println("Uscite OK");

	}
}
