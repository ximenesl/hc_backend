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
                System.err.println("Erro OCR PDF: " + e.getMessage());
                text = "";
            }
        } else {
            try {
                text = extractTextFromImage(fileBytes);
            } catch (Exception e) {
                System.err.println("Erro OCR Imagem: " + e.getMessage());
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
                BufferedImage image = renderer.renderImageWithDPI(0, 300);
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

        String lowerText = text.toLowerCase();
        
        // 1. Tenta Regex direto para os casos mais comuns: "40h", "40 horas", "40 hrs"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{1,3})\\s*(?:horas|hora|hrs|hs|h\\b)");
        java.util.regex.Matcher matcher = pattern.matcher(lowerText);
        if (matcher.find()) {
            try {
                int val = Integer.parseInt(matcher.group(1));
                if (val > 0 && val <= 500) {
                    return val;
                }
            } catch (Exception e) {}
        }

        // 2. Busca por proximidade: procura qualquer palavra relacionada a hora e busca o número mais próximo
        String[] words = lowerText.split("\\s+");
        java.util.List<Integer> numberIndices = new java.util.ArrayList<>();
        java.util.List<Integer> hourWordIndices = new java.util.ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String w = words[i].replaceAll("[^a-z0-9]", ""); // limpa pontuação
            if (w.matches("\\d{1,3}")) {
                numberIndices.add(i);
            } else if (w.equals("horas") || w.equals("hora") || w.equals("hrs") || 
                       w.equals("hs") || w.equals("horaria") || w.equals("horario") || w.equals("h")) {
                hourWordIndices.add(i);
            }
        }

        int minDistance = Integer.MAX_VALUE;
        Integer bestValue = null;

        for (int nIdx : numberIndices) {
            for (int hIdx : hourWordIndices) {
                int dist = Math.abs(nIdx - hIdx);
                if (dist < minDistance && dist <= 6) { // busca num raio de 6 palavras
                    try {
                        String numStr = words[nIdx].replaceAll("[^0-9]", "");
                        int val = Integer.parseInt(numStr);
                        if (val > 0 && val <= 500) {
                            minDistance = dist;
                            bestValue = val;
                        }
                    } catch (Exception e) {}
                }
            }
        }

        return bestValue;
    }

    private String extractTitle(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Certificado Submetido";
        }

        // 1. Normalize spacing and casing
        String normalizedText = text.replaceAll("\\s+", " ").trim();
        String lowerText = normalizedText.toLowerCase();

        // Target keywords list (case-insensitive search)
        String[] keywords = {
            "java", "spring boot", "spring", "hibernate", "jpa", "maven",
            "javascript", "typescript", "node.js", "node", "express", "react", "react native",
            "angular", "vue", "html", "css", "python", "django", "flask", "pandas", "numpy",
            "sql", "mysql", "postgresql", "oracle", "mongodb", "nosql", "banco de dados", "database",
            "php", "laravel", "c#", ".net", "asp.net", "abap", "sap", "salesforce",
            "docker", "kubernetes", "aws", "azure", "google cloud", "gcp", "cloud computing", "computação em nuvem",
            "git", "github", "gitlab", "figma", "ui/ux", "design", "photoshop", "illustrator",
            "scrum", "kanban", "agile", "metodologias ágeis", "flutter", "dart", "android", "ios", "mobile",
            "inteligência artificial", "ia", "ai", "machine learning", "data science", "ciência de dados",
            "qa", "testes", "selenium", "cypress", "lógica de programação", "algoritmos", "estrutura de dados",
            "redes de computadores", "segurança da informação", "cybersecurity", "linux",
            "trabalho em equipe", "colaboração", "liderança", "gestão de pessoas", "oratória",
            "comunicação", "comunicação assertiva", "inteligência emocional", "empatia",
            "portfólio", "portfolios", "portfolio", "resolução de problemas", "criatividade",
            "gestão do tempo", "produtividade", "negociação", "vendas", "customer success",
            "ética", "cidadania", "diversidade", "inclusão", "empreendedorismo", "inovação",
            "programação", "desenvolvimento", "computação", "tecnologia", "tecnologia da informação"
        };

        // Split normalized text into words to make indexing easy
        String[] words = normalizedText.split(" ");
        String[] lowerWords = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            lowerWords[i] = words[i].toLowerCase().replaceAll("[^a-z0-9à-ú#\\+\\-\\.]", "");
        }

        String bestCandidate = null;
        int bestScore = -1000;

        // Trigger words pattern
        Pattern titlePattern = Pattern.compile(
            "(?i)\\b(curso|palestra|workshop|treinamento|bootcamp|webinar|academia|academy|evento|simpósio|capacitação|minicurso|semana)\\b(?:\\s+(?:de|do|da|sobre|em|de introdução ao|de introdução à|para))?\\s+([A-ZÀ-Úa-zà-ú0-9\\s#\\+\\-\\.\\/]{3,80})"
        );
        Matcher matcher = titlePattern.matcher(normalizedText);

        while (matcher.find()) {
            String candidate = matcher.group(2);
            String triggerWord = matcher.group(1);
            candidate = cleanCandidateTitle(candidate);
            
            if (candidate != null && !candidate.trim().isEmpty()) {
                String fullTitleCandidate = triggerWord + " de " + candidate; // default format
                String matchedPart = matcher.group(0);
                int indexInMatched = matchedPart.indexOf(candidate);
                if (indexInMatched != -1) {
                    fullTitleCandidate = matchedPart.substring(0, indexInMatched + candidate.length());
                } else {
                    fullTitleCandidate = triggerWord + " " + candidate;
                }
                
                int score = evaluateTitle(fullTitleCandidate, keywords);
                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = fullTitleCandidate;
                }
            }
        }

        // 2. Proximity search: find keywords and look backwards for a trigger word
        for (int i = 0; i < words.length; i++) {
            String cleanWord = lowerWords[i];
            boolean isKeyword = false;
            for (String kw : keywords) {
                if (cleanWord.equals(kw) || (kw.contains(" ") && lowerText.contains(kw))) {
                    isKeyword = true;
                    break;
                }
            }

            if (isKeyword) {
                for (int j = Math.max(0, i - 12); j < i; j++) {
                    String w = lowerWords[j];
                    if (w.equals("curso") || w.equals("palestra") || w.equals("workshop") ||
                        w.equals("treinamento") || w.equals("bootcamp") || w.equals("webinar") ||
                        w.equals("academia") || w.equals("academy") || w.equals("evento") ||
                        w.equals("simpósio") || w.equals("capacitação") || w.equals("minicurso")) {
                        
                        StringBuilder sb = new StringBuilder();
                        for (int k = j; k <= Math.min(words.length - 1, i + 4); k++) {
                            sb.append(words[k]).append(" ");
                        }
                        String candidate = sb.toString().trim();
                        candidate = cleanCandidateTitle(candidate);
                        
                        if (candidate != null && candidate.length() > 5) {
                            int score = evaluateTitle(candidate, keywords);
                            if (score > bestScore) {
                                bestScore = score;
                                bestCandidate = candidate;
                            }
                        }
                    }
                }
            }
        }

        // 3. Fallback to lines matching "certificado" or "declaração"
        if (bestCandidate == null || bestScore < 10) {
            String[] lines = text.split("\\n");
            for (String line : lines) {
                String clean = line.trim();
                if (clean.toLowerCase().contains("certificado") || clean.toLowerCase().contains("declaração") || clean.toLowerCase().contains("certifica")) {
                    if (clean.length() > 10 && clean.length() < 100) {
                        return clean;
                    }
                }
            }
            // 4. Fallback to first line with reasonable length
            for (String line : lines) {
                String clean = line.trim();
                if (clean.length() > 10 && clean.length() < 70) {
                    return clean;
                }
            }
        }

        return bestCandidate != null ? bestCandidate : "Certificado Submetido";
    }

    private String cleanCandidateTitle(String candidate) {
        if (candidate == null) return null;
        
        candidate = candidate.replaceAll("[,\\.\\-\\s]+$", "");

        String[] stopWords = {
            "\\bcom\\b", "\\brealizado\\b", "\\bministrado\\b", "\\bpromovido\\b", 
            "\\borganizado\\b", "\\bno período\\b", "\\bna data\\b", "\\bno dia\\b", 
            "\\bdurante\\b", "\\bministrada\\b", "\\bconcluido\\b", "\\bconcluído\\b", 
            "\\bministrante\\b", "\\bcarga\\b", "\\bhoras\\b", "\\bdata\\b", 
            "\\bconcedido\\b", "\\boutorgado\\b", "\\bemitido\\b", "\\bna cidade\\b",
            "\\bsob a\\b", "\\bsob o\\b", "\\batravés\\b", "\\boferecido\\b",
            "\\bcom duração\\b", "\\bcom carga horária\\b", "\\bparabeniza\\b"
        };

        for (String stop : stopWords) {
            Pattern p = Pattern.compile("(?i)" + stop);
            Matcher m = p.matcher(candidate);
            if (m.find()) {
                candidate = candidate.substring(0, m.start());
            }
        }

        candidate = candidate.trim().replaceAll("[,\\.\\-\\s]+$", "");
        
        Pattern pConcluiu = Pattern.compile("(?i)\\b(concluiu|participou|obteve|realizou)\\b");
        Matcher mConcluiu = pConcluiu.matcher(candidate);
        if (mConcluiu.find()) {
            candidate = candidate.substring(0, mConcluiu.start());
        }

        candidate = candidate.trim().replaceAll("[,\\.\\-\\s]+$", "");
        
        if (candidate.length() < 3 || candidate.length() > 80) {
            return null;
        }

        return candidate;
    }

    private int evaluateTitle(String title, String[] keywords) {
        if (title == null || title.isEmpty()) {
            return -1000;
        }
        int score = 0;
        String lowerTitle = title.toLowerCase();

        int len = title.length();
        if (len >= 12 && len <= 55) {
            score += 20;
        } else if (len > 55 && len <= 80) {
            score += 5;
        } else if (len < 8) {
            score -= 30;
        }

        boolean hasKeyword = false;
        for (String kw : keywords) {
            Pattern p = Pattern.compile("\\b" + Pattern.quote(kw) + "\\b", Pattern.CASE_INSENSITIVE);
            if (p.matcher(lowerTitle).find()) {
                score += 50;
                hasKeyword = true;
            }
        }

        String[] eduTriggers = {
            "programação", "desenvolvimento", "tecnologia", "introdução", "básico", "avançado",
            "web", "mobile", "design", "agilidade", "gestão", "prática", "oficina", "palestra",
            "curso", "workshop", "bootcamp", "academia"
        };
        for (String edu : eduTriggers) {
            if (lowerTitle.contains(edu)) {
                score += 15;
            }
        }

        if (lowerTitle.startsWith("curso") || lowerTitle.startsWith("palestra") ||
            lowerTitle.startsWith("workshop") || lowerTitle.startsWith("bootcamp") ||
            lowerTitle.startsWith("treinamento")) {
            score += 15;
        }

        if (Character.isUpperCase(title.charAt(0))) {
            score += 10;
        }

        return score;
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
