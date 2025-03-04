package desugar.java.lang;

public class DesugarString {
    public static String join(CharSequence c, CharSequence[] s) {
        throw new RuntimeException();
    }

    public static String join(CharSequence c, Iterable i) {
        throw new RuntimeException();
    }
}
