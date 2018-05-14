package core.encryptors.blowfish;

import core.encryptors.Encryptor;
import core.exceptions.CannotCreateEncryptException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class BlowfishEncrypt implements Encryptor {

    public static final String PACKAGE = "src/main/resources/";

    private static BlowfishEncrypt instance;
    private Key secretKey;

    public static BlowfishEncrypt getInstance() throws CannotCreateEncryptException {
        if (instance == null) {
            instance = new BlowfishEncrypt();
        }
        return instance;
    }

    private BlowfishEncrypt() throws CannotCreateEncryptException {
        try {
            File key = new File(PACKAGE + ".acab");
            if (key.exists()) {
                try (FileInputStream fileIn = new FileInputStream(key);
                     ObjectInputStream in = new ObjectInputStream(fileIn)){
                    this.secretKey = (Key) in.readObject();
                } catch (Exception e) {
                    throw new NoSuchAlgorithmException();
                }
            } else {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
                keyGenerator.init(128);
                this.secretKey = keyGenerator.generateKey();
                try (FileOutputStream fileOut = new FileOutputStream(key);
                     ObjectOutputStream out = new ObjectOutputStream(fileOut)){
                    out.writeObject(this.secretKey);
                } catch (Exception e) {
                    throw new NoSuchAlgorithmException();
                }
            }
        } catch (NoSuchAlgorithmException nse) {
            throw new CannotCreateEncryptException();
        }
    }

    public void encrypt(InputStream is, OutputStream os) throws Throwable {
        encryptOrDecrypt(Cipher.ENCRYPT_MODE, is, os);
    }

    public void decrypt(InputStream is, OutputStream os) throws Throwable {
        encryptOrDecrypt(Cipher.DECRYPT_MODE, is, os);
    }

    private void encryptOrDecrypt(int mode, InputStream is, OutputStream os) throws Throwable {
        Cipher cipher = Cipher.getInstance("Blowfish/CFB/NoPadding");

        byte[] iv = {1, 4, 8, 8, 0, 2, 2, 8};
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        if (mode == Cipher.ENCRYPT_MODE) {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, ivspec);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            doCopy(cis, os);
        } else if (mode == Cipher.DECRYPT_MODE) {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, ivspec);
            CipherOutputStream cos = new CipherOutputStream(os, cipher);
            doCopy(is, cos);
        }
    }

    private void doCopy(InputStream is, OutputStream os) throws IOException {
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
