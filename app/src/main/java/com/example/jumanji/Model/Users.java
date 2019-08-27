package com.example.jumanji.Model;

public class Users {

    public String FirstName,LastName,Phone,image,address;


    public Users(){

    }

    public Users(String firstName, String lastName, String phone, String image, String address) {
        FirstName = firstName;
        LastName = lastName;
        Phone = phone;
        this.image = image;
        this.address = address;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
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
}
