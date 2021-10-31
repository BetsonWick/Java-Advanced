package info.kgeorgiy.ja.strelnikov.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecursiveWalk {

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Invalid input");
            return;
        }
        final Path input;
        final Path output;
        try {
            input = Paths.get(args[0]);
            output = Paths.get(args[1]);
            try {
                if (output.getParent() != null) {
                    Files.createDirectories(output.getParent());
                }
                try (BufferedReader reader = Files.newBufferedReader(input)) {
                    try (BufferedWriter writer = Files.newBufferedWriter(output)) {
                        recursivelyWalk(reader, new HashingFileVisitor(writer));
                    } catch (IOException e) {
                        System.err.println("Invalid output file" + e.getMessage());
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("Input file not found: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Invalid input file: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Inaccessible output file: " + e.getMessage());
            }
        } catch (InvalidPathException e) {
            System.err.println("Invalid path: " + e.getInput());
        }

    }

    private static void recursivelyWalk(BufferedReader reader, HashingFileVisitor visitor) throws IOException {
        String newFileName;
        while ((newFileName = reader.readLine()) != null) {
            try {
                Files.walkFileTree(Paths.get(newFileName), visitor);
            } catch (InvalidPathException | IOException e) {
                System.err.println("File not available for a visitor: " + e.getMessage());
                visitor.printFileHash(0, newFileName);
            }
        }
    }
}