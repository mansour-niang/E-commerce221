package Dakardrop;

import Dakardrop.exceptions.InsufficientStockException;
import Dakardrop.exceptions.InvalidProductException;
import Dakardrop.inventory.InventoryService;
import Dakardrop.inventory.Money;
import Dakardrop.inventory.Product;
import Dakardrop.inventory.SKU;
import Dakardrop.model.ProductStockView;
import Dakardrop.storage.InventoryStorage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Petite console interactive pour tester Dakardrop avec Scanner.
 */
public class DakardropScannerApp {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        InventoryService service = new InventoryService();
        Map<String, Product> catalog = new HashMap<>();

        System.out.println("=== Dakardrop - Console de test inventaire ===");

        // Charger l'inventaire sauvegardé s'il existe
        try {
            service = InventoryStorage.loadFromJson();
            if (service.getProductCount() > 0) {
                System.out.println("✓ Inventaire chargé depuis dakardrop_inventory.json (" + service.getProductCount() + " produits)\n");
            } else {
                System.out.println("⚠ Fichier JSON vide ou inexistant. Chargement des produits par défaut...\n");
                initializeDefaultProducts(service);
            }
        } catch (Exception e) {
            System.out.println("⚠ Erreur lors du chargement: " + e.getMessage());
            System.out.println("   Chargement des produits par défaut...\n");
            initializeDefaultProducts(service);
        }


        boolean running = true;
        while (running) {
            printMenu();
            String choice = readText("Choix");
            try {
                switch (choice) {
                    case "1" -> createProduct(service, catalog);
                    case "2" -> addStock(service);
                    case "3" -> removeStock(service);
                    case "4" -> showStock(service);
                    case "5" -> showLowStock(service);
                    case "6" -> showAllProducts(service);
                    case "7" -> saveInventory(service);
                    case "0" -> running = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (InvalidProductException | InsufficientStockException | IllegalArgumentException e) {
                System.out.println("[ERREUR] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERREUR SYSTÈME] " + e.getMessage());
            }
            System.out.println();
        }

        // Proposer de sauvegarder avant de quitter
        try {
            String save = readText("Sauvegarder avant de quitter? (o/n)");
            if (save.equalsIgnoreCase("o") || save.equalsIgnoreCase("oui")) {
                InventoryStorage.saveToJson(service);
            }
        } catch (Exception e) {
            System.out.println("[ERREUR] " + e.getMessage());
        }

        System.out.println("Au revoir.");
    }

    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Créer un produit");
        System.out.println("2. Ajouter du stock");
        System.out.println("3. Retirer du stock");
        System.out.println("4. Voir le stock d'un SKU");
        System.out.println("5. Lister les produits sous seuil critique");
        System.out.println("6. Afficher tous les produits");
        System.out.println("7. Sauvegarder dans un fichier JSON");
        System.out.println("0. Quitter");
    }

    private static void createProduct(InventoryService service, Map<String, Product> catalog) {
        String skuValue = readText("SKU (ex: TSH-1000)");
        String name = readText("Nom");
        String description = readText("Description");
        BigDecimal price = readDecimal("Prix unitaire");
        int initialQuantity = readInt("Quantité initiale");

        Product product = new Product(new SKU(skuValue), name, description, new Money(price, "XOF"));
        service.createProduct(product, initialQuantity);
        catalog.put(product.getSku().getValue(), product);
        System.out.println("Produit créé : " + product);
        autoSave(service);
    }

    private static void addStock(InventoryService service) {
        SKU sku = new SKU(readText("SKU"));
        int quantity = readInt("Quantité à ajouter");
        service.addStock(sku, quantity);
        System.out.println("Stock mis à jour pour " + sku + " = " + service.getStock(sku));
        autoSave(service);
    }

    private static void removeStock(InventoryService service) {
        SKU sku = new SKU(readText("SKU"));
        int quantity = readInt("Quantité à retirer");
        service.removeStock(sku, quantity);
        System.out.println("Stock mis à jour pour " + sku + " = " + service.getStock(sku));
        autoSave(service);
    }

    private static void showStock(InventoryService service) {
        SKU sku = new SKU(readText("SKU"));
        System.out.println("Stock actuel : " + service.getStock(sku));
    }

    private static void showLowStock(InventoryService service) {
        int threshold = readInt("Seuil critique");
        System.out.println("Produits sous le seuil :");
        for (ProductStockView view : service.listLowStockViews(threshold)) {
            System.out.println("- " + view);
        }
    }

