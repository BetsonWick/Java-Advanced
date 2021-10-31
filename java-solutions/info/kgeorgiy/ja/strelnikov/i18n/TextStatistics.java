package info.kgeorgiy.ja.strelnikov.i18n;

import info.kgeorgiy.ja.strelnikov.i18n.output.Category;
import info.kgeorgiy.ja.strelnikov.i18n.utils.DateConsumer;
import info.kgeorgiy.ja.strelnikov.i18n.utils.LexicalConsumer;
import info.kgeorgiy.ja.strelnikov.i18n.utils.NumberConsumer;
import info.kgeorgiy.ja.strelnikov.i18n.utils.TextConsumer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.*;

public class TextStatistics {
    private static final int ARGUMENTS_NUMBER = 4;
    private static final String BUNDLES = "info.kgeorgiy.ja.strelnikov.i18n.output.ResourceBundle";
    private static Map<Category, TextConsumer<?>> consumers;
    private static void printUsage(final ResourceBundle bundle) {
        System.err.format(
                "%s: <%s> <%s> <%s> <%s>%n",
                bundle.getString("usage"),
                bundle.getString("input_loc"),
                bundle.getString("output_loc"),
                bundle.getString("input"),
                bundle.getString("output")
        );
    }

    private static boolean validateArguments(final String[] args) {
        if (args == null || args.length < ARGUMENTS_NUMBER) {
            return false;
        }
        for (int i = 0; i < ARGUMENTS_NUMBER; i++) {
            if (args[i] == null) {
                return false;
            }
        }
        return true;
    }

    public static void main(final String[] args) {
        ResourceBundle defaultBundle;
        try {
            defaultBundle = ResourceBundle.getBundle(BUNDLES, Locale.getDefault());
        } catch (final MissingResourceException ignored) {
            defaultBundle = ResourceBundle.getBundle(BUNDLES, Locale.US);
        }
        if (!validateArguments(args)) {
            printUsage(defaultBundle);
            return;
        }
        final Locale input;
        final Locale output;
        try {
            input = Locale.forLanguageTag(args[0]);
            output = Locale.forLanguageTag(args[1]);
        } catch (final NullPointerException e) {
            System.err.println(defaultBundle.getString("locale_err"));
            return;
        }
        final ResourceBundle outputBundle;
        try {
            outputBundle = ResourceBundle.getBundle(BUNDLES, output);
        } catch (final MissingResourceException e) {
            System.err.println(defaultBundle.getString("bundle_err"));
            return;
        }
        try (final BufferedReader reader = Files.newBufferedReader(Path.of(args[2]), StandardCharsets.UTF_8);
             final BufferedWriter writer = Files.newBufferedWriter(Path.of(args[3]), StandardCharsets.UTF_8)) {
            final StringBuilder builder = new StringBuilder();
            while (reader.ready()) {
                builder.append(reader.readLine());
            }
            calculateAndPrint(input, output, builder.toString(), outputBundle, writer);
        } catch (final IOException ioException) {
            System.err.println(defaultBundle.getString("io_err"));
        }
    }

    private static void out(final BufferedWriter writer, final String fromBundle,
                     final String value, final ResourceBundle bundle) throws IOException {
        writer.write("\t");
        outTitle(writer, fromBundle, value, bundle);
    }

    private static void outTitle(final BufferedWriter writer, final String fromBundle,
                          final String value, final ResourceBundle bundle) throws IOException {
        writer.write(bundle.getString(fromBundle) + ": " + value + "\n");
    }

    private static String hint(final String value) {
        return String.format(" (%s)", value);
    }

    private static String formatted(final Object value, final Format format) {
        return format.format(value);
    }

    private static void out(final Locale output,
                     final BufferedWriter writer,
                     final Map<Category, TextConsumer<?>> consumers,
                     final ResourceBundle bundle) throws IOException {
        outTitle(writer, "common", "", bundle);
        final Format numberOut = NumberFormat.getNumberInstance(output);
        for (final Category category : Category.values()) {
            out(writer, category.toString() + "_num",
                    formatted(consumers.get(category).getEntriesNumber(), numberOut), bundle);
        }
        for (final Category category : Category.values()) {
            final Format out = category == Category.DATE ?
                    DateFormat.getDateInstance(DateFormat.DEFAULT, output) :
                    numberOut;
            final TextConsumer<?> consumer = consumers.get(category);
            final int number = consumer.getEntriesNumber();
            if (number == 0) {
                continue;
            }
            outTitle(writer, category.toString(), "", bundle);
            out(writer, category.toString() + "_num",
                    number + hint(bundle.getString("unique") +
                            ": " + formatted(consumer.getUniqueEntriesNumber(), numberOut)), bundle);
            out(writer, category.toString() + "_min", consumer.getMinValueAsString(), bundle);
            out(writer, category.toString() + "_max", consumer.getMaxValueAsString(), bundle);
            out(writer, category.toString() + "_minL",
                    formatted(consumer.getMinLength(), out)
                            + hint(consumer.getMinLengthValueAsString()), bundle);
            out(writer, category.toString() + "_maxL",
                    formatted(consumer.getMaxLength(), out)
                            + hint(consumer.getMaxLengthValueAsString()), bundle);
            out(writer, category.toString() + "_avg",
                    formatted(consumer.getAverageValue(), out), bundle);
        }

    }

    private static void calculateAndPrint(final Locale input,
                                   final Locale output,
                                   final String text,
                                   final ResourceBundle bundle,
                                   final BufferedWriter writer) throws IOException {
         consumers =
                Map.of(Category.SENTENCE, new LexicalConsumer(input, BreakIterator.getSentenceInstance(input)),
                        Category.WORD, new LexicalConsumer(input, BreakIterator.getWordInstance(input)),
                        Category.NUMBER, new NumberConsumer(NumberFormat.getNumberInstance(input)),
                        Category.SUM, new NumberConsumer(NumberFormat.getCurrencyInstance(input)),
                        Category.DATE, new DateConsumer(DateFormat.getDateInstance(DateFormat.DEFAULT, input)));
        for (final Category category : Category.values()) {
            consumers.get(category).parse(text);
        }
        out(output, writer, consumers, bundle);
    }
    public static TextConsumer<?> getConsumer(final Category category) {
        return consumers.get(category);
    }

}
