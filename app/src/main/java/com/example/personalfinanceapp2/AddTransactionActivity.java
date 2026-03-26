package com.example.personalfinanceapp2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalfinanceapp2.data.database.DatabaseClient;
import com.example.personalfinanceapp2.data.model.Transaction;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity {

    private TextInputEditText etAmount, etNote;
    private AutoCompleteTextView spinnerCategory;
    private RadioButton rbIncome, rbExpense;
    private Button btnSaveTransaction, btnSelectDate;
    private TextView tvAddTitle, tvPreviewAmount, tvPreviewCategory, tvPreviewType;
    private MaterialCardView cardIncome, cardExpense;

    private int transactionId = -1;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        btnSaveTransaction = findViewById(R.id.btnSaveTransaction);
        btnSelectDate = findViewById(R.id.btnSelectDate);

        tvAddTitle = findViewById(R.id.tvAddTitle);
        tvPreviewAmount = findViewById(R.id.tvPreviewAmount);
        tvPreviewCategory = findViewById(R.id.tvPreviewCategory);
        tvPreviewType = findViewById(R.id.tvPreviewType);

        cardIncome = findViewById(R.id.cardIncome);
        cardExpense = findViewById(R.id.cardExpense);

        selectedDate = System.currentTimeMillis();

        setupCategorySpinner();
        setupListeners();
        loadTransactionDataIfEditing();

        btnSaveTransaction.setOnClickListener(v -> saveTransaction());
        btnSelectDate.setOnClickListener(v -> openDatePicker());
    }

    private void setupCategorySpinner() {
        String[] categories = {
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

        spinnerCategory.setAdapter(adapter);
    }

    private void setupListeners() {
        rbExpense.setChecked(true);
        updateTypeSelectionUI();

        rbIncome.setOnClickListener(v -> {
            rbIncome.setChecked(true);
            rbExpense.setChecked(false);
            updateTypeSelectionUI();
        });

        rbExpense.setOnClickListener(v -> {
            rbExpense.setChecked(true);
            rbIncome.setChecked(false);
            updateTypeSelectionUI();
        });

        spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            String category = spinnerCategory.getText().toString().trim();
            if (!TextUtils.isEmpty(category)) {
                tvPreviewCategory.setText(category);
            }
        });

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString().trim();
                if (value.isEmpty()) {
                    tvPreviewAmount.setText("$0.00");
                } else {
                    tvPreviewAmount.setText("$" + value);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateTypeSelectionUI() {
        if (rbIncome.isChecked()) {
            tvPreviewType.setText("Income");
            tvPreviewType.setTextColor(android.graphics.Color.parseColor("#86EFAC"));

            cardIncome.setStrokeColor(android.graphics.Color.parseColor("#10B981"));
            cardIncome.setCardBackgroundColor(android.graphics.Color.parseColor("#D1FAE5"));

            cardExpense.setStrokeColor(android.graphics.Color.parseColor("#FCA5A5"));
            cardExpense.setCardBackgroundColor(android.graphics.Color.parseColor("#FEF2F2"));
        } else {
            tvPreviewType.setText("Expense");
            tvPreviewType.setTextColor(android.graphics.Color.parseColor("#FCA5A5"));

            cardExpense.setStrokeColor(android.graphics.Color.parseColor("#EF4444"));
            cardExpense.setCardBackgroundColor(android.graphics.Color.parseColor("#FEE2E2"));

            cardIncome.setStrokeColor(android.graphics.Color.parseColor("#A7F3D0"));
            cardIncome.setCardBackgroundColor(android.graphics.Color.parseColor("#ECFDF5"));
        }
    }

    private void loadTransactionDataIfEditing() {
        if (getIntent().hasExtra("id")) {
            transactionId = getIntent().getIntExtra("id", -1);
            double amount = getIntent().getDoubleExtra("amount", 0.0);
            String type = getIntent().getStringExtra("type");
            String category = getIntent().getStringExtra("category");
            selectedDate = getIntent().getLongExtra("date", System.currentTimeMillis());
            String note = getIntent().getStringExtra("note");

            tvAddTitle.setText("Edit Transaction");
            btnSaveTransaction.setText("Update Transaction");

            etAmount.setText(String.valueOf(amount));
            etNote.setText(note);
            spinnerCategory.setText(category, false);
            tvPreviewCategory.setText(category);
            tvPreviewAmount.setText("$" + amount);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selectedDate);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            btnSelectDate.setText(day + "/" + month + "/" + year);

            if ("Income".equals(type)) {
                rbIncome.setChecked(true);
                rbExpense.setChecked(false);
            } else {
                rbExpense.setChecked(true);
                rbIncome.setChecked(false);
            }

            updateTypeSelectionUI();
        }
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedDate = selected.getTimeInMillis();
                    btnSelectDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveTransaction() {
        String amountText = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String category = spinnerCategory.getText() != null ? spinnerCategory.getText().toString().trim() : "";
        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";

        if (TextUtils.isEmpty(amountText)) {
            etAmount.setError("Enter amount");
            etAmount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
            etAmount.requestFocus();
            return;
        }

        if (amount <= 0) {
            etAmount.setError("Amount must be greater than 0");
            etAmount.requestFocus();
            return;
        }

        String type = rbIncome.isChecked() ? "Income" : "Expense";

        if (transactionId == -1) {
            Transaction transaction = new Transaction(
                    amount,
                    type,
                    category,
                    selectedDate,
                    note
            );

            DatabaseClient.getInstance(this)
                    .getAppDatabase()
                    .transactionDao()
                    .insert(transaction);

            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
        } else {
            Transaction transaction = new Transaction(
                    transactionId,
                    amount,
                    type,
                    category,
                    selectedDate,
                    note
            );

            DatabaseClient.getInstance(this)
                    .getAppDatabase()
                    .transactionDao()
                    .update(transaction);

            Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}