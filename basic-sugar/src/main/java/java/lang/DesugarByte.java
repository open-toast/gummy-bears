package java.lang;

public final class DesugarByte {
    public static int hashCode(byte i) {
        throw new RuntimeException();
    }
}
