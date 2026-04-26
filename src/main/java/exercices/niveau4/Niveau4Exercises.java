package exercices.niveau4;

import contracts.UserRepository;
import model.Employee;
import model.Transaction;
import model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class Niveau4Exercises {
    private Niveau4Exercises() {
    }

    public static Predicate<Transaction> positiveAmount() {
        return transaction -> transaction != null && transaction.getAmount() > 0;
    }

    public static Predicate<Transaction> xofCurrency() {
        return transaction -> transaction != null && "XOF".equals(transaction.getCurrency());
    }

    public static Predicate<Transaction> premiumUser() {
        return transaction -> transaction != null && "PREMIUM".equals(transaction.getUserType());
    }

    public static Predicate<Transaction> complexValidator() {
        return positiveAmount().and(xofCurrency()).or(premiumUser());
    }

    public static List<User> existingUsers(List<String> ids, UserRepository repository) {
        return ids.stream()
            .filter(Objects::nonNull)
            .map(repository::findById)
            .flatMap(Optional::stream)
            .toList();
    }

    public static List<Employee> importEmployees(List<String> csvLines) {
        return csvLines.stream()
            .filter(Objects::nonNull)
            .map(Niveau4Exercises::parseEmployee)
            .flatMap(Optional::stream)
            .toList();
    }

    private static Optional<Employee> parseEmployee(String line) {
        return Optional.ofNullable(line)
            .map(csv -> csv.trim().split(";"))
            .filter(parts -> parts.length == 3)
            .flatMap(parts -> {
                try {
                    return Optional.of(new Employee(
                        parts[0].trim(),
                        Integer.parseInt(parts[1].trim()),
                        Double.parseDouble(parts[2].trim())
                    ));
                } catch (RuntimeException exception) {
                    return Optional.empty();
                }
            });
    }
}

