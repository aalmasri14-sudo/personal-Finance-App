package com.example.personalfinanceapp2.ui.transaction;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinanceapp2.R;
import com.example.personalfinanceapp2.data.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private List<Transaction> transactionsFull;
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
        this.transactions = transactions;
        this.transactionsFull = new ArrayList<>(transactions);
        this.listener = listener;
    }

    public void updateData(List<Transaction> newTransactions) {
        this.transactions.clear();
        this.transactions.addAll(newTransactions);

        this.transactionsFull.clear();
        this.transactionsFull.addAll(newTransactions);

        notifyDataSetChanged();
    }

    public void filter(String query) {
        transactions.clear();

        if (query == null || query.trim().isEmpty()) {
            transactions.addAll(transactionsFull);
        } else {
            String searchText = query.toLowerCase().trim();

            for (Transaction transaction : transactionsFull) {
                String category = transaction.getCategory() != null ? transaction.getCategory().toLowerCase() : "";
                String note = transaction.getNote() != null ? transaction.getNote().toLowerCase() : "";

                if (category.contains(searchText) || note.contains(searchText)) {
                    transactions.add(transaction);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.tvCategory.setText(transaction.getCategory());
        holder.tvNote.setText(transaction.getNote());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(transaction.getDate())));

        if (transaction.getType().equals("Income")) {
            holder.tvAmount.setText("+ $" + transaction.getAmount());
            holder.tvAmount.setTextColor(Color.parseColor("#2ECC71"));
            holder.imgCategory.setImageResource(android.R.drawable.ic_menu_upload);
            holder.imgCategory.setColorFilter(Color.parseColor("#2ECC71"));
        } else {
            holder.tvAmount.setText("- $" + transaction.getAmount());
            holder.tvAmount.setTextColor(Color.parseColor("#E74C3C"));
            holder.imgCategory.setImageResource(android.R.drawable.ic_menu_delete);
            holder.imgCategory.setColorFilter(Color.parseColor("#E74C3C"));
        }

        holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvNote, tvAmount;
        ImageView imgCategory;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
    public void sortByNewest() {
        transactions.sort((t1, t2) -> Long.compare(t2.getDate(), t1.getDate()));
        notifyDataSetChanged();
    }

    public void sortByOldest() {
        transactions.sort((t1, t2) -> Long.compare(t1.getDate(), t2.getDate()));
        notifyDataSetChanged();
    }
    public void filterByType(String type) {
        transactions.clear();

        if (type.equals("All")) {
            transactions.addAll(transactionsFull);
        } else {
            for (Transaction transaction : transactionsFull) {
                if (transaction.getType().equalsIgnoreCase(type)) {
                    transactions.add(transaction);
                }
            }
        }

        notifyDataSetChanged();
    }
}