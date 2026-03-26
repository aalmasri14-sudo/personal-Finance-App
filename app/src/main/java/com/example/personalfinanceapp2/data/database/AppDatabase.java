package com.example.personalfinanceapp2.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.personalfinanceapp2.data.dao.TransactionDao;
import com.example.personalfinanceapp2.data.model.Transaction;

@Database(entities = {Transaction.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
}