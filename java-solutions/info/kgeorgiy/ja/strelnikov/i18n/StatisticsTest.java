package info.kgeorgiy.ja.strelnikov.i18n;


import info.kgeorgiy.ja.strelnikov.i18n.output.Category;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    private final String prefix = "java-solutions/info/kgeorgiy/ja/strelnikov/i18n/texts/";

    private void test(final String name, final String textLocale, final String writeLocale) {
        System.out.println("===============================\nTest: " + name + "\n");
        TextStatistics.main(new String[]{textLocale, writeLocale, name + ".in", name + ".out"});

    }

    @Test
    public void testChinese() {
        test(prefix + "ChineseTest", "ch", "en-US");
        assertEquals(5, TextStatistics.getConsumer(Category.SENTENCE).getEntriesNumber());
        assertEquals("你好。 ", TextStatistics.getConsumer(Category.SENTENCE).getMinLengthValue());
        assertEquals("金额", TextStatistics.getConsumer(Category.WORD).getMaxValue());
        assertEquals("我叫伊利亚", TextStatistics.getConsumer(Category.WORD).getMaxLengthValue());
    }

    @Test
    public void testTolstoy() {
        test(prefix + "TolstoyTest", "ru", "ru-RU");
        assertEquals(13, TextStatistics.getConsumer(Category.SENTENCE).getEntriesNumber());
        assertEquals("То ему думалось, что враг его теперь нарочно пошлет эскадрон в отчаянную атаку," +
                        " чтобы наказать его, Ростова. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxValue());
    }
    @Test
    public void testFrench() {
        test(prefix + "FrenchTest", "ru", "en-US");
        assertEquals(13, TextStatistics.getConsumer(Category.SENTENCE).getEntriesNumber());
        assertEquals("Zherkov, après son expulsion du quartier général principal, n'est pas resté dans le régiment, affirmant qu'il n'était pas un imbécile à l'avant de tirer la sangle lorsqu'il était au quartier général, ne faisant rien, il recevrait plus de récompenses et savait comment régler en tant qu'infirmier du prince Bagration. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxValue());
    }
    @Test
    public void testEmpty() {
        test(prefix + "EmptyTest", "en", "ru-RU");
        for (final Category category : Category.values()) {
            assertEquals(0, TextStatistics.getConsumer(category).getEntriesNumber());
        }
    }

    @Test
    public void testArab() {
        test(prefix + "ArabTest", "ar", "ru-RU");
        assertEquals("انا عمري 20 سنة. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxLengthValue());
        assertEquals("اسمي ايليا. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMinValue());
    }
    @Test
    public void testKzOnKzWithKz() {
        test(prefix + "RuAndKzTest", "ru-KZ", "ru-RU");
        assertEquals("14 214,00 ₸ вот столько на Казахском стоит нормальная бутылочка. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxLengthValue());
        assertEquals("14 214,00 ₸ вот столько на Казахском стоит нормальная бутылочка. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMinValue());
        assertEquals(1, TextStatistics.getConsumer(Category.SUM).getEntriesNumber());
        assertEquals("14 214,00 ₸", TextStatistics.getConsumer(Category.SUM).getMaxValueAsString());
    }
    @Test
    public void testRuOnKzWithKz() {
        test(prefix + "RuAndKzTest", "ru-RU", "en-US");
        assertEquals("14 214,00 ₸ вот столько на Казахском стоит нормальная бутылочка. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxLengthValue());
        assertEquals("14 214,00 ₸ вот столько на Казахском стоит нормальная бутылочка. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMinValue());
        assertEquals(0, TextStatistics.getConsumer(Category.SUM).getEntriesNumber());
    }
    @Test
    public void testRuOnRuWithRu() {
        test(prefix + "KzOnRuTest", "ru", "en-US");
        assertEquals("Число 100, 200, 142, 15125.",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxLengthValue());
        assertEquals("Меня зовут Илья. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMinValue());
        assertEquals(1, TextStatistics.getConsumer(Category.SUM).getEntriesNumber());
    }
    @Test
    public void testKzOnRuWithRu() {
        test(prefix + "KzOnRuTest", "ru-KZ", "ru-RU");
        assertEquals("Число 100, 200, 142, 15125.",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxLengthValue());
        assertEquals("Меня зовут Илья. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMinValue());
        assertEquals(0, TextStatistics.getConsumer(Category.SUM).getEntriesNumber());
    }
    @Test
    public void testPortuguese() {
        test(prefix + "PortugueseTest", "pt", "en-US");
        assertEquals("Eu tenho 20 anos. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxLengthValue());
        assertEquals("Eu tenho 20 anos. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMinValue());
    }

    @Test
    public void testRussian() {
        test(prefix + "RussianTest", "ru", "ru-RU");
        assertEquals("Сумма денег: 111₽ 1 янв. 1970 г. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMaxLengthValue());
        assertEquals("Меня зовут Илья. ",
                TextStatistics.getConsumer(Category.SENTENCE).getMinValue());
    }
}
