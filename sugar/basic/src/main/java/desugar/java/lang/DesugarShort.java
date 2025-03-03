package desugar.java.lang;

public final class DesugarShort {
    public static int compare(short x, short y) {
        throw new RuntimeException();
    }

    public static int compareUnsigned(short x, short y) {
        throw new RuntimeException();
    }

    public static int hashCode(short i) {
        throw new RuntimeException();
    }

    public static long toUnsignedLong(short i) {
        throw new RuntimeException();
    }

    public static int toUnsignedInt(short i) {
        throw new RuntimeException();
    }
}
