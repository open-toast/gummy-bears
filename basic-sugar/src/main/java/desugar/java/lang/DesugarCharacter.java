package desugar.java.lang;

public final class DesugarCharacter {
    public static int compare(char a, char b) {
        throw new RuntimeException();
    }

    public static int hashCode(char i) {
        throw new RuntimeException();
    }
}
