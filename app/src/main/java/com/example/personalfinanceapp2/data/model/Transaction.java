package com.example.personalfinanceapp2.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private String type;
    private String category;
    private long date;
    private String note;

    public Transaction(int id, double amount, String type, String category, long date, String note) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    @Ignore
    public Transaction(double amount, String type, String category, long date, String note) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}