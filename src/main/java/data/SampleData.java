package data;

import model.*;

import java.util.List;

public final class SampleData {
    private SampleData() {
    }

    public static List<String> fruits() {
        return List.of("Ananas", "Banane", "Abricot", "Poire");
    }

    public static List<User> users() {
        return List.of(
            new User("1", "ada@example.com", "ADMIN", new Address("Rue des Fleurs")),
            new User("2", "moussa@example.com", "USER", new Address("Rue du Marché")),
            new User("3", "sara@example.com", "USER", null)
        );
    }

    public static List<Integer> prices() {
        return List.of(50, 120, 200, 80, 300);
    }

    public static List<String> names() {
        return List.of("Ali", "Sara", "Ali", "Moussa", "Binta");
    }

    public static List<Transaction> transactions() {
        return List.of(
            new Transaction(150, "XOF", "STANDARD"),
            new Transaction(80, "EUR", "PREMIUM"),
            new Transaction(200, "XOF", "PREMIUM")
        );
    }

    public static List<Product> products() {
        return List.of(
            new Product("Laptop", 1200),
            new Product("Mouse", 25),
            new Product("Phone", 850)
        );
    }

    public static List<Student> students() {
        return List.of(
            new Student("Amina", 12.5),
            new Student("Oumar", 8.0),
            new Student("Yara", 10.0)
        );
    }

    public static List<Order> orders() {
        return List.of(
            new Order(List.of(new Item("Cahier"), new Item("Stylo"))),
            new Order(List.of(new Item("Stylo"), new Item("Sac")))
        );
    }

    public static List<String> ids() {
        return List.of("1", "3", "99");
    }

    public static List<String> csvLines() {
        return List.of(
            "Alice;29;2500.5",
            "Bob;not-a-number;1800",
            "Claire;41;3200",
            "BadLine"
        );
    }

    public static List<Employee> employees() {
        return List.of(
            new Employee("Alice", 29, 2500.5),
            new Employee("Claire", 41, 3200)
        );
    }
}

