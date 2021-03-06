## HW

### 1. Обход файлов

#### Условие

1. Разработайте класс Walk, осуществляющий подсчет хеш-сумм файлов.
    1. Формат запуска

        `java Walk <входной файл> <выходной файл>`

    2. Входной файл содержит список файлов, которые требуется обойти.

    3. Выходной файл должен содержать по одной строке для каждого файла. Формат строки:

        `<шестнадцатеричная хеш-сумма> <путь к файлу>`

    4. Для подсчета хеш-суммы используйте 64-битную версию алгоритма [PJW](https://en.wikipedia.org/wiki/PJW\_hash\_function).
    
    5. Если при чтении файла возникают ошибки, укажите в качестве его хеш-суммы `0000000000000000`.
    
    6. Кодировка входного и выходного файлов — UTF-8.
    
    7. Если родительская директория выходного файла не существует, то соответствующий путь надо создать.
    
    8. Размеры файлов могут превышать размер оперативной памяти.

    9. Пример
    
        Входной файл

        ```sh
        samples/1
        samples/12
        samples/123
        samples/1234
        samples/1
        samples/binary
        samples/no-such-file
        ```               

        Выходной файл

        ```sh
        0000000000000031 samples/1
        0000000000003132 samples/12
        0000000000313233 samples/123
        0000000031323334 samples/1234
        0000000000000031 samples/1
        005501015554abff samples/binary
        0000000000000000 samples/no-such-file
        ```

2. Сложный вариант:
    1. Разработайте класс `RecursiveWalk`, осуществляющий подсчет хеш-сумм файлов в директориях
    
    2. Входной файл содержит список файлов и директорий, которые требуется обойти. Обход директорий осуществляется рекурсивно.
    
    3. Пример
    
        Входной файл

        ```sh
        samples/binary
        samples
        samples/no-such-file
        ```

        Выходной файл

        ```sh
        005501015554abff samples/binary
        0000000000000031 samples/1    
        0000000000003132 samples/12
        0000000000313233 samples/123
        0000000031323334 samples/1234
        005501015554abff samples/binary
        0000000000000000 samples/no-such-file
        ```
                            
3. При выполнении задания следует обратить внимание на:
    * Дизайн и обработку исключений, диагностику ошибок.

    * Программа должна корректно завершаться даже в случае ошибки.
    
    * Корректная работа с вводом-выводом.
    
    * Отсутствие утечки ресурсов.
    
4. Требования к оформлению задания.

    * Проверяется исходный код задания.

    * Весь код должен находиться в пакете info.kgeorgiy.ja.фамилия.walk.

#### Тесты

Исходный код

 * простой вариант (`Walk`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/WalkTest.java)
 * сложный вариант (`RecursiveWalk`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/RecursiveWalkTest.java)
 * продвинутый вариант (`AdvancedWalk`) ✅:
    должный проходить тесты от простого и сложного вариантов

Тестовый модуль: [info.kgeorgiy.java.advanced.walk](artifacts/info.kgeorgiy.java.advanced.walk.jar)

Для того, чтобы протестировать программу:

 * Скачайте
    * тесты
        * [базовый модуль](artifacts/info.kgeorgiy.java.advanced.base.jar)
        * [тестовый модуль](artifacts/info.kgeorgiy.java.advanced.walk.jar) (свой для каждого ДЗ)
    * [библиотеки](lib)
 * Откомпилируйте решение домашнего задания
 * Протестируйте домашнее задание
    * Текущая директория должна:
       * содержать все скачанные `.jar` файлы;
       * содержать скомпилированное решение;
       * __не__ содержать скомпилированные самостоятельно тесты.
    * Запустите тесты:
        `java -cp . -p . -m <тестовый модуль> <вариант> <полное имя класса>`
    * Пример для простого варианта ДЗ-1:
        `java -cp . -p . -m info.kgeorgiy.java.advanced.walk Walk <полное имя класса>`

#### [Реализация](modules/info.kgeorgiy.ja.slastin.walk/info/kgeorgiy/ja/slastin/walk)


### 2. Множество на массиве
 
#### Условие

1. Разработайте класс `ArraySet`, реализующие неизменяемое упорядоченное множество.

    * Класс `ArraySet` должен реализовывать интерфейс `SortedSet` (простой вариант) или `NavigableSet` (сложный вариант).
    
    * Все операции над множествами должны производиться с максимально возможной асимптотической эффективностью.
    
2. При выполнении задания следует обратить внимание на:

    * Применение стандартных коллекций.
    
    * Избавление от повторяющегося кода.

#### Тесты

Исходный код

 * простой вариант (`SortedSet`) ✅: 
    [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/SortedSetTest.java)
 * сложный вариант (`NavigableSet`) ✅: 
    [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/NavigableSetTest.java)
 * продвинутый вариант (`AdvancedSet`) ✅: 
    [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/AdvancedSetTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.arrayset](artifacts/info.kgeorgiy.java.advanced.arrayset.jar)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.arrayset/info.kgeorgiy.ja.slastin.arrayset)


