package com.example.bingospring.util;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FontLoader {

    /**
     * Load any .ttf or .otf font from /app/data/ directory
     * Just finds the first font file and loads it
     * @param style Font style (Font.PLAIN, Font.BOLD, Font.ITALIC)
     * @param size Font size
     * @return The loaded Font, or default SansSerif if loading failed
     */
    public static Font loadAnyTtfFont(int style, float size) {
        File dataDir = new File("/app/data/");

        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("Directory /app/data/ does not exist");
            return new Font("SansSerif", style, (int) size);
        }

        File[] files = dataDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".ttf") || lower.endsWith(".otf");
        });

        if (files == null || files.length == 0) {
            System.out.println("No .ttf or .otf files found in /app/data/");
            return new Font("SansSerif", style, (int) size);
        }

        // Try to load the first .ttf file found
        File fontFile = files[0];

        try (InputStream fontStream = new FileInputStream(fontFile)) {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            Font derivedFont = baseFont.deriveFont(style, size);

            // Register the font so it's available system-wide
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont);

            System.out.println("Successfully loaded font: " + fontFile.getName() +
                    " (" + baseFont.getFamily() + ")");
            return derivedFont;

        } catch (IOException e) {
            System.err.println("Error reading font file: " + fontFile.getName());
            e.printStackTrace();
        } catch (FontFormatException e) {
            System.err.println("Invalid font format: " + fontFile.getName());
            e.printStackTrace();
        }

        // Fallback to default
        System.out.println("Falling back to SansSerif");
        return new Font("SansSerif", style, (int) size);
    }

    // Example usage
    public static void main(String[] args) {
        // Just load whatever .ttf is in /app/data/
        Font font = loadAnyTtfFont(Font.BOLD, 16f);
        System.out.println("Using font: " + font.getFamily());
    }
}