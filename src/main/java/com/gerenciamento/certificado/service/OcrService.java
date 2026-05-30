package com.gerenciamento.certificado.service;

import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService {

    private final Path tessdataPath = Paths.get("temp", "tessdata").toAbsolutePath();

    @PostConstruct
    public void initializeTessdata() {
        try {
            Files.createDirectories(tessdataPath);
            downloadTessdataFile("por");
            downloadTessdataFile("eng");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing tessdata directory", e);
        }
    }

    private void downloadTessdataFile(String lang) {
        String fileName = lang + ".traineddata";
        Path destFile = tessdataPath.resolve(fileName);
        if (!Files.exists(destFile)) {
            try {
                String sourceUrl = "https://raw.githubusercontent.com/tesseract-ocr/tessdata/main/" + fileName;
                URL url = new URL(sourceUrl);
                try (InputStream in = url.openStream()) {
                    Files.copy(in, destFile, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                System.err.println("Could not download tesseract language file " + fileName + ": " + e.getMessage());
            }
        }
    }

    public OcrResult analyzeFile(byte[] fileBytes, String contentType) {
        String text = "";
        if (contentType != null && contentType.equalsIgnoreCase("application/pdf")) {
            try {
                text = extractTextFromPdf(fileBytes);
            } catch (Exception e) {
                text = "";
            }
        } else {
            try {
                text = extractTextFromImage(fileBytes);
            } catch (Exception e) {
                text = "";
            }
        }
        
        Integer hours = extractHours(text);
        String name = extractTitle(text);

        return new OcrResult(text, hours, name);
    }

    private String extractTextFromPdf(byte[] fileBytes) throws Exception {
        try (PDDocument document = PDDocument.load(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text != null && text.trim().length() > 50) {
                return text;
            }
            PDFRenderer renderer = new PDFRenderer(document);
            if (document.getNumberOfPages() > 0) {
                BufferedImage image = renderer.renderImageWithDPI(0, 150);
                return runTesseract(image);
            }
        }
        return "";
    }

    private String extractTextFromImage(byte[] fileBytes) throws Exception {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes)) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new IOException("Failed to read image bytes");
            }
            return runTesseract(image);
        }
    }

    private String runTesseract(BufferedImage image) throws Exception {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath.toString());
        tesseract.setLanguage("por+eng");
        return tesseract.doOCR(image);
    }

    private Integer extractHours(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("(?i)(\\d+)\\s*(?:horas|horas\\b|hrs\\b|hs\\b|h\\b)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                int val = Integer.parseInt(matcher.group(1));
                if (val > 0 && val <= 300) {
                    return val;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String extractTitle(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Certificado Submetido";
        }
        String[] lines = text.split("\\n");
        for (String line : lines) {
            String clean = line.trim();
            if (clean.toLowerCase().contains("certificado") || clean.toLowerCase().contains("declaração")) {
                if (clean.length() > 10 && clean.length() < 100) {
                    return clean;
                }
            }
        }
        for (String line : lines) {
            String clean = line.trim();
            if (clean.length() > 10 && clean.length() < 60) {
                return clean;
            }
        }
        return "Certificado Submetido";
    }

    public static class OcrResult {
        private String text;
        private Integer hours;
        private String suggestedName;

        public OcrResult(String text, Integer hours, String suggestedName) {
            this.text = text;
            this.hours = hours;
            this.suggestedName = suggestedName;
        }

        public String getText() { return text; }
        public Integer getHours() { return hours; }
        public String getSuggestedName() { return suggestedName; }
    }
}
