package desugar.java.lang;

public final class DesugarFloat {
    public static int hashCode(float d) {
        throw new RuntimeException();
    }

    public static float max(float a, float b) {
        throw new RuntimeException();
    }

    public static float min(float a, float b) {
        throw new RuntimeException();
    }

    public static float sum(float a, float b) {
        throw new RuntimeException();
    }

    public static boolean isFinite(float a) {
        throw new RuntimeException();
    }
}
