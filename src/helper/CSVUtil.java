package helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling CSV file operations, such as removing empty rows.
 */

public class CSVUtil {
    /**
     * Removes empty rows from a CSV file. This method reads all lines from the file,
     * filters out the empty or whitespace-only lines, and rewrites the file with the
     * remaining lines.
     *
     * @param filePath The path to the CSV file that needs to be cleaned.
     * @throws IOException If an error occurs while reading from or writing to the file.
     */
    public static void removeEmptyRows(String filePath) throws IOException {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();

        // Read the CSV file and filter out empty lines
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line is not empty or contains only whitespace
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }

        // Rewrite the file with the filtered lines
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

}
