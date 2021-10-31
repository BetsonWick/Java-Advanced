package info.kgeorgiy.ja.strelnikov.i18n.utils;

import java.util.Comparator;
import java.util.TreeSet;

public abstract class TextConsumer<T> {
    protected final TreeSet<T> unitsSet;
    protected int entriesNumber;
    protected T minValue;
    protected T maxValue;
    protected T minLengthValue;
    protected T maxLengthValue;
    protected int minLength;
    protected int maxLength;
    protected Double sum;

    protected TextConsumer(final Comparator<T> comparator) {
        entriesNumber = 0;
        sum = 0D;
        unitsSet = new TreeSet<>(comparator);
        minValue = null;
        maxValue = null;
        maxLengthValue = null;
        maxLengthValue = null;
    }

    public void consume(final T unit) {
        entriesNumber++;
        unitsSet.add(unit);
    }

    protected void updateMinAndMaxLengths(final T newValue, final int newSize) {
        if (minLengthValue == null || newSize < minLength) {
            minLengthValue = newValue;
            minLength = newSize;
        }
        if (maxLengthValue == null || newSize > maxLength) {
            maxLengthValue = newValue;
            maxLength = newSize;
        }
    }

    public abstract void parse(final String text);

    public int getEntriesNumber() {
        return entriesNumber;
    }

    public T getMinValue() {
        return unitsSet.first();
    }

    public T getMaxValue() {
        return unitsSet.last();
    }

    public int getUniqueEntriesNumber() {
        return unitsSet.size();
    }

    public double getAverageValue() {
        return sum / entriesNumber;
    }

    public abstract String getMinValueAsString();

    public abstract String getMaxValueAsString();

    public abstract String getMinLengthValueAsString();

    public abstract String getMaxLengthValueAsString();

    public T getMinLengthValue() {
        return minLengthValue;
    }

    public T getMaxLengthValue() {
        return maxLengthValue;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
