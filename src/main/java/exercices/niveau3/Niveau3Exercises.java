package exercices.niveau3;

import model.*;

import java.util.*;
import java.util.stream.Collectors;

public final class Niveau3Exercises {
    private Niveau3Exercises() {
    }

    public static Map<String, List<Transaction>> groupTransactionsByCurrency(List<Transaction> transactions) {
        return transactions.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(Transaction::getCurrency));
    }

    public static DoubleSummaryStatistics summarizeProducts(List<Product> products) {
        return products.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.summarizingDouble(Product::getPrice));
    }

    public static Map<Boolean, List<Student>> partitionStudentsByAverage(List<Student> students) {
        return students.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.partitioningBy(student -> student.getAverage() >= 10));
    }

    public static List<Item> collectAllUniqueItems(List<Order> orders) {
        return orders.stream()
            .filter(Objects::nonNull)
            .flatMap(order -> order.getItems().stream())
            .distinct()
            .toList();
    }

    public static String resolveStreet(User user) {
        return Optional.ofNullable(user)
            .map(User::getAddress)
            .map(Address::getStreet)
            .orElse("Rue inconnue");
    }
}

