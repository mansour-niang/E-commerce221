package exercices.niveau2;

import model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Niveau2Exercises {
    private Niveau2Exercises() {
    }

    public static List<String> filterFruitsStartingWithA(List<String> fruits) {
        return fruits.stream()
            .filter(Objects::nonNull)
            .filter(fruit -> fruit.startsWith("A"))
            .toList();
    }

    public static List<String> extractEmails(List<User> users) {
        return users.stream()
            .filter(Objects::nonNull)
            .map(User::getEmail)
            .filter(Objects::nonNull)
            .toList();
    }

    public static int sumPricesAbove100(List<Integer> prices) {
        return prices.stream()
            .filter(Objects::nonNull)
            .filter(price -> price > 100)
            .mapToInt(Integer::intValue)
            .sum();
    }

    public static List<String> uniqueSortedNames(List<String> names) {
        return names.stream()
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .toList();
    }

    public static Optional<User> findAdmin(List<User> users) {
        return users.stream()
            .filter(Objects::nonNull)
            .filter(user -> "ADMIN".equals(user.getRole()))
            .findAny();
    }
}

