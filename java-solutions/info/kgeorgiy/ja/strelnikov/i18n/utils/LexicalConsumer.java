package info.kgeorgiy.ja.strelnikov.i18n.utils;

import java.text.BreakIterator;
import java.text.Collator;
import java.util.Locale;

public class LexicalConsumer extends TextConsumer<String> {
    private final BreakIterator iterator;

    public LexicalConsumer(final Locale locale, final BreakIterator iterator) {
        super(Collator.getInstance(locale)::compare);
        this.iterator = iterator;
    }

    @Override
    public void parse(final String text) {
        iterator.setText(text);
        for (
                int begin = iterator.first(), end = iterator.next();
                end != BreakIterator.DONE;
                begin = end, end = iterator.next()
        ) {
            final String unit = text.substring(begin, end);
            if (unit.codePoints().anyMatch(Character::isLetter)) {
                consume(unit);
            }
        }
    }

    @Override
    public void consume(final String unit) {
        super.consume(unit);
        updateMinAndMaxLengths(unit, unit.length());
        sum += unit.length();
    }

    @Override
    public String getMinValueAsString() {
        return getMinValue();
    }

    @Override
    public String getMaxValueAsString() {
        return getMaxValue();
    }

    @Override
    public String getMinLengthValueAsString() {
        return getMinLengthValue();
    }

    @Override
    public String getMaxLengthValueAsString() {
        return getMaxLengthValue();
    }

}
