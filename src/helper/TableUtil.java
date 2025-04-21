package helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for table formatting operations in the BTO housing system.
 * Provides methods for text manipulation to improve display in console-based tables
 * and listings.
 */
public class TableUtil {
    /**
     * Wraps text to fit within a specified width by breaking it into multiple lines.
     * This method is useful for formatting text to display properly in console tables
     * or fixed-width displays where content might otherwise overflow, such as enquiry text.
     *
     * @param text     The string to be wrapped. If null or empty, returns a list with one empty string.
     * @param maxWidth The maximum width (in characters) for each line.
     * @return A list of strings where each string represents a line that fits within the specified width.
     *         Line breaks occur at spaces when possible to avoid breaking words.
     */
    public List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        while (text.length() > maxWidth) {
            int breakIndex = text.lastIndexOf(' ', maxWidth);
            if (breakIndex == -1) breakIndex = maxWidth;
            lines.add(text.substring(0, breakIndex));
            text = text.substring(breakIndex).trim();
        }

        lines.add(text);
        return lines;
    }
}