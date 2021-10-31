package info.kgeorgiy.ja.strelnikov.walk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class HashingFileVisitor extends SimpleFileVisitor<Path> {
    private final Writer writer;

    public HashingFileVisitor(Writer writer) {
        this.writer = writer;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        printFileHash(calculateFileHash(file), file.toString());
        return super.visitFile(file, attrs);
    }

    private long calculateFileHash(Path file) {
        try (InputStream reader = new BufferedInputStream(Files.newInputStream(file))) {
            long hash = 0;
            long high;
            final byte[] buffer = new byte[256];
            final int bitsInLong = 64;
            final long mask = 0xFF00_0000_0000_0000L;
            int newBlockSize;
            while ((newBlockSize = reader.read(buffer)) >= 0) {
                for (int i = 0; i < newBlockSize; i++) {
                    hash = (hash << bitsInLong / 8) + (buffer[i] & 0xFF);
                    if ((high = hash & mask) != 0) {
                        hash = hash ^ (high >> (bitsInLong * 3 / 4));
                        hash &= ~high;
                    }
                }

            }
            return hash;
        } catch (IOException e) {
            System.err.println("Invalid file: " + e.getMessage());
        }
        return 0;
    }

    public void printFileHash(long fileHash, String path) {
        try {
            writer.write(String.format("%016x %s\n", fileHash, path));
        } catch (IOException e) {
            System.err.println("Invalid output file: " + e.getMessage());
        }
    }
}
