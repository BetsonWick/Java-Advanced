package info.kgeorgiy.ja.strelnikov.i18n.utils;

import java.text.Format;
import java.util.Date;

public class DateConsumer extends FormatConsumer<Date> {

    public DateConsumer(final Format format) {
        super(Date::compareTo, format);
    }

    @Override
    public void consume(final Date unit) {
        super.consume(unit);
        sum += unit.getTime();
    }

}
