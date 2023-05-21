package com.example.leshemayapro.classes;

public class User extends Guest
{
    private String phone;
    private String email;
    private String id;

    public User(String id, String fullName, String email, String phone)
    {
        super(fullName);
        this.id = id;
        this.phone = phone;
        this.email = email;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {this.phone = phone;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + id + '\'' +
                ", phone_number='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}