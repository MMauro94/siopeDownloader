import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AnagraficheDownload {
	public static final NumberFormat PERCENT_FORMAT = new DecimalFormat("0.00%");

	public static void main(String[] args) throws IOException {
		Anagrafiche anagrafiche = Anagrafiche.downloadAnagrafiche(progress -> System.out.println(PERCENT_FORMAT.format(progress)));
		System.out.println("Ripartizioni Geografiche: " + anagrafiche.getRipartizioniGeografiche().size());
		System.out.println("Regioni: " + anagrafiche.getRegioni().size());
		System.out.println("Provincie: " + anagrafiche.getProvincie().size());
		System.out.println("Comuni: " + anagrafiche.getComuni().size());
		System.out.println("--------------------------------------------------");
		System.out.println("Comparti: " + anagrafiche.getComparti().size());
		System.out.println("Sottocomparti: " + anagrafiche.getSottocomparti().size());
		System.out.println("--------------------------------------------------");
		System.out.println("Codici gestionali entrate: " + anagrafiche.getCodiciGestionaliEntrate().size());
		System.out.println("Codici gestionali uscite: " + anagrafiche.getCodiciGestionaliUscite().size());
		System.out.println("--------------------------------------------------");
		System.out.println("Enti: " + anagrafiche.getEnti().size());
	}
}
