package cacheinspector.swing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

public class NamedVector extends Vector {
    String name;

    public NamedVector(String name) {
        this.name = name;
    }

    public NamedVector(String name, CheckBoxNode[] elements) {
        this.name = name;
        Arrays.sort(elements, new Comparator<CheckBoxNode>() {
            @Override
            public int compare(CheckBoxNode o1, CheckBoxNode o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        for (int i = 0, n = elements.length; i < n; i++) {
            add(elements[i]);
        }
    }

    public String toString() {
        return "[" + name + "]";
    }
}
