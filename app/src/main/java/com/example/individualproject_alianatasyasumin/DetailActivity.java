package com.example.individualproject_alianatasyasumin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    int billId;
    EditText etUnits, etRebate;
    TextView tvMonth, tvTotal, tvFinal;
    double calculatedTotal = 0, calculatedFinal = 0;
    String originalUnits, originalRebate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Update Record");
        }

        dbHelper = new DatabaseHelper(this);
        billId = getIntent().getIntExtra("BILL_ID", -1);

        tvMonth = findViewById(R.id.tvDetailMonth);
        etUnits = findViewById(R.id.etDetailUnits);
        etRebate = findViewById(R.id.etDetailRebate);
        tvTotal = findViewById(R.id.tvDetailTotal);
        tvFinal = findViewById(R.id.tvDetailFinal);

        loadDetails();

        // Add Auto-Calculation listeners
        TextWatcher autoCalcWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runLiveCalculation(); // Recalculate every time text changes
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etUnits.addTextChangedListener(autoCalcWatcher);
        etRebate.addTextChangedListener(autoCalcWatcher);

        findViewById(R.id.btnUpdate).setOnClickListener(v -> saveUpdateToDb());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());

    }

    // Display billing details
    private void loadDetails() {
        for (Bill b : dbHelper.getAllBills()) {
            if (b.getId() == billId) {
                tvMonth.setText("Month: " + b.getMonth());
                originalUnits = String.valueOf(b.getUnits());
                originalRebate = String.valueOf(b.getRebate());
                etUnits.setText(originalUnits);
                etRebate.setText(originalRebate);
                runLiveCalculation();
                break;
            }
        }
    }

    // Recalculate when changes made
    private void runLiveCalculation() {
        try {
            String uStr = etUnits.getText().toString();
            String rStr = etRebate.getText().toString();

            if (!uStr.isEmpty() && !rStr.isEmpty()) {
                double units = Double.parseDouble(uStr);
                double rebatePercent = Double.parseDouble(rStr);

                // Re-use your Tiered Logic
                if (units <= 200) calculatedTotal = units * 0.218;
                else if (units <= 300) calculatedTotal = (200 * 0.218) + ((units - 200) * 0.334);
                else if (units <= 600) calculatedTotal = (200 * 0.218) + (100 * 0.334) + ((units - 300) * 0.516);
                else calculatedTotal = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((units - 600) * 0.546);

                calculatedFinal = calculatedTotal - (calculatedTotal * (rebatePercent / 100));

                tvTotal.setText(String.format("Total Charges: RM %.2f", calculatedTotal));
                tvFinal.setText(String.format("Final Cost: RM %.2f", calculatedFinal));
            }
        } catch (Exception e) {
            // Handle parsing errors silently during typing
        }
    }

    // Update to database
    private void saveUpdateToDb() {
        String uStr = etUnits.getText().toString().trim();
        String rStr = etRebate.getText().toString().trim();

        // Check for empty fields
        if (uStr.isEmpty()) {
            etUnits.setError("Please enter usage units");
            return;
        }
        if (rStr.isEmpty()) {
            etRebate.setError("Please enter rebate percentage");
            return;
        }

        try {
            double units = Double.parseDouble(uStr);
            double rebate = Double.parseDouble(rStr);

            // Validate Units (1 - 1000)
            if (units < 1 || units > 1000) {
                etUnits.setError("Usage must be between 1 and 1000 kWh");
                return;
            }

            // Validate Rebate (0 - 5)
            if (rebate < 0 || rebate > 5) {
                etRebate.setError("Rebate must be between 0% and 5%");
                return;
            }

            // If all validations pass, update the DB
            int result = dbHelper.updateBill(billId, units, rebate, calculatedTotal, calculatedFinal);

            if (result > 0) {
                Toast.makeText(this, "Record Updated Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update Failed: Record not found", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input. Please enter numbers only.", Toast.LENGTH_SHORT).show();
        }
    }

    // Confirmation to delete record
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Delete this record?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteBill(billId);
                    finish();
                }).setNegativeButton("No", null).show();
    }

    // Confirmation to discard changes
    private void handleBackAction() {
        if (!etUnits.getText().toString().equals(originalUnits) || !etRebate.getText().toString().equals(originalRebate)) {
            new AlertDialog.Builder(this).setTitle("Discard Changes?")
                    .setMessage("Unsaved progress will be lost.").setPositiveButton("Discard", (d, w) -> finish())
                    .setNegativeButton("Cancel", null).show();
        } else { finish(); }
    }

    @Override
    public boolean onSupportNavigateUp() {
        handleBackAction();
        return true;
    }

    @Override
    public void onBackPressed() {
        handleBackAction();
    }
}