package core.encryptors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

public interface Encryptor {
    String PACKAGE = "src/main/resources/";
    void encrypt(InputStream is, OutputStream os) throws Throwable;
    void decrypt(InputStream is, OutputStream os) throws Throwable;
    default void doCopy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
        os.close();
        is.close();
    }
}
