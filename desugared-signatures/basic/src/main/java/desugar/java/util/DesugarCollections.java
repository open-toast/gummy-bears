package desugar.java.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.ListIterator;

public class DesugarCollections {
    public static Iterator emptyIterator() {
        throw new RuntimeException();
    }

    public static Enumeration emptyEnumeration() {
        throw new RuntimeException();
    }

    public static ListIterator emptyListIterator() {
        throw new RuntimeException();
    }
}
