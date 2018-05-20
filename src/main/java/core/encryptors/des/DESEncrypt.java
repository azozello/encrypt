package core.encryptors.des;

import core.encryptors.Encryptor;
import core.encryptors.Folders;
import core.encryptors.aes.AESEncrypt;
import core.exceptions.CannotCreateEncryptException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class DESEncrypt implements Encryptor {

    private static DESEncrypt instance;
    private Key secretKey;

    public static DESEncrypt getInstance() throws CannotCreateEncryptException {
        if (instance == null) {
            instance = new DESEncrypt();
        }
        return instance;
    }

    private DESEncrypt() throws CannotCreateEncryptException {
        try {
            File folder = new File(PACKAGE + Folders.DES);
            if (!Files.exists(folder.toPath())) {
                folder.mkdir();
            }
            File key = new File(PACKAGE + Folders.DES + "/.acab");
            if (key.exists()) {
                try (FileInputStream fileIn = new FileInputStream(key);
                     ObjectInputStream in = new ObjectInputStream(fileIn)) {
                    this.secretKey = (Key) in.readObject();
                } catch (Exception e) {
                    throw new NoSuchAlgorithmException();
                }
            } else {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
                keyGenerator.init(56);
                this.secretKey = keyGenerator.generateKey();
                try (FileOutputStream fileOut = new FileOutputStream(key);
                     ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                    out.writeObject(this.secretKey);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new NoSuchAlgorithmException();
                }
            }
        } catch (NoSuchAlgorithmException nse) {
            nse.printStackTrace();
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
        Cipher cipher = Cipher.getInstance("DES/CFB/NoPadding");

        byte[] iv = {1, 9, 9, 8, 0, 3, 2, 2};
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
}