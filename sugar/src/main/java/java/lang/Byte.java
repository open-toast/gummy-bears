package java.lang;

public abstract class Byte extends Number implements Comparable<Byte> {
    public static int hashCode(byte i) {
        throw new RuntimeException();
    }
}
