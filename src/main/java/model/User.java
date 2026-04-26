package model;

public record User(String id, String email, String role, Address address) {
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Address getAddress() {
        return address;
    }
}

