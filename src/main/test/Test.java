import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Entrata;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Uscita;
import com.github.mmauro94.siopeDownloader.utils.ClosableIterator;
import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;

public class Test {

	static class ControlloreDelProgressamento implements OnProgressListener {
		private float lastProgress = 0;
		private Long startTime = null;
		private long[] tookTimes = new long[10];

		@Override
		public void onProgress(float progress) {
			//System.out.println(PERCENT_FORMAT.format(progress));
			final long now = System.currentTimeMillis();
			if (startTime == null) {
				startTime = now;
			} else {
				final int lastIndex = (int) (lastProgress * 10);
				final int currentIndex = (int) (progress * 10);
				if (lastIndex != currentIndex) {
					tookTimes[lastIndex] = now - startTime;
					startTime = now;
				}
			}
			lastProgress = progress;
		}

		public long[] getTookTimes() {
			return Arrays.copyOf(tookTimes, tookTimes.length);
		}

		public float[] getRelativeTimes() {
			final float[] ret = new float[tookTimes.length];
			long sum = 0;
			for (int i = 0; i < tookTimes.length; i++) {
				sum += tookTimes[i];
			}
			for (int i = 0; i < tookTimes.length; i++) {
				ret[i] = (tookTimes[i] / (float) sum) * tookTimes.length;
			}
			return ret;
		}

		@Override
		public String toString() {
			return super.toString();
		}
	}

	public static final NumberFormat PERCENT_FORMAT = new DecimalFormat("0.00%");

	public static void main(String[] args) throws IOException, InterruptedException {
		final Anagrafiche a = downloadAnagrafiche();
		System.out.println("--------------------------------------------------");
		downloadOperazioni(new Entrata.Downloader(2017, a));
		System.out.println("--------------------------------------------------");
		downloadOperazioni(new Uscita.Downloader(2017, a));
	}

	private static void downloadOperazioni(@NotNull Operazione.Downloader<?, ?, ?> downloader) throws IOException, InterruptedException {
		long t = System.currentTimeMillis();
		ControlloreDelProgressamento cdp = new ControlloreDelProgressamento();
		downloader.setOnProgressListener(cdp);
		System.out.println("Download...");
		int i = 0;
		try (ClosableIterator<? extends Operazione<?>> download = downloader.download(new File("temp.zip"))) {
			while(download.hasNext()) {
				download.next();
				i++;
			}
		}
		t = System.currentTimeMillis() - t;
		System.out.println("Download " + i + " operations took " + t + "ms");
		System.out.println(Arrays.toString(cdp.getTookTimes()));
		System.out.println(Arrays.toString(cdp.getRelativeTimes()));
	}

	public static Anagrafiche downloadAnagrafiche() throws IOException {
		long t = System.currentTimeMillis();
		System.out.println("Download anagrafiche...");

		Anagrafiche anagrafiche = new Anagrafiche.Downloader().setOnProgressListener(progress -> System.out.println(PERCENT_FORMAT.format(progress))).download();
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
		t = System.currentTimeMillis() - t;
		System.out.println("Download anagrafiche took " + t + "ms");
		return anagrafiche;
	}
}
