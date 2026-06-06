package com.example.individualproject_alianatasyasumin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUnits;
    TextView tvTotalCharges, tvFinalCost, tvRebateLabel;
    Spinner spinnerMonth;
    SeekBar seekBarRebate;
    DatabaseHelper dbHelper;
    double currentRebate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Rubric Requirement: Custom Title Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Electricity Bill Calculator");
        }

        etUnits = findViewById(R.id.etUnits);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        tvRebateLabel = findViewById(R.id.tvRebateLabel);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        dbHelper = new DatabaseHelper(this);

        // SeekBar Logic
        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRebateLabel.setText("Rebate Percentage: " + progress + "%");
                currentRebate = progress;
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        findViewById(R.id.btnCalculate).setOnClickListener(v -> calculateAndSave());
        findViewById(R.id.btnAbout).setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        findViewById(R.id.btnGoHistory).setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
    }

    // Calculate the total charges and final cost
    private void calculateAndSave() {
        String unitStr = etUnits.getText().toString();
        if (unitStr.isEmpty()) {
            etUnits.setError("Please enter units (1-1000)");
            return;
        }

        double units = Double.parseDouble(unitStr);
        if (units < 1 || units > 1000) {
            etUnits.setError("Usage must be between 1 and 1000");
            return;
        }

        double totalCharges = 0;
        if (units <= 200) totalCharges = units * 0.218;
        else if (units <= 300) totalCharges = (200 * 0.218) + ((units - 200) * 0.334);
        else if (units <= 600) totalCharges = (200 * 0.218) + (100 * 0.334) + ((units - 300) * 0.516);
        else totalCharges = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((units - 600) * 0.546);

        double finalCost = totalCharges - (totalCharges * (currentRebate / 100));

        tvTotalCharges.setText(String.format("Total Charges: RM %.2f", totalCharges));
        tvFinalCost.setText(String.format("Final Cost: RM %.2f", finalCost));

        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        dbHelper.addBill(new Bill(0, selectedMonth, units, currentRebate, totalCharges, finalCost));

        Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
    }
}