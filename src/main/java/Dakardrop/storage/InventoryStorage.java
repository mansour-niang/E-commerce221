package Dakardrop.storage;

import Dakardrop.inventory.InventoryService;
import Dakardrop.inventory.Money;
import Dakardrop.inventory.Product;
import Dakardrop.inventory.SKU;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Classe pour sauvegarder et charger l'inventaire en JSON.
 */
public class InventoryStorage {
    private static final String DEFAULT_FILE = "dakardrop_inventory.json";

    /**
     * Sauvegarde l'inventaire dans un fichier JSON (format simple et lisible).
     */
    public static void saveToJson(InventoryService service, String filename) throws IOException {
        String json = buildJson(service);
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json);
        }
        System.out.println("✓ Inventaire sauvegardé dans " + filename);
    }

    public static void saveToJson(InventoryService service) throws IOException {
        saveToJson(service, DEFAULT_FILE);
    }

    /**
     * Charge l'inventaire depuis un fichier JSON.
     */
    public static InventoryService loadFromJson(String filename) throws IOException {
        if (!new File(filename).exists()) {
            System.out.println("⚠ Fichier " + filename + " non trouvé. Inventaire vide créé.");
            return new InventoryService();
        }
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        return parseJson(content);
    }

    public static InventoryService loadFromJson() throws IOException {
        return loadFromJson(DEFAULT_FILE);
    }

    private static String buildJson(InventoryService service) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"1.0\",\n");
        sb.append("  \"timestamp\": ").append(System.currentTimeMillis()).append(",\n");
        sb.append("  \"productCount\": ").append(service.getProductCount()).append(",\n");
        sb.append("  \"products\": [\n");

        var items = service.getAllItems();
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            sb.append("    {\n");
            sb.append("      \"sku\": \"").append(escapeJson(item.getSku().getValue())).append("\",\n");
            sb.append("      \"name\": \"").append(escapeJson(item.getName())).append("\",\n");
            sb.append("      \"description\": \"").append(escapeJson(item.getDescription())).append("\",\n");
            sb.append("      \"unitPrice\": ").append(item.getUnitPrice().getAmount()).append(",\n");
            sb.append("      \"currency\": \"").append(item.getUnitPrice().getCurrency()).append("\",\n");
            sb.append("      \"quantity\": ").append(item.getQuantity()).append("\n");
            sb.append("    }");
            if (i < items.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private static InventoryService parseJson(String jsonContent) {
        InventoryService service = new InventoryService();
        try {
            // Parsing simple du JSON
            String[] lines = jsonContent.split("\n");
            String currentSku = null;
            String currentName = null;
            String currentDesc = "";
            BigDecimal currentPrice = null;
            String currentCurrency = null;
            int currentQty = 0;
            boolean inProduct = false;

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"sku\":")) {
                    currentSku = extractJsonValue(line);
                    inProduct = true;
                } else if (line.startsWith("\"name\":")) {
                    currentName = extractJsonValue(line);
                } else if (line.startsWith("\"description\":")) {
                    currentDesc = extractJsonValue(line);
                } else if (line.startsWith("\"unitPrice\":")) {
                    currentPrice = new BigDecimal(extractJsonNumber(line));
                } else if (line.startsWith("\"currency\":")) {
                    currentCurrency = extractJsonValue(line);
                } else if (line.startsWith("\"quantity\":")) {
                    currentQty = Integer.parseInt(extractJsonNumber(line));

                    // Créer le produit
                    if (currentSku != null && currentName != null && currentPrice != null && currentCurrency != null) {
                        try {
                            Product product = new Product(
                                    new SKU(currentSku),
                                    currentName,
                                    currentDesc,
                                    new Money(currentPrice, currentCurrency)
                            );
                            service.createProduct(product, currentQty);
                        } catch (Exception e) {
                            System.err.println("⚠ Erreur lors du chargement du produit " + currentSku + ": " + e.getMessage());
                        }
                        // Réinitialiser pour le produit suivant
                        currentSku = null;
                        currentName = null;
                        currentDesc = "";
                        currentPrice = null;
                        currentCurrency = null;
                        currentQty = 0;
                        inProduct = false;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠ Erreur lors du parsing du JSON: " + e.getMessage());
        }
        return service;
    }

    private static String extractJsonValue(String line) {
        int start = line.indexOf("\"") + 1;
        int end = line.lastIndexOf("\"");
        if (start > 0 && end > start) {
            return line.substring(start, end);
        }
        return "";
    }

    private static String extractJsonNumber(String line) {
        int start = line.indexOf(":") + 1;
        int end = line.indexOf(",");
        if (end == -1) end = line.length();
        return line.substring(start, end).trim();
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}