    private static void showAllProducts(InventoryService service) {
        var items = service.getAllItems();
        if (items.isEmpty()) {
            System.out.println("Aucun produit en stock.");
        } else {
            System.out.println("=== Inventaire complet ===");
            for (ProductStockView view : items) {
                System.out.println("  " + view);
            }
        }
    }

    private static void saveInventory(InventoryService service) throws Exception {
        String filename = readText("Nom du fichier (défaut: dakardrop_inventory.json)");
        if (filename.trim().isEmpty()) {
            filename = "dakardrop_inventory.json";
        }
        Dakardrop.storage.InventoryStorage.saveToJson(service, filename);
    }

    private static String readText(String label) {
        System.out.print(label + ": ");
        String value = SCANNER.nextLine().trim();
        while (value.isEmpty()) {
            System.out.print(label + " (obligatoire): ");
            value = SCANNER.nextLine().trim();
        }
        return value;
    }

    private static int readInt(String label) {
        while (true) {
            String raw = readText(label);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un entier valide.");
            }
        }
    }

    private static BigDecimal readDecimal(String label) {
        while (true) {
            String raw = readText(label);
            try {
                return new BigDecimal(raw);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
    }

    /**
     * Sauvegarde automatique après chaque opération.
     */
    private static void autoSave(InventoryService service) {
        try {
            InventoryStorage.saveToJson(service);
        } catch (Exception e) {
            System.out.println("⚠ Attention: les changements n'ont pas pu être sauvegardés");
        }
    }

    /**
     * Pré-charge des produits de démonstration pour faciliter les tests.
     */
    private static void initializeDefaultProducts(InventoryService service) {
        System.out.println("\n📦 Chargement des produits de démonstration...\n");

        try {
            // Produit 1: Stock normal
            Product tshirt = new Product(
                    new SKU("TSH-1000"),
                    "T-Shirt DakarDrop",
                    "T-shirt coton premium",
                    new Money(new BigDecimal("5000"), "XOF")
            );
            service.createProduct(tshirt, 15);
            System.out.println("✓ TSH-1000 : T-Shirt DakarDrop (15 en stock)");

            // Produit 2: Stock bas (pour test seuil critique)
            Product charger = new Product(
                    new SKU("ACC-2000"),
                    "Chargeur USB-C",
                    "Chargeur rapide 18W",
                    new Money(new BigDecimal("12000"), "XOF")
            );
            service.createProduct(charger, 3);
            System.out.println("✓ ACC-2000 : Chargeur USB-C (3 en stock - STOCK BAS)");

            // Produit 3: Stock normal
            Product jeans = new Product(
                    new SKU("PNT-3000"),
                    "Jeans Classique",
                    "Jean bleu délavé",
                    new Money(new BigDecimal("8500"), "XOF")
            );
            service.createProduct(jeans, 25);
            System.out.println("✓ PNT-3000 : Jeans Classique (25 en stock)");

            // Produit 4: Stock très bas
            Product headphones = new Product(
                    new SKU("AUD-4000"),
                    "Casque Bluetooth",
                    "Casque wireless",
                    new Money(new BigDecimal("15000"), "XOF")
            );
            service.createProduct(headphones, 2);
            System.out.println("✓ AUD-4000 : Casque Bluetooth (2 en stock - TRÈS BAS)");

            // Produit 5: Stock moyen
            Product hat = new Product(
                    new SKU("HAT-5000"),
                    "Casquette Baseball",
                    "Casquette ajustable",
                    new Money(new BigDecimal("3500"), "XOF")
            );
            service.createProduct(hat, 8);
            System.out.println("✓ HAT-5000 : Casquette Baseball (8 en stock)");

            // Produit 6: Stock critique
            Product watch = new Product(
                    new SKU("WTC-6000"),
                    "Montre digitale",
                    "Montre sport",
                    new Money(new BigDecimal("18000"), "XOF")
            );
            service.createProduct(watch, 1);
            System.out.println("✓ WTC-6000 : Montre digitale (1 en stock - CRITIQUE)");

            System.out.println("\n📊 Inventaire initial chargé (6 produits)");
            System.out.println("   Testez: option 5 pour voir les produits sous seuil (< 5 unités)");
            System.out.println("   Testez: option 3 pour retirer du stock");
            System.out.println("   Testez: option 6 pour afficher tous les produits\n");

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des produits: " + e.getMessage());
        }
    }
}
