package info.kgeorgiy.ja.strelnikov.i18n.utils;

import java.text.Format;
import java.text.ParsePosition;
import java.util.Comparator;

public abstract class FormatConsumer<T> extends TextConsumer<T> {
    protected final Format format;

    protected FormatConsumer(final Comparator<T> comparator, final Format format) {
        super(comparator);
        this.format = format;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parse(final String text) {
        int current = 0;
        ParsePosition position = new ParsePosition(current);
        while (current < text.length()) {
            final T parsedValue = (T) format.parseObject(text, position);
            if (position.getErrorIndex() == -1) {
                consume(parsedValue);
                final int length = position.getIndex() - current;
                updateMinAndMaxLengths(parsedValue, length);
                current = position.getIndex();
            } else {
                current = position.getErrorIndex();
            }
            current++;
            position = new ParsePosition(current);
        }
    }

    @Override
    public String getMinValueAsString() {
        return format.format(getMinValue());
    }

    @Override
    public String getMaxValueAsString() {
        return format.format(getMaxValue());
    }

    @Override
    public String getMinLengthValueAsString() {
        return format.format(getMinLengthValue());
    }

    @Override
    public String getMaxLengthValueAsString() {
        return format.format(getMaxLengthValue());
    }
}
