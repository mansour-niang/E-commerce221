package exercices.niveau1;

import contracts.Validator;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Niveau1Exercises {
    private Niveau1Exercises() {
    }

    public static Predicate<Integer> isPositive() {
        return number -> number != null && number > 0;
    }

    public static Consumer<String> logger() {
        return text -> System.out.println("[LOG] " + String.valueOf(text).toUpperCase());
    }

    public static Function<String, Integer> doubleValue() {
        return text -> Integer.parseInt(text) * 2;
    }

    public static Supplier<Double> interestRateSupplier() {
        return () -> Math.random() * 5.0;
    }

    public static Validator emailValidator() {
        return text -> text != null && text.contains("@");
    }
}

