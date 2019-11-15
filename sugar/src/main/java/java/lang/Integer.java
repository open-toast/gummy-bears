package java.lang;

public abstract class Integer extends Number implements Comparable<Integer> {
    public static int hashCode(int value) {
        throw new RuntimeException();
    }

    public static int compare(int x, int y) {
        throw new RuntimeException();
    }

    public static int compareUnsigned(int x, int y) {
        throw new RuntimeException();
    }

    public static long toUnsignedLong(int x) {
        throw new RuntimeException();
    }

    public static int divideUnsigned(int dividend, int divisor) {
        throw new RuntimeException();
    }

    public static int remainderUnsigned(int dividend, int divisor) {
        throw new RuntimeException();
    }

    public static int sum(int a, int b) {
        throw new RuntimeException();
    }

    public static int max(int a, int b) {
        throw new RuntimeException();
    }

    public static int min(int a, int b) {
        throw new RuntimeException();
    }
}
