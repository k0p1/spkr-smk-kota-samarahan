package com.example.spkr.Model;

public class Users
{
    private String username, phone, password, image, address, note;

    public Users()
    {

    }

    public Users(String username, String phone, String password, String image, String address, String note , String email) {
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
        this.note=note;
    }


    public String getUsername() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
