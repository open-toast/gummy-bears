package java.lang;

public abstract class Short extends Number implements Comparable<Short> {
    public static int hashCode(short i) {
        throw new RuntimeException();
    }
}
