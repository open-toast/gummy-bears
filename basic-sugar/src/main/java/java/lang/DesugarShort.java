package java.lang;

public final class DesugarShort {
    public static int hashCode(short i) {
        throw new RuntimeException();
    }
}
