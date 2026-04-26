package model;

public record Address(String street) {
    public String getStreet() {
        return street;
    }
}

