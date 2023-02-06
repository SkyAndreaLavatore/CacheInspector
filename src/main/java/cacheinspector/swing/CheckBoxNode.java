package cacheinspector.swing;

public class CheckBoxNode {
    String text;
    String key;
    boolean selected;

    public CheckBoxNode(String text, String key, boolean selected) {
        this.text = text;
        this.key = key;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        return getClass().getName() + "[" + text + "/" + selected + "]";
    }
}
