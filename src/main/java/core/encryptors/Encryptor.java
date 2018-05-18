package core.encryptors;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

public interface Encryptor {
    String PACKAGE = "src/main/resources/";
    void encrypt(InputStream is, OutputStream os) throws Throwable;
    void decrypt(InputStream is, OutputStream os) throws Throwable;
}
