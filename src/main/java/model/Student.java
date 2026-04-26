package model;

public record Student(String name, double average) {
    public String getName() {
        return name;
    }

    public double getAverage() {
        return average;
    }
}

