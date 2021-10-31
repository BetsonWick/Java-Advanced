package info.kgeorgiy.ja.strelnikov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.String.format;

/**
 * Provides methods to implement a given class/interface to a given path
 * alongside with methods to compile {@code .java} code implementation file and
 * to build a {@code .jar} file containing the implementation.
 * <p>
 * Class/interface to implement and path where to create an implementation or {@code -jar} tag,
 * class/interface to create {@code .jar} file for its implementation and where to create should be indicated
 * as command line parameters.
 * </p>
 * <p>
 * Implementation of a given class/interface is produced by creating a corresponding {@code .java} file
 * containing compilable {@code .java} code.
 * </p>
 * <p>
 * Creating a {@code .jar} archive of implementation of a given class/interface
 * is produced by creating a corresponding {@code .java} file
 * containing compilable {@code .java} code, compiling it to a {@code .class} file and
 * then archiving it..
 * </p>
 * Implements interface {@link Impler}
 *
 * @author Strelnikov Ilya
 */
public class Implementor implements Impler, JarImpler {
    /**
     * A default constructor of {@link Implementor} class.
     * <p>Used to create an instance of {@link Implementor}.</p>
     */
    public Implementor() {

    }

    /**
     * Used for {@link #deleteDirectory(Path)} method to delete files
     * Contains overridden methods:
     * <ul>
     *     <li>{@link SimpleFileVisitor#visitFile(Object, BasicFileAttributes)}</li>
     *      <li>{@link SimpleFileVisitor#postVisitDirectory(Object, IOException)}</li>
     *  </ul>
     *  Which both delete file when called
     */
    private final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        /**
         * Deletes file which {@link Path} is the first argument.
         * @param file file to delete during visit which is the current file in file tree
         * @param attrs file attributes
         * @return {@link FileVisitResult#CONTINUE}
         * @throws IOException if deletion is not successful
         */
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Deletes file which {@link Path} is the first argument.
         * @param dir file to delete during visit which is the current file in file tree
         * @param exc  null if the iteration of the directory completes without an error;
         *            otherwise the I/O exception that caused the iteration of the directory to complete prematurely
         * @return {@link FileVisitResult#CONTINUE}
         * @throws IOException if deletion is not successful
         */
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /**
     * Validates command line arguments for {@link #main(String[])}.
     * Validation is successful if arguments are:
     * <ul>
     *     <li>not {@code null}</li>
     *     <li>not less than {@code 3}</li>
     *     <Li>first argument is {@code -jar}</Li>
     * </ul>
     *
     * @param input an array of command line arguments
     * @return {@code true/false} - arguments have been validated successfully or not
     */
    public static boolean validateInputJar(String[] input) {
        if (input == null || input.length < 3) {
            return false;
        }
        for (String value : input) {
            if (value == null) {
                return false;
            }
        }
        return input[0].equals("-jar");
    }

    /**
     * Writes an implementation of a validated class/interface to a {@code .java} file.
     * <p>
     * Creates directory and file for the given {@link Path},
     * there writes an implementation of a given class/interface to {@code .java} file
     * containing a normalized {@code .java} code.
     * </p>
     * <p>
     * Normalized means that every {@code codepoint} of {@link String} representation of
     * an implementation that is not already in the {@code Unicode} format is left
     * justified with at least four digits, padded with leading zeros and printed
     * as a {@code Unicode} symbol.
     * </p>
     *
     * @param token class/interface to implement
     * @param root  path to where implemented file is written
     * @throws ImplerException when there is an input/output exception or parameters are invalid.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        validateImpl(token, root);
        Path sourcePath = getSourcePath(token, root, ".java");
        createDirectory(sourcePath);
        try (BufferedWriter writer = Files.newBufferedWriter(sourcePath)) {
            writer.write(getImplementation(token).
                    codePoints().
                    mapToObj(cp -> cp > (1 << 7) ?
                            String.format("\\u%04x", cp) :
                            String.valueOf((char) cp)).
                    collect(Collectors.joining()));
        } catch (IOException e) {
            throw new ImplerException("Invalid output", e);
        }
    }

    /**
     * Creates a directory using {@link Files} class if a given path has parent.
     *
     * @param file path for which create a directory if needed
     * @throws ImplerException when impossible to create a directory
     */
    protected void createDirectory(Path file) throws ImplerException {
        Path parentFile = file.toAbsolutePath().getParent();
        if (parentFile != null) {
            try {
                Files.createDirectories(parentFile);
            } catch (IOException e) {
                throw new ImplerException("Invalid directory", e);
            }
        }
    }

    /**
     * Returns {@link Path} resolved for a certain path root related to class/interface with a given extension.
     * <p>
     * For an implemented class/interface tag {@code Impl} is added.
     * </p>
     *
     * @param token     class/interface provided
     * @param root      path root
     * @param extension file extension
     * @return {@link Path} resolved
     */
    protected Path getSourcePath(Class<?> token, Path root, String extension) {
        return root.resolve(
                Path.of(token.getPackageName().replace('.', File.separatorChar),
                        String.format("%sImpl%s",
                                token.getSimpleName(),
                                extension)));
    }

    /**
     * Validates parameters for {@link #implement(Class, Path)} method.
     * <p>
     * Class/interface and given path have to be not {@code null}
     * </p>
     * Class/interface could not be:
     * <ul>
     *     <li>A primitive type</li>
     *     <li>An array</li>
     *     <li>{@code Enum} class</li>
     * </ul>
     * Class/interface could not contain modifiers:
     * <ul>
     *     <li>{@code final}</li>
     *     <li>{@code private}</li>
     * </ul>
     *
     * @param token first parameter of {@link #implement(Class, Path)} method
     * @param root  second parameter of {@link #implement(Class, Path)} method
     * @throws ImplerException when validation is not successful
     */
    protected void validateImpl(Class<?> token, Path root) throws ImplerException {
        Objects.requireNonNull(token);
        Objects.requireNonNull(root);
        if (token.isPrimitive()
                || token.isArray()
                || token == Enum.class
                || Modifier.isFinal(token.getModifiers())
                || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Invalid token to implement");
        }
    }

    /**
     * Validates command line arguments for {@link #main(String[])}.
     * Validation is successful if arguments are:
     * <ul>
     *     <li>not {@code null}</li>
     *     <li>not less than {@code 2}</li>
     * </ul>
     *
     * @param input an array of command line arguments
     * @return {@code true/false} - arguments have been validated successfully or not
     */
    public static boolean validateInput(String[] input) {
        if (input == null || input.length < 2) {
            return false;
        }
        for (String value : input) {
            if (value == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Main method of {@link Implementor} class.
     * <p>
     * Gets parameters which are then validated by {@link #validateInput(String[])}
     * and {@link #validateInputJar(String[])} methods.
     * </p>
     * <p>
     * Calls {@link #implementJar(Class, Path)} to create a {@code -jar} archive of an
     * implementation of class/interface, or if not possible, writes a message -
     * if {@link #validateInputJar(String[])} returned {@code true}.
     * </p>
     * <p>
     * Otherwise it calls {@link #implement(Class, Path)}} to create an implementation of class/interface,
     * or if not possible, writes a message.
     * </p>
     * Successful {@code -jar} creation is impossible if one of these occur:
     * <ul>
     *     <li>{@link ClassNotFoundException}</li>
     *     <li>{@link InvalidPathException}</li>
     *     <li>{@link Implementor}</li>
     * </ul>
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (!validateInput(args) && !validateInputJar(args)) {
            System.err.println("Invalid input");
        } else {
            Implementor implementor = new Implementor();
            try {
                if (validateInputJar(args)) {
                    implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
                } else {
                    implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
                }
            } catch (ClassNotFoundException | InvalidPathException | ImplerException e) {
                System.err.println("Impossible to implement: " + e.getMessage());
            }
        }
    }

    /**
     * Implements class/interface as {@link String} of its {@code .java} file.
     * <p>Implementation consists of {@code .java} code containing:</p>
     * <ul>
     *     <li>Package if needed to write</li>
     *     <li>Head with modifiers,
     *     name with {@code Impl} tag and mark of extension/implementation of class/interface</li>
     *     <li>Constructors if it is a class implementation</li>
     *     <li>Methods extended/implemented</li>
     * </ul>
     *
     * @param token class/interface to implement
     * @return {@link String} value of implemented {@code .java} file
     * @throws ImplerException when method called inside has not delivered
     */
    private String getImplementation(Class<?> token) throws ImplerException {
        return format("%s%s%s%s%n}",
                getPackageString(token),
                getHeadString(token),
                token.isInterface() ? "" : getConstructorsString(token),
                getMethodsString(token)
        );
    }

    /**
     * Creates {@link String} representation of package of given class/interface.
     * <p>If there is no package name, it is not written.</p>
     * <p>Otherwise, there is a package name</p>
     *
     * @param token class/interface
     * @return {@link String} value of package
     */
    private String getPackageString(Class<?> token) {
        if (token.getPackageName().isEmpty()) {
            return skip();
        }
        return format("package %s;%s",
                token.getPackage().getName(),
                skip()
        );
    }

    /**
     * Creates {@link String} representation of head of class implementing class/interface.
     * <p>Head of that class consists of:</p>
     * <ul>
     *     <li>Modifiers</li>
     *     <li>Name with {@code Impl}</li>
     *     <li>Mark of extension/implementation of class/interface</li>
     * </ul>
     *
     * @param token class/interface
     * @return {@link String} value of head of a class/interface
     */
    private String getHeadString(Class<?> token) {
        return format("public class %sImpl %s %s {%n%s",
                token.getSimpleName(),
                token.isInterface() ? "implements" : "extends",
                token.getCanonicalName(),
                step(1)
        );
    }

    /**
     * Collects all {@link #getExecutableString(Executable)} representations for implementation of methods.
     * <p>Methods, which {@link String} implementations are collected, are taken if they are:</p>
     * <ul>
     *     <li>Situated in a given class/interface which we should implement</li>
     *     <li>
     *         Abstract and situated in super-classes/super-interfaces of a given class/interface
     *         which we should implement
     *     </li>
     * </ul>
     *
     * @param token class/interface of which methods are implemented
     * @return {@link String} containing all methods.
     */
    private String getMethodsString(Class<?> token) {
        HashSet<MethodExc> methodsSet = Arrays.stream(token.getMethods())
                .map(MethodExc::new)
                .collect(Collectors.toCollection(HashSet::new));
        while (token != null) {
            methodsSet.addAll(Arrays.stream(token.getDeclaredMethods())
                    .map(MethodExc::new)
                    .collect(Collectors.toCollection(HashSet::new)));
            token = token.getSuperclass();
        }
        return methodsSet.stream()
                .filter(m -> Modifier.isAbstract(m.getMethod().getModifiers()))
                .map(m -> getExecutableString(m.getMethod()))
                .collect(Collectors.joining(skip() + step(1)));
    }

    /**
     * Collects all {@link #getExecutableString(Executable)} representations for implementation of constructors.
     * <p>Constructors, which {@link String} implementations are collected, are taken if they are:</p>
     * <ul>
     *     <li>Declared in a given class which we should implement</li>
     *     <li>Not {@code private}</li>
     * </ul>
     *
     * @param token class/interface of which constructors are implemented
     * @return {@link String} containing all constructors.
     * @throws ImplerException when there are no declared constructors.
     */
    private String getConstructorsString(Class<?> token) throws ImplerException {
        Constructor<?>[] constructors = Arrays.stream(token.getDeclaredConstructors())
                .filter(constructor -> !Modifier.isPrivate(constructor.getModifiers()))
                .toArray(Constructor[]::new);
        if (constructors.length == 0) {
            throw new ImplerException("No constructors found");
        }
        return Arrays.stream(constructors)
                .map(this::getExecutableString)
                .collect(Collectors.joining(skip() + step(1)))
                + skip() + step(1);
    }

    /**
     * Creates {@link String} representation of {@link Executable}, which could be added to a compilable {@code .java}
     * of class/interface.
     *
     * @param executable {@link Constructor} or {@link Method}
     * @return {@link String} value of {@link Executable}.
     */
    private String getExecutableString(Executable executable) {
        final int modifiers = executable.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.NATIVE & ~Modifier.TRANSIENT;
        return format("%s%s%s%s%s %s",
                Modifier.toString(modifiers),
                modifiers == 0 ? "" : " ",
                getName(executable),
                embrace(collect(Arrays.stream(executable.getParameters())
                        .map(parameter -> format("%s %s",
                                parameter.getType().getCanonicalName(),
                                parameter.getName())))),
                getExceptionsString(executable),
                embraceCB(format("%n%s%s%n%s",
                        step(2),
                        getBody(executable),
                        step(1))
                ));
    }

    /**
     * Creates {@link String} representation of {@link Executable} name.
     * <p>
     * Creates a name for either {@link Constructor} or {@link Method} depending on what given
     * executable is an instance of.
     * </p>
     *
     * @param executable {@link Constructor} or {@link Method}
     * @return {@link String} value of name.
     */
    private String getName(Executable executable) {
        if (executable instanceof Constructor) {
            return executable.getDeclaringClass().getSimpleName() + "Impl";
        }
        Method toGetName = (Method) executable;
        return format("%s %s",
                toGetName.getReturnType().getCanonicalName(),
                toGetName.getName()
        );
    }

    /**
     * Creates {@link String} representation of {@link Executable} body.
     * <p>
     * Creates a body for either {@link Constructor} or {@link Method} depending on what given
     * executable is an instance of.
     * </p>
     * <p>
     * If executable is an instance of {@link Constructor}, it calls for a super-class constructor
     * with certain collected parameters
     * </p>
     * <p>If executable is an instance of {@link Method}, it returns a default value</p>
     * <p>Default values for a method are:</p>
     * <ul>
     *     <li>{@code false} if method return type is {@code boolean.class}</li>
     *     <li>{@code void} if method return type is {@code void.class}</li>
     *     <li>0 if method return type is primitive</li>
     *     <li>{@code null} otherwise</li>
     * </ul>
     *
     * @param executable {@link Constructor} or {@link Method}
     * @return {@link String} value of body.
     * @see #collect(Stream)
     */
    private String getBody(Executable executable) {
        if (executable instanceof Constructor) {
            return format("super%s;",
                    embrace(collect(Arrays.stream(executable.getParameters())
                            .map(Parameter::getName))));
        }
        Method method = (Method) executable;
        Class<?> token = method.getReturnType();
        String defaultValue = " null";
        if (token.equals(boolean.class)) {
            defaultValue = " false";
        } else if (token.equals(void.class)) {
            defaultValue = "";
        } else if (token.isPrimitive()) {
            defaultValue = " 0";
        }
        return format("return%s;", defaultValue);
    }

    /**
     * Creates {@link String} representation of exceptions thrown by {@link Executable}.
     * <p>If executable doesn't throw any exceptions, returns an empty string</p>
     * <p>Otherwise, it returns {@code throws} with collected into string exception types</p>
     *
     * @param executable {@link Constructor} or {@link Method}
     * @return {@link String} value of collected exceptions
     * @see #collect(Stream)
     */
    private String getExceptionsString(Executable executable) {
        String exceptions = collect(Arrays.stream(executable.getExceptionTypes())
                .map(Class::getCanonicalName));
        return format("%s%s",
                exceptions.isEmpty() ? "" : " throws ",
                exceptions);
    }

    /**
     * Returns {@link String} embraced in parentheses.
     *
     * @param body initial {@link String}
     * @return embraced {@link String}
     */
    private String embrace(String body) {
        return format("(%s)", body);
    }

    /**
     * Collects given {@link Stream} of {@link String} into a one {@link String} delimited with commas.
     *
     * @param stream {@link Stream} of {@link String}
     * @return delimited collected {@link String}
     */
    private String collect(Stream<String> stream) {
        return stream.collect(Collectors.joining(", "));
    }

    /**
     * Returns {@link String} embraced in braces.
     *
     * @param body initial {@link String}
     * @return embraced {@link String}
     */
    private String embraceCB(String body) {
        return format("{%s}", body);
    }

    /**
     * Creates a certain amount of indents.
     *
     * @param count indents count.
     * @return {@link String} containing the indents.
     */
    private String step(int count) {
        return "\t".repeat(count);
    }

    /**
     * Creates a {@code -jar} archive containing a compiled
     * implementation of a validated class/interface to a {@code .java} file.
     * <p>
     * Creates directory and file for the given {@link Path},
     * then creates a temporary directory for compiled by {@link #compile(Class, Path)} file
     * implementation of class/interface got from {@link #implement(Class, Path)}.
     * </p>
     * <p>
     * Creates a {@code -jar} archive containing a compiled
     * implementation of class/interface.
     * </p>
     * <p>
     * Finally deletes the temporary directory created previously.
     * </p>
     * <p>
     * Catches {@link CompilationException} when occurred an error during compilation or building
     * </p>
     *
     * @param token   class/interface to create {@code -jar} archive for
     * @param jarFile path to where {@code -jar} archive is delivered
     * @throws ImplerException when there is an input/output exception or parameters are invalid.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path tempDirectory = null;
        try {
            tempDirectory = Files.createTempDirectory(jarFile.toAbsolutePath().getParent(), "implementor-temp-");
        } catch (IOException e) {
            System.err.println("Temporary directory cannot be created");
        }
        try {
            implement(token, tempDirectory);
            compile(token, tempDirectory);
            buildJarFile(token, jarFile, tempDirectory);
        } catch (CompilationException e) {
            System.err.println("Error during compilation or building");
        } finally {
            deleteDirectory(tempDirectory);
        }
    }

    /**
     * Deletes a given directory recursively using {@link Files#walkFileTree(Path, FileVisitor)}.
     * {@link #DELETE_VISITOR} is used as a FileVisitor.
     * <p>
     * Catches {@link IOException} when deletion of one of files caused an error.
     * </p>
     *
     * @param directory {@link Path} of directory to delete
     */
    private void deleteDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, DELETE_VISITOR);
        } catch (IOException e) {
            System.err.println("Error occurred when deleting the directory");
        }
    }

    /**
     * Compiles {@code .java} file of implementation of class/interface.
     * <p>Resulting {@code .class} file is located in a given root directory</p>
     *
     * @param token class/interface for which compiled its implementation
     * @param root  location of resulting {@code .class} file
     * @throws CompilationException when a compilation error occurred
     */
    public void compile(final Class<?> token, final Path root) throws CompilationException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new CompilationException("No compiler is provided");
        }
        String classpath;
        try {
            CodeSource source = token.getProtectionDomain().getCodeSource();
            if (source == null) {
                throw new CompilationException("CodeSource of token domain is null");
            }
            URL url = source.getLocation();
            if (url == null) {
                throw new CompilationException("No URL was supplied");
            }
            classpath = Path.of(url.toURI()).toString();
            if (classpath.isEmpty()) {
                throw new CompilationException("Invalid classpath");
            }
        } catch (URISyntaxException e) {
            throw new CompilationException("Failed when converting URL to URI", e);
        }
        classpath = String.format("%s%s%s",
                root,
                File.pathSeparator,
                classpath);
        final String[] args = new String[]{
                getSourcePath(token, root, ".java").toString(),
                "-cp",
                classpath
        };
        final int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new CompilationException("Exit code is not zero. Exit code: " + exitCode);
        }
    }

    /**
     * Build a {@code .jar} file containing {@code .class} file of
     * {@code .java} file of implementation of class/interface
     *
     * @param token         a class/interface which compiled implementation should be archived
     * @param jarFile       directory for the resulted {@code .jar} file
     * @param tempDirectory temporary directory containing the compiled file of an implementation of class/interface
     * @throws CompilationException when compilation error occurred during creating a {@code .jar} file
     */
    private void buildJarFile(Class<?> token, Path jarFile, Path tempDirectory) throws CompilationException {
        try (ZipOutputStream writer = new ZipOutputStream(Files.newOutputStream(jarFile))) {
            String implName = String.format("%s/%sImpl.class",
                    token.getPackageName().replace(".", "/"),
                    token.getSimpleName());
            writer.putNextEntry(new ZipEntry(implName));
            Files.copy(getSourcePath(token, tempDirectory, ".class"), writer);
        } catch (IOException e) {
            throw new CompilationException("Error occurred during writing to JAR file", e);
        }
    }

    /**
     * An auxiliary exception to mark that an error during compilation occurred
     */
    public static class CompilationException extends Exception {
        /**
         * @param message message to show
         */
        public CompilationException(String message) {
            super(message);
        }

        /**
         * @param message message to show
         * @param cause   cause of the exception
         */
        public CompilationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Skips two lines.
     * Works correctly for every OS.
     *
     * @return {@link String} of two skips
     */
    private String skip() {
        return String.format("%n%n");
    }

    /**
     * An auxiliary class for {@link Method} class.
     * Contains overridden {@link #equals(Object)} and {@link #hashCode()} methods.
     */
    static class MethodExc {
        /**
         * Method for which this class is created.
         */
        private final Method METHOD;

        /**
         * @param method is assigned to {@link #METHOD}
         */
        public MethodExc(Method method) {
            this.METHOD = method;
        }

        /**
         * @return {{@link #METHOD}}
         */
        public Method getMethod() {
            return METHOD;
        }

        /**
         * Compares two methods basing on their names and parameter types.
         *
         * @param obj for which check the equality
         * @return {@code true/false} result of check
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MethodExc toCompare = (MethodExc) obj;
            return getMethod().getName().equals(toCompare.getMethod().getName())
                    && Arrays.equals(getMethod().getParameterTypes(), toCompare.getMethod().getParameterTypes());
        }

        /**
         * Evaluates hashcode for method basing on its name and parameter types.
         *
         * @return hashcode of {{@link #METHOD}}.
         */
        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(getMethod().getParameterTypes()), getMethod().getName());
        }
    }
}