package core.encryptors;

public enum Folders {
    BLOWFISH("blowfish"),
    AES("aes"),
    DES("des"),
    OAEP("oaep")
    ;

    private final String text;

    Folders(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