### 3. Студенты

#### Условие

1. Разработайте класс `StudentDB`, осуществляющий поиск по базе данных студентов.

    * Класс `StudentDB` должен реализовывать интерфейс `StudentQuery` (простой вариант) или `GroupQuery` (сложный вариант).

    * Каждый метод должен состоять из ровно одного оператора. При этом длинные операторы надо разбивать на несколько строк.

2. При выполнении задания следует обратить внимание на:

    * применение лямбда-выражений и потоков;

    * избавление от повторяющегося кода.

#### Тесты

Исходный код

 * простой вариант (`StudentQuery`) ✅:
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQueryTest.java)
 * сложный вариант (`GroupQuery`) ✅:
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/GroupQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/GroupQueryTest.java)
 * продвинутый вариант (`AdvancedQuery`) ✅:
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedQueryTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.student](artifacts/info.kgeorgiy.java.advanced.student.jar)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.student/info/kgeorgiy/ja/slastin/student)


### 4. Implementor

#### Условие

1. Реализуйте класс `Implementor`, который будет генерировать реализации классов и интерфейсов.

    * Аргумент командной строки: полное имя класса/интерфейса, для которого требуется сгенерировать реализацию.
    
    * В результате работы должен быть сгенерирован java-код класса с суффиксом `Impl`, расширяющий (реализующий) указанный класс (интерфейс).
    
    * Сгенерированный класс должен компилироваться без ошибок.
    
    * Сгенерированный класс не должен быть абстрактным.
    
    * Методы сгенерированного класса должны игнорировать свои аргументы и возвращать значения по умолчанию.
    
2. В задании выделяются три варианта:

    * _Простой_ ✅ — `Implementor` должен уметь реализовывать только интерфейсы (но не классы). Поддержка generics не требуется.

    * _Сложный_ ✅ — `Implementor` должен уметь реализовывать и классы, и интерфейсы. Поддержка generics не требуется.
    
    * _Бонусный_ — `Implementor` должен уметь реализовывать generic-классы и интерфейсы. Сгенерированный код должен иметь корректные параметры типов и не порождать `UncheckedWarning`.

#### Тесты

Класс `Implementor` должен реализовывать интерфейс
[Impler](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java).

Исходный код

 * простой вариант (`interface`) ✅: 
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceImplementorTest.java)
 * сложный вариант (`class`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassImplementorTest.java)
 * продвинутый вариант (`advanced`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedImplementorTest.java)
 * предварительные тесты бонусного варианта (`covariant`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedImplementorTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.implementor](artifacts/info.kgeorgiy.java.advanced.implementor.jar)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.implementor/info/kgeorgiy/ja/slastin)


### 5. Jar Implementor

#### Условие

1. Создайте `.jar`-файл, содержащий скомпилированный `Implementor` и сопутствующие классы.

    * Созданный `.jar`-файл должен запускаться командой `java -jar`.
    
    * Запускаемый `.jar`-файл должен принимать те же аргументы командной строки, что и класс `Implementor`.
    
