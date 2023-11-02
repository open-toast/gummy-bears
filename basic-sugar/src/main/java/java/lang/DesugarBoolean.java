package java.lang;

public final class DesugarBoolean {
    public static int hashCode(boolean b) {
        throw new RuntimeException();
    }

    public static boolean logicalAnd(boolean a, boolean b) {
        throw new RuntimeException();
    }

    public static boolean logicalOr(boolean a, boolean b) {
        throw new RuntimeException();
    }

    public static boolean logicalXor(boolean a, boolean b) {
        throw new RuntimeException();
    }
}
