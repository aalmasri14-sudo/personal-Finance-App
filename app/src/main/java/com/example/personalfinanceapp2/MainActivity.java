package com.example.personalfinanceapp2;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.example.personalfinanceapp2.data.database.DatabaseClient;
import com.example.personalfinanceapp2.data.model.Transaction;
import com.example.personalfinanceapp2.ui.transaction.TransactionAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.AutoCompleteTextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnFilterAll, btnFilterIncome, btnFilterExpense;
    private FloatingActionButton btnAddTransaction;
    private RecyclerView recyclerTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private TextView tvBalance, tvIncome, tvExpense;
    private SearchView searchViewTransactions;
    private TextView tvEmptyState;
    private Button btnOpenReports;
    private Button btnSortNewest, btnSortOldest;
    private AutoCompleteTextView spinnerCategoryFilter;
    private Button btnOpenSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSortNewest = findViewById(R.id.btnSortNewest);
        btnSortOldest = findViewById(R.id.btnSortOldest);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnOpenReports = findViewById(R.id.btnOpenReports);
        btnOpenSettings = findViewById(R.id.btnOpenSettings);
        tvBalance = findViewById(R.id.tvBalance);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        searchViewTransactions = findViewById(R.id.searchViewTransactions);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        btnFilterIncome = findViewById(R.id.btnFilterIncome);
        btnFilterExpense = findViewById(R.id.btnFilterExpense);
        btnAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));
        btnOpenReports.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
            startActivity(intent);
        });
        btnOpenSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        transactionList = new ArrayList<>();

        transactionAdapter = new TransactionAdapter(transactionList, transaction -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            intent.putExtra("id", transaction.getId());
            intent.putExtra("amount", transaction.getAmount());
            intent.putExtra("type", transaction.getType());
            intent.putExtra("category", transaction.getCategory());
            intent.putExtra("date", transaction.getDate());
            intent.putExtra("note", transaction.getNote());
            startActivity(intent);
        });

        recyclerTransactions.setAdapter(transactionAdapter);

        loadTransactions();
        loadSummary();
        enableSwipeToDelete();
        setupSearch();
        setupFilters();
        setupSort();
        setupCategoryFilter();
    }
    private void setupCategoryFilter() {

        String[] categories = {
                "All",
                "Food",
                "Transport",
                "Shopping",
                "Bills",
                "Entertainment",
                "Health",
                "Education",
                "Groceries",
                "Travel",
                "Subscriptions",
                "Salary",
                "Freelance",
                "Investment",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );

        spinnerCategoryFilter.setAdapter(adapter);

        spinnerCategoryFilter.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = categories[position];

            if (selectedCategory.equals("All")) {
                transactionAdapter.filter("");
            } else {
                transactionAdapter.filter(selectedCategory);
            }
        });
    }
    private void setupSort() {
        btnSortNewest.setOnClickListener(v -> transactionAdapter.sortByNewest());
        btnSortOldest.setOnClickListener(v -> transactionAdapter.sortByOldest());
    }
    private void loadTransactions() {
        DatabaseClient.getInstance(this)
                .getAppDatabase()
                .transactionDao()
                .getAllTransactions()
                .observe(this, transactions -> {
                    transactionAdapter.updateData(transactions);

                    if (transactions == null || transactions.isEmpty()) {
                        recyclerTransactions.setVisibility(View.GONE);
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        recyclerTransactions.setVisibility(View.VISIBLE);
                        tvEmptyState.setVisibility(View.GONE);
                    }
                });
    }

    private void loadSummary() {
        DatabaseClient.getInstance(this)
                .getAppDatabase()
                .transactionDao()
                .getTotalIncome()
                .observe(this, income -> {
                    double totalIncome = income != null ? income : 0.0;
                    tvIncome.setText("Income: $" + totalIncome);

                    DatabaseClient.getInstance(this)
                            .getAppDatabase()
                            .transactionDao()
                            .getTotalExpense()
                            .observe(this, expense -> {
                                double totalExpense = expense != null ? expense : 0.0;
                                tvExpense.setText("Expense: $" + totalExpense);

                                double balance = totalIncome - totalExpense;
                                tvBalance.setText("$" + balance);
                            });
                });
    }

    private void setupSearch() {
        if (searchViewTransactions == null) return;

        searchViewTransactions.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                transactionAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionAdapter.filter(newText);
                return true;
            }
        });
    }
    private void setupFilters() {
        btnFilterAll.setOnClickListener(v -> transactionAdapter.filterByType("All"));
        btnFilterIncome.setOnClickListener(v -> transactionAdapter.filterByType("Income"));
        btnFilterExpense.setOnClickListener(v -> transactionAdapter.filterByType("Expense"));
    }
    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Transaction transaction = transactionList.get(position);

                        DatabaseClient.getInstance(MainActivity.this)
                                .getAppDatabase()
                                .transactionDao()
                                .delete(transaction);

                        Toast.makeText(MainActivity.this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                            RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState,
                                            boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;

                        Drawable deleteIcon = ContextCompat.getDrawable(
                                MainActivity.this,
                                android.R.drawable.ic_menu_delete
                        );

                        if (deleteIcon == null) {
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            return;
                        }

                        android.graphics.Paint paint = new android.graphics.Paint();
                        paint.setColor(Color.parseColor("#E74C3C"));

                        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                        if (dX > 0) {
                            c.drawRect(
                                    (float) itemView.getLeft(),
                                    (float) itemView.getTop(),
                                    (float) itemView.getLeft() + dX,
                                    (float) itemView.getBottom(),
                                    paint
                            );

                            int iconLeft = itemView.getLeft() + iconMargin;
                            int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();

                            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            deleteIcon.draw(c);

                        } else if (dX < 0) {
                            c.drawRect(
                                    (float) itemView.getRight() + dX,
                                    (float) itemView.getTop(),
                                    (float) itemView.getRight(),
                                    (float) itemView.getBottom(),
                                    paint
                            );

                            int iconRight = itemView.getRight() - iconMargin;
                            int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();

                            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            deleteIcon.draw(c);
                        }

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerTransactions);
    }
}