2. Модифицируйте `Implemetor` так, чтобы при запуске с аргументами `-jar имя-класса файл.jar` он генерировал `.jar`-файл с реализацией соответствующего класса (интерфейса).

3. Для проверки, кроме исходного кода так же должны быть представлены:

    * скрипт для создания запускаемого `.jar`-файла, в том числе, исходный код манифеста;
    
    * запускаемый `.jar`-файл.
    
4. Данное домашнее задание сдается только вместе с предыдущим. Предыдущее домашнее задание отдельно сдать будет нельзя.

5. **Сложный вариант**. Решение должно быть модуляризовано.

#### Тесты

Класс `Implementor` должен дополнительно реализовывать интерфейс
[JarImpler](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/JarImpler.java).

Исходный код

 * простой вариант (`jar-interface`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceJarImplementorTest.java)
 * сложный вариант (`jar-class`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassJarImplementorTest.java)
 * продвинутый вариант (`jar-advanced`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedJarImplementorTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.implementor](artifacts/info.kgeorgiy.java.advanced.implementor.jar)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.implementor/info/kgeorgiy/ja/slastin)

### 6. Javadoc

#### Условие

1. Документируйте класс `Implementor` и сопутствующие классы с применением Javadoc.

    * Должны быть документированы все классы и все члены классов, в том числе `private`.
    
    * Документация должна генерироваться без предупреждений.
    
    * Сгенерированная документация должна содержать корректные ссылки на классы стандартной библиотеки.
    
2. Для проверки, кроме исходного кода так же должны быть представлены:

    * скрипт для генерации документации;
    
    * сгенерированная документация.
    
3. Данное домашнее задание сдается только вместе с предыдущим. Предыдущее домашнее задание отдельно сдать будет нельзя.

#### Тесты

Класс `Implementor` должен дополнительно реализовывать интерфейс
[JarImpler](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/JarImpler.java).

Исходный код

 * простой вариант (`jar-interface`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceJarImplementorTest.java)
 * сложный вариант (`jar-class`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassJarImplementorTest.java)
 * продвинутый вариант (`jar-advanced`) ✅:
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedJarImplementorTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.implementor](artifacts/info.kgeorgiy.java.advanced.implementor.jar)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.implementor/info/kgeorgiy/ja/slastin)


### 7. Итеративный параллелизм

#### Условие

1. Реализуйте класс `IterativeParallelism`, который будет обрабатывать списки в несколько потоков.

2. В простом варианте должны быть реализованы следующие методы:

    * `minimum(threads, list, comparator)` — первый минимум;

    * `maximum(threads, list, comparator)` — первый максимум;

    * `all(threads, list, predicate)` — проверка, что все элементы списка удовлетворяют предикату;

    * `any(threads, list, predicate)` — проверка, что существует элемент списка, удовлетворяющий предикату.

3. В сложном варианте должны быть дополнительно реализованы следующие методы:

    * `filter(threads, list, predicate)` — вернуть список, содержащий элементы удовлетворяющие предикату;
    
    * `map(threads, list, function)` — вернуть список, содержащий результаты применения функции;
    
    * `join(threads, list)` — конкатенация строковых представлений элементов списка.
    
4. Во все функции передается параметр `threads` — сколько потоков надо использовать при вычислении. Вы можете рассчитывать, что число потоков не велико.

5. Не следует рассчитывать на то, что переданные компараторы, предикаты и функции работают быстро. 

6. При выполнении задания **нельзя** использовать _Concurrency Utilities_.

7. Рекомендуется подумать, какое отношение к заданию имеют [моноиды](https://en.wikipedia.org/wiki/Monoid).

#### Тесты

Тестирование

 * простой вариант ✅:
   ```info.kgeorgiy.java.advanced.concurrent scalar <полное имя класса>```

   Класс должен реализовывать интерфейс
   [ScalarIP](modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ScalarIP.java).

 * сложный вариант: ✅
   ```info.kgeorgiy.java.advanced.concurrent list <полное имя класса>```

   Класс должен реализовывать интерфейс
   [ListIP](modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ListIP.java).

 * продвинутый вариант ✅:
   ```info.kgeorgiy.java.advanced.concurrent advanced <полное имя класса>```

   Класс должен реализовывать интерфейс
   [AdvancedIP](modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/AdvancedIP.java).

Исходный код тестов:

* [простой вариант](modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ScalarIPTest.java)
* [сложный вариант](modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ListIPTest.java)
* [продвинутый вариант](modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/AdvancedIPTest.java)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.concurrent/info/kgeorgiy/ja/slastin)


