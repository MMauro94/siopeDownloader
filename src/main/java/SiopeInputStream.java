import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class SiopeInputStream extends InputStream {

    @NotNull
    private final ZipInputStream zis;

    public SiopeInputStream(@NotNull ZipInputStream zis) {
        this.zis = zis;
    }

    public int read() throws IOException {
        return zis.read();
    }

    @Override
    public void close() throws IOException {
        zis.close();
    }
}
