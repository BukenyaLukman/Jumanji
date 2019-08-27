package com.example.jumanji.Model;

public class AdminOrders {
    private String City,State,address,date,name,phone,time,totalAmount;

    public AdminOrders() {

    }

    public AdminOrders(String city, String state, String address, String date, String name, String phone, String time, String totalAmount) {
        City = city;
        State = state;
        this.address = address;
        this.date = date;
        this.name = name;
        this.phone = phone;
        this.time = time;
        this.totalAmount = totalAmount;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
