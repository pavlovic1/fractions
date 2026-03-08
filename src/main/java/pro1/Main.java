package pro1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private static final Path INPUT_DIR = Paths.get("input");
    private static final Path OUTPUT_DIR = Paths.get("output");

    public static void main(String[] args) {
        try {
            prepareDirectories();
            processFiles();
            System.out.println("Processing finished successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void prepareDirectories() throws IOException {
        if (Files.notExists(INPUT_DIR)) Files.createDirectories(INPUT_DIR);
        if (Files.notExists(OUTPUT_DIR)) Files.createDirectories(OUTPUT_DIR);
    }

    private static void processFiles() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(INPUT_DIR)) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".zip")) {
                    processZip(entry);
                } else if (fileName.endsWith(".csv")) {
                    processCsvFile(entry);
                }
            }
        }
    }

    private static void processZip(Path zipPath) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".csv")) {
                    String fileName = new File(entry.getName()).getName();
                    processStream(zis, fileName);
                }
                zis.closeEntry();
            }
        }
    }

    private static void processCsvFile(Path csvPath) throws IOException {
        try (InputStream is = new FileInputStream(csvPath.toFile())) {
            processStream(is, csvPath.getFileName().toString());
        }
    }

    private static void processStream(InputStream is, String outputFileName) throws IOException {
        Path outputPath = OUTPUT_DIR.resolve(outputFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder outputContent = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) continue;

            String[] columns = line.split("[,;:|=\\\\]", 2);

            if (columns.length == 2) {
                String name = columns[0].trim();
                String rawData = columns[1].trim();

                try {
                    Fraction fraction = Fraction.parse(rawData);
                    outputContent.append(name).append(",").append(fraction.toString()).append("\n");
                } catch (Exception e) {
                    outputContent.append(line).append("\n");
                }
            } else {
                outputContent.append(line).append("\n");
            }
        }
        Files.writeString(outputPath, outputContent.toString(), StandardCharsets.UTF_8);
    }
}