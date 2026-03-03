package models;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String role;

    //  Constructor
    public User(int id, String fullName, String email, String password, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    
      //getter methods
    public int getId() { return id; }

    public String getFullName() { return fullName; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getRole() { return role; }
}