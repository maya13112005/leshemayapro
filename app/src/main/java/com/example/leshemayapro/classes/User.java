package com.example.leshemayapro.classes;

public class User {

    private String fullName;
    private String phone;
    private String email;
    private String id;

    public User(String id, String fullName, String email, String phone) {
        this.setId(id);
        this.setPhone(phone);
        this.setEmail(email);
        this.setFullName(fullName);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String un) {
        this.id = un;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fn) {
        this.fullName = fn;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id='" + id + '\'' +
                ", phone_number='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", username='" + fullName + '\'' +
                '}';
    }

}