### 8. Параллельный запуск

#### Условие

1. Напишите класс `ParallelMapperImpl`, реализующий интерфейс `ParallelMapper`.

```java
public interface ParallelMapper extends AutoCloseable {
        List map(
            Function f,
            List args
        ) throws InterruptedException;

        @Override
        void close() throws InterruptedException;
    } 
```

* Метод `run` должен параллельно вычислять функцию `f` на каждом из указанных аргументов (`args`).

* Метод `close` должен останавливать все рабочие потоки.

* Конструктор `ParallelMapperImpl(int threads)` создает `threads` рабочих потоков, которые могут быть использованы для распараллеливания.

* К одному `ParallelMapperImpl` могут одновременно обращаться несколько клиентов.

* Задания на исполнение должны накапливаться в очереди и обрабатываться в порядке поступления.

* В реализации не должно быть активных ожиданий.

2. Доработайте класс `IterativeParallelism` так, чтобы он мог использовать `ParallelMapper`.

    * Добавьте конструктор `IterativeParallelism(ParallelMapper)`
    
    * Методы класса должны делить работу на `threads` фрагментов и исполнять их при помощи `ParallelMapper`.
    
    * При наличии `ParallelMapper` сам `IterativeParallelism` новые потоки создавать не должен.
    
    * Должна быть возможность одновременного запуска и работы нескольких клиентов, использующих один `ParallelMapper`.

#### Тесты

Тестирование

 * простой вариант ✅:
    ```info.kgeorgiy.java.advanced.mapper scalar <полное имя класса>```
 * сложный вариант ✅:
    ```info.kgeorgiy.java.advanced.mapper list <полное имя класса>```
 * продвинутый вариант ✅:
    ```info.kgeorgiy.java.advanced.mapper advanced <полное имя класса>```

Исходный код тестов:

* [простой вариант](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ScalarMapperTest.java)
* [сложный вариант](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ListMapperTest.java)
* [продвинутый вариант](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/AdvancedMapperTest.java)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.concurrent/info/kgeorgiy/ja/slastin)


### 9. Web Crawler

#### Условие

