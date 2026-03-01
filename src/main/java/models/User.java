package models;

public class User {
    private int id;
    private String email;
    private String role;
    private String fullName;

    public User(int id, String email, String role, String fullName) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }

    // Getters
    public int getId() { return id; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
}