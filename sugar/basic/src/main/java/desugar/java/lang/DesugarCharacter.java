package desugar.java.lang;

public final class DesugarCharacter {
    public static int compare(char a, char b) {
        throw new RuntimeException();
    }

    public static int hashCode(char a) {
        throw new RuntimeException();
    }

    public static String toString(int i) {
        throw new RuntimeException();
    }
}
