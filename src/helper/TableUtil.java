package helper;

import java.util.ArrayList;
import java.util.List;

public class TableUtil {
    public static String padLineToTableWidth(String line, int width) {
        String content = line;
        if (!content.endsWith("|")) {
            content += " ";
        }
        while (content.length() < width) {
            content += " ";
        }
        if (!content.endsWith("|")) {
            content += "|";
        }
        return content;
    }
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
