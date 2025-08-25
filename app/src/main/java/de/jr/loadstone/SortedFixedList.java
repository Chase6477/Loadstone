package de.jr.loadstone;


import java.util.ArrayList;

public class SortedFixedList<V extends Comparable<V>> {

    public final int size;

    private final ArrayList<V> unsortedList;
    private final ArrayList<V> sortedList;


    public SortedFixedList(int size, V defaultValue) {
        this.size = size;

        sortedList = new ArrayList<>(size);
        unsortedList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            sortedList.add(defaultValue);
            unsortedList.add(defaultValue);
        }
    }

    public void add(V value) {
        V removableObject = unsortedList.get(0);

        int newLocation = 0;

        for (int i = 0; i < size - 1; i++) {
            if (value.compareTo(sortedList.get(i)) <= 0) break;
            newLocation++;
        }
        sortedList.remove(removableObject);
        sortedList.add(newLocation, value);
        unsortedList.remove(removableObject);
        unsortedList.add(value);
    }

    public V get(int index) {
        return sortedList.get(index);
    }

    public V getMedian() {
        return sortedList.get(size / 2);
    }
}
