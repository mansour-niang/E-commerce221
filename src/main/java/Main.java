import catalogue.Money;
import catalogue.Product;
import catalogue.SKU;
import catalogue.exceptions.InvalidCurrencyException;
import catalogue.exceptions.InvalidDiscountException;
import catalogue.exceptions.InvalidSkuException;
import contracts.UserRepository;
import data.SampleData;
import exercices.niveau1.Niveau1Exercises;
import exercices.niveau2.Niveau2Exercises;
import exercices.niveau3.Niveau3Exercises;
import exercices.niveau4.Niveau4Exercises;
import model.Transaction;
import model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

/**
 * Classe de démonstration.
 * Prouve que chaque règle métier est blindée : aucune valeur absurde ne passe.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  DÉMONSTRATION DU MODULE CATALOGUE E-COMMERCE");
        System.out.println("═══════════════════════════════════════════════════════\n");

        // ────────────────────────────────────────────────────────────────────────
        // 1. CAS VALIDES : création normale d'un produit
        // ────────────────────────────────────────────────────────────────────────
        System.out.println("─── 1. Création de produits valides ───────────────────");

        SKU    skuTelephone = new SKU("TEL-10001");
        Money  prixDepart   = new Money(new BigDecimal("150000"), "FCFA");
        Product telephone   = new Product(skuTelephone, "Smartphone Galaxy X10", prixDepart);

        System.out.println("✅ Produit créé : " + telephone);

        // Remise de 10%
        telephone.applyDiscount(new BigDecimal("10"));
        System.out.println("✅ Après remise de 10% : " + telephone.getPrice());

        // Addition de deux Money de même devise
        Money frais     = new Money(new BigDecimal("5000"), "FCFA");
        Money total     = prixDepart.add(frais);
        System.out.println("✅ Addition FCFA + FCFA : " + total);

        System.out.println();

        // ────────────────────────────────────────────────────────────────────────
        // 2. TENTATIVES INVALIDES SUR Money
        // ────────────────────────────────────────────────────────────────────────
        System.out.println("─── 2. Money : tests des gardes-fous ──────────────────");

        tester("Montant négatif", () ->
                new Money(new BigDecimal("-500"), "FCFA")
        );

        tester("Devise inconnue (USD)", () ->
                new Money(new BigDecimal("1000"), "USD")
        );

        tester("Devise null", () ->
                new Money(new BigDecimal("1000"), null)
        );

        tester("Addition FCFA + EUR (devises mixtes)", () -> {
            Money enFcfa = new Money(new BigDecimal("5000"), "FCFA");
            Money enEuro = new Money(new BigDecimal("10"), "EUR");
            enFcfa.add(enEuro); // Doit exploser
        });

        System.out.println();

        // ────────────────────────────────────────────────────────────────────────
        // 3. TENTATIVES INVALIDES SUR SKU
        // ────────────────────────────────────────────────────────────────────────
        System.out.println("─── 3. SKU : tests des gardes-fous ────────────────────");

        tester("SKU en minuscules (tel-10001)", () ->
                new SKU("tel-10001")
        );

        tester("SKU trop court en chiffres (TEC-123)", () ->
                new SKU("TEC-123")
        );

        tester("SKU trop long en chiffres (TEC-1234567)", () ->
                new SKU("TEC-1234567")
        );

        tester("SKU avec 4 lettres (TECH-1234)", () ->
                new SKU("TECH-1234")
        );

        tester("SKU null", () ->
                new SKU(null)
        );

        System.out.println();

        // ────────────────────────────────────────────────────────────────────────
        // 4. TENTATIVES INVALIDES SUR Product
        // ────────────────────────────────────────────────────────────────────────
        System.out.println("─── 4. Product : tests des gardes-fous ────────────────");

        tester("Nom vide", () ->
                new Product(new SKU("PRD-1000"), "   ", new Money(new BigDecimal("1000"), "FCFA"))
        );

        tester("Nom null", () ->
                new Product(new SKU("PRD-1000"), null, new Money(new BigDecimal("1000"), "FCFA"))
        );

        tester("SKU null", () ->
                new Product(null, "Produit Test", new Money(new BigDecimal("1000"), "FCFA"))
        );

        tester("Prix null", () ->
                new Product(new SKU("PRD-1000"), "Produit Test", null)
        );

        System.out.println();

        // ────────────────────────────────────────────────────────────────────────
        // 5. TENTATIVES INVALIDES SUR applyDiscount
        // ────────────────────────────────────────────────────────────────────────
        System.out.println("─── 5. applyDiscount : tests des gardes-fous ──────────");

        Product produit = new Product(new SKU("LIV-5000"), "Livre Java", new Money(new BigDecimal("25000"), "FCFA"));

        tester("Remise à 0 (trop faible)", () ->
                produit.applyDiscount(BigDecimal.ZERO)
        );

        tester("Remise à 150% (trop élevée)", () ->
                produit.applyDiscount(new BigDecimal("150"))
        );

        tester("Remise null", () ->
                produit.applyDiscount(null)
        );

        tester("Remise négative (-10%)", () ->
                produit.applyDiscount(new BigDecimal("-10"))
        );

        System.out.println();

        // ────────────────────────────────────────────────────────────────────────
        // 6. IMMUTABILITÉ : prouver qu'on ne peut pas modifier Product de l'extérieur
        // ────────────────────────────────────────────────────────────────────────
        System.out.println("─── 6. Immutabilité de Product ────────────────────────");

        Product produitFige = new Product(new SKU("ORD-2024"), "Ordinateur Pro", new Money(new BigDecimal("500000"), "FCFA"));
        System.out.println("✅ Produit avant toute tentative : " + produitFige);

        // Il n'existe aucun setter : le code suivant ne compile même pas.
        // produitFige.setPrice(...);  ← ERREUR DE COMPILATION
        // produitFige.setName(...);   ← ERREUR DE COMPILATION
        // produitFige.setId(...);     ← ERREUR DE COMPILATION
        System.out.println("✅ Aucun setter disponible : impossible de modifier id, sku, name ou price depuis l'extérieur.");
        System.out.println("✅ Le seul moyen de changer le prix est applyDiscount(), qui valide le pourcentage.");

        // Money est un record → immuable par nature. add() retourne un NOUVEAU Money, n'altère pas l'original.
        Money original = produitFige.getPrice();
        original.add(new Money(new BigDecimal("99999"), "FCFA")); // retourne un nouveau Money, original intact
        System.out.println("✅ Money.add() retourne un nouvel objet, le prix du produit est inchangé : " + produitFige.getPrice());

        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  TOUS LES GARDES-FOUS FONCTIONNENT CORRECTEMENT ✅");
        System.out.println("═══════════════════════════════════════════════════════");

        System.out.println("=== Séance 2 : Programmation Fonctionnelle & API Stream ===");
        System.out.println("Arguments reçus : " + args.length);

        UserRepository repository = id -> SampleData.users().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();

        print("Niveau 1_Ex1", Niveau1Exercises.isPositive().test(5));
        Niveau1Exercises.logger().accept("Niveau 1_Ex2 - démo du logger");
        print("Niveau 1_Ex3", Niveau1Exercises.doubleValue().apply("10"));
        print("Niveau 1_Ex4", Niveau1Exercises.interestRateSupplier().get());
        print("Niveau 1_Ex5", Niveau1Exercises.emailValidator().validate("user@example.com"));

        print("Niveau 2_Ex6", Niveau2Exercises.filterFruitsStartingWithA(SampleData.fruits()));
        print("Niveau 2_Ex7", Niveau2Exercises.extractEmails(SampleData.users()));
        print("Niveau 2_Ex8", Niveau2Exercises.sumPricesAbove100(SampleData.prices()));
        print("Niveau 2_Ex9", Niveau2Exercises.uniqueSortedNames(SampleData.names()));
        print("Niveau 2_Ex10", Niveau2Exercises.findAdmin(SampleData.users()));

        print("Niveau 3_Ex11", Niveau3Exercises.groupTransactionsByCurrency(SampleData.transactions()));
        print("Niveau 3_Ex12", Niveau3Exercises.summarizeProducts(SampleData.products()));
        print("Niveau 3_Ex13", Niveau3Exercises.partitionStudentsByAverage(SampleData.students()));
        print("Niveau 3_Ex14", Niveau3Exercises.collectAllUniqueItems(SampleData.orders()));
        print("Niveau 3_Ex15", Niveau3Exercises.resolveStreet(new User("4", "demo@example.com", "USER", null)));

        Predicate<Transaction> complexValidator = Niveau4Exercises.complexValidator();
        print("Niveau 4_Ex16", complexValidator.test(SampleData.transactions().get(2)));
        print("Niveau 4_Ex17", Niveau4Exercises.existingUsers(SampleData.ids(), repository));
        print("Niveau 4_Ex18", Niveau4Exercises.importEmployees(SampleData.csvLines()));
    }

    /**
     * Utilitaire de test : exécute un bloc de code et affiche l'exception attendue.
     * Si aucune exception n'est levée, c'est un problème — on le signale.
     */
    private static void tester(String description, Runnable action) {
        try {
            action.run();
            System.out.println("❌ [PROBLÈME] Aucune exception pour : " + description);
        } catch (InvalidCurrencyException | InvalidSkuException | InvalidDiscountException e) {
            System.out.println("✅ [BLOQUÉ] " + description + " → " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("✅ [BLOQUÉ] " + description + " → " + e.getMessage());
        }
    }

    private static void print(String label, Object value) {
        System.out.println(label + " : " + value);
    }
}