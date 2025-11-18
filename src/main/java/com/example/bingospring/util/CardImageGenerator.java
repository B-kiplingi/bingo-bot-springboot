package com.example.bingospring.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class CardImageGenerator {
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "([\\x{1F600}-\\x{1F64F}]|[\\x{1F300}-\\x{1F5FF}]|[\\x{1F680}-\\x{1F6FF}]|[\\x{2600}-\\x{26FF}]|[\\x{2700}-\\x{27BF}]|[\\x{1F900}-\\x{1F9FF}]|[\\x{1F1E0}-\\x{1F1FF}])"
    );
    private static final Pattern DISCORD_EMOJI_PATTERN = Pattern.compile("<a?:(\\w+):(\\d+)>");

    public static File generateCardImage(boolean[][] checked, String[][] labels, String user) {
        int rows = labels.length;
        int cols = labels[0].length;

        int cellWidth = 200;
        int cellHeight = 100;
        int borderThickness = 2;
        int labelMargin = 30;

        int width = cols * cellWidth + borderThickness + labelMargin;
        int height = rows * cellHeight + borderThickness + labelMargin;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Draw column labels
        Font labelFont = FontLoader.loadAnyTtfFont(Font.BOLD, 18);
        g.setFont(labelFont);
        g.setColor(Color.BLACK);
        FontMetrics labelFm = g.getFontMetrics();

        for (int c = 0; c < cols; c++) {
            String colLabel = String.valueOf((char)('A' + c));
            int x = labelMargin + c * cellWidth + cellWidth / 2 - labelFm.stringWidth(colLabel) / 2;
            int y = labelMargin / 2 + labelFm.getAscent() / 2;
            g.drawString(colLabel, x, y);
        }

        // Draw row labels
        for (int r = 0; r < rows; r++) {
            String rowLabel = String.valueOf(r + 1);
            int x = labelMargin / 2 - labelFm.stringWidth(rowLabel) / 2;
            int y = labelMargin + r * cellHeight + cellHeight / 2 + labelFm.getAscent() / 2;
            g.drawString(rowLabel, x, y);
        }

        // Draw cells
        Font font = FontLoader.loadAnyTtfFont(Font.BOLD, 16);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = labelMargin + c * cellWidth;
                int y = labelMargin + r * cellHeight;

                // Fill cell background
                if (checked[r][c]) {
                    g.setColor(new Color(100, 200, 100));
                } else {
                    g.setColor(new Color(230, 230, 230));
                }
                g.fillRect(x, y, cellWidth, cellHeight);

                // Draw border
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(borderThickness));
                g.drawRect(x, y, cellWidth, cellHeight);

                // Draw content (text + emojis)
                g.setColor(Color.BLACK);
                String text = labels[r][c];

                int padding = 10;
                int emojiSize = fm.getHeight();

                List<ContentLine> lines = wrapContent(text, fm, cellWidth - padding * 2, emojiSize);

                // Compute total height
                int totalHeight = 0;
                for (ContentLine line : lines) {
                    totalHeight += Math.max(fm.getHeight(), line.hasEmoji ? emojiSize : 0);
                }

                // Start Y position (center vertically)
                int startY = y + (cellHeight - totalHeight) / 2;

                // Draw each line
                for (ContentLine line : lines) {
                    int lineHeight = Math.max(fm.getHeight(), line.hasEmoji ? emojiSize : 0);
                    int lineWidth = calculateLineWidth(line, fm, emojiSize);
                    int lineX = x + (cellWidth - lineWidth) / 2; // Center horizontally

                    drawContentLine(g, line, lineX, startY, fm, emojiSize);
                    startY += lineHeight;
                }
            }
        }

        g.dispose();

        try {
            File file = File.createTempFile(user, ".png");
            ImageIO.write(image, "png", file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<ContentLine> wrapContent(String text, FontMetrics fm, int maxWidth, int emojiSize) {
        List<ContentLine> lines = new ArrayList<>();

        for (String paragraph : text.split("\n")) {
            ContentLine currentLine = new ContentLine();

            List<ContentPart> parts = parseTextWithEmojis(paragraph);

            for (ContentPart part : parts) {
                if (part.isEmoji) {
                    // Handle emoji as a single unit
                    int partWidth = emojiSize;

                    if (currentLine.isEmpty()) {
                        currentLine.add(part);
                    } else {
                        int spacing = 2;
                        int newWidth = calculateLineWidth(currentLine, fm, emojiSize) + spacing + partWidth;

                        if (newWidth <= maxWidth) {
                            currentLine.addSpacing(spacing);
                            currentLine.add(part);
                        } else {
                            lines.add(currentLine);
                            currentLine = new ContentLine();
                            currentLine.add(part);
                        }
                    }
                } else {
                    // Handle text - split into words and wrap
                    String[] words = part.content.split(" ");

                    for (String word : words) {
                        if (word.isEmpty()) continue;

                        int wordWidth = fm.stringWidth(word);

                        if (currentLine.isEmpty()) {
                            currentLine.add(new ContentPart(word, false));
                        } else {
                            int spaceWidth = fm.stringWidth(" ");
                            int newWidth = calculateLineWidth(currentLine, fm, emojiSize) + spaceWidth + wordWidth;

                            if (newWidth <= maxWidth) {
                                currentLine.addSpacing(spaceWidth);
                                currentLine.add(new ContentPart(word, false));
                            } else {
                                lines.add(currentLine);
                                currentLine = new ContentLine();
                                currentLine.add(new ContentPart(word, false));
                            }
                        }
                    }
                }
            }

            if (!currentLine.isEmpty()) {
                lines.add(currentLine);
            }
        }

        return lines;
    }

    private static List<ContentPart> parseTextWithEmojis(String text) {
        List<ContentPart> parts = new ArrayList<>();

        // First check for Discord custom emojis
        Matcher discordMatcher = DISCORD_EMOJI_PATTERN.matcher(text);
        int lastEnd = 0;

        while (discordMatcher.find()) {
            // Add text before emoji
            if (discordMatcher.start() > lastEnd) {
                String textBefore = text.substring(lastEnd, discordMatcher.start());
                addTextParts(textBefore, parts);
            }

            // Add Discord emoji
            String emojiId = discordMatcher.group(2);
            String emojiUrl = "https://cdn.discordapp.com/emojis/" + emojiId + ".png";
            parts.add(new ContentPart(emojiUrl, true));

            lastEnd = discordMatcher.end();
        }

        // Add remaining text
        if (lastEnd < text.length()) {
            String remaining = text.substring(lastEnd);
            addTextParts(remaining, parts);
        }

        return parts;
    }

    private static void addTextParts(String text, List<ContentPart> parts) {
        Matcher emojiMatcher = EMOJI_PATTERN.matcher(text);
        int lastEnd = 0;

        while (emojiMatcher.find()) {
            // Add text before emoji
            if (emojiMatcher.start() > lastEnd) {
                String textPart = text.substring(lastEnd, emojiMatcher.start());
                if (!textPart.isEmpty()) {
                    parts.add(new ContentPart(textPart, false));
                }
            }

            // Add unicode emoji
            String emoji = emojiMatcher.group();
            String emojiUrl = getEmojiUrl(emoji);
            parts.add(new ContentPart(emojiUrl, true));

            lastEnd = emojiMatcher.end();
        }

        // Add remaining text
        if (lastEnd < text.length()) {
            String textPart = text.substring(lastEnd);
            if (!textPart.isEmpty()) {
                parts.add(new ContentPart(textPart, false));
            }
        }
    }

    private static String getEmojiUrl(String emoji) {
        // Convert emoji to unicode codepoint(s) for Twemoji URL
        StringBuilder codepoints = new StringBuilder();
        for (int i = 0; i < emoji.length(); i++) {
            int codepoint = emoji.codePointAt(i);
            if (Character.isSupplementaryCodePoint(codepoint)) {
                i++; // Skip the next char for supplementary characters
            }
            if (codepoints.length() > 0) {
                codepoints.append("-");
            }
            codepoints.append(Integer.toHexString(codepoint));
        }
        return "https://cdn.jsdelivr.net/gh/twitter/twemoji@latest/assets/72x72/" + codepoints + ".png";
    }

    private static int calculateLineWidth(ContentLine line, FontMetrics fm, int emojiSize) {
        int width = 0;
        for (int i = 0; i < line.parts.size(); i++) {
            ContentPart part = line.parts.get(i);
            width += part.isEmoji ? emojiSize : fm.stringWidth(part.content);

            // Add spacing
            if (i < line.parts.size() - 1) {
                width += line.spacings.size() > i ? line.spacings.get(i) : 0;
            }
        }
        return width;
    }

    private static void drawContentLine(Graphics2D g, ContentLine line, int x, int y, FontMetrics fm, int emojiSize) {
        int currentX = x;

        for (int i = 0; i < line.parts.size(); i++) {
            ContentPart part = line.parts.get(i);

            if (part.isEmoji) {
                BufferedImage emojiImg = loadEmojiImage(part.content);
                if (emojiImg != null) {
                    int emojiY = y + (emojiSize - fm.getAscent()) / 2;
                    System.out.println("height: " + fm.getHeight() + " emojiHeigth: " + emojiSize + " ascend: " + fm.getAscent() + " y: " + y + " emojiY: " + emojiY);
                    g.drawImage(emojiImg, currentX, emojiY, emojiSize, emojiSize, null);
                    currentX += emojiSize;
                } else {
                    // Fallback to text if image loading fails
                    g.drawString("?", currentX, y + fm.getAscent());
                    currentX += fm.stringWidth("?");
                }
            } else {
                g.drawString(part.content, currentX, y + fm.getAscent());
                currentX += fm.stringWidth(part.content);
            }

            // Add spacing
            if (i < line.spacings.size()) {
                currentX += line.spacings.get(i);
            }
        }
    }

    private static BufferedImage loadEmojiImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            System.err.println("Failed to load emoji from: " + url);
            return null;
        }
    }

    // Helper classes
    private static class ContentPart {
        String content; // Either text or emoji URL
        boolean isEmoji;

        ContentPart(String content, boolean isEmoji) {
            this.content = content;
            this.isEmoji = isEmoji;
        }
    }

    private static class ContentLine {
        List<ContentPart> parts = new ArrayList<>();
        List<Integer> spacings = new ArrayList<>(); // Spacing after each part
        boolean hasEmoji = false;

        void add(ContentPart part) {
            parts.add(part);
            if (part.isEmoji) {
                hasEmoji = true;
            }
        }

        void addSpacing(int spacing) {
            spacings.add(spacing);
        }

        boolean isEmpty() {
            return parts.isEmpty();
        }
    }
}