1. Напишите потокобезопасный класс `WebCrawler`, который будет рекурсивно обходить сайты.

    1. Класс `WebCrawler` должен иметь конструктор

    `public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost)`
                            
    * `downloader` позволяет скачивать страницы и извлекать из них ссылки;
    
    * `downloaders` — максимальное число одновременно загружаемых страниц;
    
    * `extractors` — максимальное число страниц, из которых одновременно извлекаются ссылки;
    
    * `perHost` — максимальное число страниц, одновременно загружаемых c одного хоста. Для определения хоста следует использовать метод `getHost` класса `URLUtils` из тестов.

    2. Класс `WebCrawler` должен реализовывать интерфейс `Crawler`

    ```java
    public interface Crawler extends AutoCloseable {
            Result download(String url, int depth);

            void close();
        }
    ```                     

    * Метод `download` должен рекурсивно обходить страницы, начиная с указанного URL на указанную глубину и возвращать список загруженных страниц и файлов. Например, если глубина равна 1, то должна быть загружена только указанная страница. Если глубина равна 2, то указанная страница и те страницы и файлы, на которые она ссылается и так далее. Этот метод может вызываться параллельно в нескольких потоках.
    
    * Загрузка и обработка страниц (извлечение ссылок) должна выполняться максимально параллельно, с учетом ограничений на число одновременно загружаемых страниц (в том числе с одного хоста) и страниц, с которых загружаются ссылки.
    
    * Для распараллеливания разрешается создать до `downloaders + extractors` вспомогательных потоков.
    
    * Загружать и/или извлекать ссылки из одной и той же страницы в рамках одного обхода (`download`) запрещается.
    
    * Метод `close` должен завершать все вспомогательные потоки.
    
    3. Для загрузки страниц должен применяться `Downloader`, передаваемый первым аргументом конструктора.

    ```java
    public interface Downloader {
            public Document download(final String url) throws IOException;
        }
    ```                           

    * Метод `download` загружает документ по его адресу ([URL](http://tools.ietf.org/html/rfc3986)).
    
    * Документ позволяет получить ссылки по загруженной странице:

    ```java
    public interface Document {
                List extractLinks() throws IOException;
            } 
    ```

    Ссылки, возвращаемые документом, являются абсолютными и имеют схему `http` или `https`. 
    
    4. Должен быть реализован метод `main`, позволяющий запустить обход из командной строки
    
    * Командная строка

        `WebCrawler url [depth [downloads [extractors[perHost]]]]`
                                        

    * Для загрузки страниц требуется использовать реализацию `CachingDownloader` из тестов.
    
2. Версии задания

    1. _Простая_ ✅ — не требуется учитывать ограничения на число одновременных закачек с одного хоста (`perHost >= downloaders`).
    
    2. _Полная_ ✅ — требуется учитывать все ограничения.
    
    3. _Бонусная_ — сделать параллельный обод в ширину.

#### Тесты

Тестирование

 * простой вариант:
    ```info.kgeorgiy.java.advanced.crawler easy <полное имя класса>```
 * сложный вариант:
    ```info.kgeorgiy.java.advanced.crawler hard <полное имя класса>```
 * продвинутый вариант: [интерфейс](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/AdvancedCrawler.java)
    ```info.kgeorgiy.java.advanced.crawler advanced <полное имя класса>```

Исходный код тестов:

* [интерфейсы и вспомогательные классы](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/)
* [простой вариант](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/EasyCrawlerTest.java)
* [сложный вариант](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/HardCrawlerTest.java)
* [продвинутый вариант](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/AdvancedCrawlerTest.java)

#### [Реализация](modules/info.kgeorgiy.ja.slastin.crawler/info.kgeorgiy.ja.slastin.crawler)


### 10. HelloUDP

#### Условие

1. Реализуйте клиент и сервер, взаимодействующие по UDP.

2. Класс `HelloUDPClient` должен отправлять запросы на сервер, принимать результаты и выводить их на консоль.
    * Аргументы командной строки:
        1. имя или ip-адрес компьютера, на котором запущен сервер;
        
        2. номер порта, на который отсылать запросы;
        
        3. префикс запросов (строка);
        
        4. число параллельных потоков запросов;
        
        5. число запросов в каждом потоке.
        
    * Запросы должны одновременно отсылаться в указанном числе потоков. Каждый поток должен ожидать обработки своего запроса и выводить сам запрос и результат его обработки на консоль. Если запрос не был обработан, требуется послать его заново.
    
    * Запросы должны формироваться по схеме `<префикс запросов><номер потока>_<номер запроса в потоке>`.
    
3. Класс `HelloUDPServer` должен принимать задания, отсылаемые классом `HelloUDPClient` и отвечать на них.

    * Аргументы командной строки:
    
        1. номер порта, по которому будут приниматься запросы;
        
        2. число рабочих потоков, которые будут обрабатывать запросы.
        
    * Ответом на запрос должно быть `Hello, <текст запроса>`.
    
    * Если сервер не успевает обрабатывать запросы, прием запросов может быть временно приостановлен.

#### Тесты

Интерфейсы

 * `HelloUDPClient` должен реализовывать интерфейс
    [HelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClient.java)
 * `HelloUDPServer` должен реализовывать интерфейс
    [HelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServer.java)

Тестирование

 * простой вариант ✅:
    * клиент:
        ```info.kgeorgiy.java.advanced.hello client <полное имя класса>```
    * сервер:
        ```info.kgeorgiy.java.advanced.hello server <полное имя класса>```
 * сложный вариант ✅:
    * на противоположной стороне находится система, дающая ответы на различных языках
    * клиент:
        ```info.kgeorgiy.java.advanced.hello client-i18n <полное имя класса>```
    * сервер:
        ```info.kgeorgiy.java.advanced.hello server-i18n <полное имя класса>```
 * продвинутый вариант ✅:
    * на противоположной стороне находится старая система,
      не полностью соответствующая последней версии спецификации
    * клиент:
        ```info.kgeorgiy.java.advanced.hello client-evil <полное имя класса>```
    * сервер:
        ```info.kgeorgiy.java.advanced.hello server-evil <полное имя класса>```

#### [Реализация](modules/info.kgeorgiy.ja.slastin.hello/info.kgeorgiy.ja.slastin.hello)

### 11. Физические лица

#### Условие

1. Добавьте к банковскому приложению возможность работы с физическими лицами.

    1. У физического лица (`Person`) можно запросить имя, фамилию и номер паспорта.
    
    2. Локальные физические лица (`LocalPerson`) должны передаваться при помощи механизма сериализации.
    
    3. Удалённые физические лица (`RemotePerson`) должны передаваться при помощи удалённых объектов.
    
    4. Должна быть возможность поиска физического лица по номеру паспорта, с выбором типа возвращаемого лица.
    
    5. Должна быть возможность создания записи о физическом лице по его данным.
    
    6. У физического лица может быть несколько счетов, к которым должен предоставляться доступ.
    
    7. Счету физического лица с идентификатором `subId` должен соответствовать банковский счет с `id` вида `passport:subId`.
    
    8. Изменения, производимые со счетом в банке (создание и изменение баланса), должны быть видны всем соответствующим `RemotePerson`, и только тем `LocalPerson`, которые были созданы после этого изменения.
    
    9. Изменения в счетах, производимые через `RemotePerson`, должны сразу применяться глобально, а производимые через `LocalPerson` – только локально для этого конкретного `LocalPerson`.
    
2. Реализуйте приложение, демонстрирующее работу с физическим лицами.

    1. Аргументы командной строки: имя, фамилия, номер паспорта физического лица, номер счета, изменение суммы счета. 
    
    2. Если информация об указанном физическом лице отсутствует, то оно должно быть добавлено. В противном случае – должны быть проверены его данные.
    
    3. Если у физического лица отсутствует счет с указанным номером, то он создается с нулевым балансом.
    
    4. После обновления суммы счета новый баланс должен выводиться на консоль. 

3. Напишите тесты, проверяющее вышеуказанное поведение как банка, так и приложения.

    * Для реализации тестов рекомендуется использовать [JUnit](https://junit.org/junit5/) ([Tutorial](https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-our-first-test-class/)). Множество примеров использования можно найти в тестах.
    
    * Если вы знакомы с другим тестовым фреймворком (например, [TestNG](https://testng.org/)), то можете использовать его.
    
    * Jar-файлы используемых библиотек надо класть в каталог `lib` вашего репозитория.
    
    * Использовать самописные фреймворки и тесты запускаемые через `main` нельзя.
    
4. **Сложный вариант**

    1. Тесты не должны рассчитывать на наличие запущенного RMI Registry. 
    
    2. Создайте класс `BankTests`, запускающий тесты.
    
    3. Создайте скрипт, запускающий `BankTests` и возвращающий код (статус) `0` в случае успеха и `1` в случае неудачи.
    
    4. Создайте скрипт, запускающий тесты с использованием стандартного подхода для вашего тестового фреймворка. Код возврата должен быть как в предыдущем пункте.
    

### 12. HelloNonblockingUDP

#### Условие

1. Реализуйте клиент и сервер, взаимодействующие по UDP, используя только неблокирующий ввод-вывод.

2. Класс `HelloUDPNonblockingClient` должен иметь функциональность аналогичную `HelloUDPClient`, но без создания новых потоков.

3. Класс `HelloUDPNonblockingServer` должен иметь функциональность аналогичную `HelloUDPServer`, но все операции с сокетом должны производиться в одном потоке.

4. В реализации не должно быть активных ожиданий, в том числе через `Selector`.

5. Обратите внимание на выделение общего кода старой и новой реализации.

6. _Бонусный вариант_. Клиент и сервер могут перед началом работы выделить O(число потоков) памяти. Выделять дополнительную память во время работы запрещено.

#### Тесты

Интерфейсы

 * `HelloUDPNonblockingClient` должен реализовывать интерфейс
    [HelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClient.java)
 * `HelloUDPNonblockingServer` должен реализовывать интерфейс
    [HelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServer.java)

Тестирование

 * простой вариант ✅:
    * клиент:
        ```info.kgeorgiy.java.advanced.hello client <полное имя класса>```
    * сервер:
        ```info.kgeorgiy.java.advanced.hello server <полное имя класса>```

#### [Реализация](modules/info.kgeorgiy.ja.slastin.hello/info.kgeorgiy.ja.slastin.hello)


### 13. Статистика текста

#### Условие

1. Создайте приложение `TextStatistics`, анализирующее тексты на различных языках.

    1. Аргументы командной строки:
        * локаль текста,
        * локаль вывода,
        * файл с текстом,
        * файл отчета.
    
    2. Поддерживаемые локали текста: все локали, имеющиеся в системе.
    
    3. Поддерживаемые локали вывода: русская и английская.
    
    4. Файлы имеют кодировку UTF-8.
    
    5. Подсчет статистики должен вестись по следующим категориям:
        * предложения,
        * слова,
        * числа,
        * деньги,
        * даты.
        
    6. Для каждой категории должна собираться следующая статистика:
        * число вхождений,
        * число различных значений,
        * минимальное значение,
        * максимальное значение,
        * минимальная длина,
        * максимальная длина,
        * среднее значение/длина.
        
    7. Пример отчета:

    ```sh
    Анализируемый файл "input.txt"
    Сводная статистика
            Число предложений: 43.
            Число слов: 275.
            Число чисел: 40.
            Число сумм: 3.
            Число дат: 3.
        Статистика по предложениям
            Число предложений: 43 (43 различных).
            Минимальное предложение: "Аргументы командной строки: локаль текста, локаль вывода, файл с текстом, файл отчета.".
            Максимальное предложение: "Число чисел: 40.".
            Минимальная длина предложения: 13 ("Число дат: 3.").
            Максимальная длина предложения: 211 ("GK: если сюда поставить реальное предложение, то процесс не сойдётся").
            Средняя длина предложения: 55,465.
        Статистика по словам
            Число слов: 275 (157 различных).
            Минимальное слово: "GK".
            Максимальное слово: "языках".
            Минимальная длина слова: 1 ("с").
            Максимальная длина слова: 14 ("TextStatistics").
            Средняя длина слова: 6,72.
        Статистика по числам
            Число чисел: 40 (24 различных).
            Минимальное число: -12345,0.
            Максимальное число: 12345,67.
            Среднее число: 207,676.
        Статистика по суммам денег
            Число сумм: 3 (3 различных).
            Минимальная сумма: 100,00 ₽.
            Максимальная сумма: 345,67 ₽.
            Средняя сумма: 222,83 ₽.
        Статистика по датам
            Число дат: 3 (3 различных).
            Минимальная дата: 22 мая 2021 г..
            Максимальная дата: 8 июн. 2021 г..
            Средняя дата: 30 мая 2021 г..
        ```

2. Вы можете рассчитывать на то, что весь текст помещается в память.

3. При выполнении задания следует обратить внимание на:
    
    1. Декомпозицию сообщений для локализации
    
    2. Согласование сообщений по роду и числу
    
4. Напишите тесты, проверяющее вышеуказанное поведение приложения.

    * Для реализации тестов рекомендуется использовать [JUnit](https://junit.org/junit5/) ([Tutorial](https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-our-first-test-class/)). Множество примеров использования можно найти в тестах.
    
    * Если вы знакомы с другим тестовым фреймворком (например, [TestNG](https://testng.org/)), то можете использовать его.
    
    * Использовать самописные фреймворки и тесты запускаемые через `main` нельзя.
