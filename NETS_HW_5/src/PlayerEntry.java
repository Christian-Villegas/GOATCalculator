import java.util.*;

public class PlayerEntry<String, V> implements Map.Entry<String, V>,
        Comparable<Map.Entry<String, V>> {
    public String key;
    public double value;

    //k = player name, v = points
    PlayerEntry(String k, V v) {
        key = k;
        value = (double) (Double) v;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return (V) (Object) value;
    }

    @Override
    public V setValue(V value) {
        V oldVal = (V) (Object) this.value;
        this.value = (double) (Double) value;
        return oldVal;
    }

    //sort instances by their values
    @Override
    public int compareTo(Map.Entry<String, V> o) {
        if ((Double) o.getValue() < value) {
            return 1;
        } else if (value < (Double) o.getValue()) {
            return -1;
        }
        return 0;
    }

    @Override
    public java.lang.String toString() {
        return key + " (Pts: " + value + ")";
    }

    //Since names are unique, if their keys are equal then they can be treated as equal
    @Override
    public boolean equals(Object o) {
        Map.Entry e = (Map.Entry) o;
        return key == e.getKey();
    }
}

