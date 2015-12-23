package com.example.accounting.Entity;

/**
 * Created by Дамир on 18.12.2015.
 */
public class Bill {
    private static int incID=0;
    private int id;
    private String billName;
    private String description;
    private double balance;

    public Bill(String billName, String description, double balance) {
        id = ++incID;
        this.billName = billName;
        this.description = description;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static void setIncID(int incID) {
        Bill.incID = incID;
    }

    @Override
    public String toString() {
        return "Bill[" +
                "id=" + id +
                ", billName='" + billName + '\'' +
                ", description='" + description + '\'' +
                ", balance=" + balance +
                ']';
    }
}
