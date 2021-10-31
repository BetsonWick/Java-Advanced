package info.kgeorgiy.ja.strelnikov.i18n.utils;

import java.text.Format;

public class NumberConsumer extends FormatConsumer<Number> {
    public NumberConsumer(final Format format) {
        super((a, b) -> a.doubleValue() >=
                b.doubleValue() ? a.doubleValue() == b.doubleValue() ? 0 : 1 : -1, format);
    }

    @Override
    public void consume(final Number unit) {
        super.consume(unit);
        sum += unit.doubleValue();
    }
}
