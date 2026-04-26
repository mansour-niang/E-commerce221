package model;

public record Transaction(double amount, String currency, String userType) {
    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUserType() {
        return userType;
    }
}

