package com.example.personalfinanceapp2.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinanceapp2.data.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Income'")
    LiveData<Double> getTotalIncome();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense'")
    LiveData<Double> getTotalExpense();
    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();
}