package desugar.java.lang;

public final class DesugarLong {
    public static int hashCode(long value) {
        throw new RuntimeException();
    }

    public static int compare(long x, long y) {
        throw new RuntimeException();
    }

    public static int compareUnsigned(long x, long y) {
        throw new RuntimeException();
    }

    public static long divideUnsigned(long dividend, long divisor) {
        throw new RuntimeException();
    }

    public static long remainderUnsigned(long dividend, long divisor) {
        throw new RuntimeException();
    }

    public static long sum(long a, long b) {
        throw new RuntimeException();
    }

    public static long max(long a, long b) {
        throw new RuntimeException();
    }

    public static long min(long a, long b) {
        throw new RuntimeException();
    }

    public static String toUnsignedString(long a) {
        throw new RuntimeException();
    }

    public static String toUnsignedString(long a, int b) {
        throw new RuntimeException();
    }

    public static long parseUnsignedLong(String a) {
        throw new RuntimeException();
    }

    public static long parseUnsignedLong(String a, int b) {
        throw new RuntimeException();
    }
}
