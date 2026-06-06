package com.example.individualproject_alianatasyasumin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ListView listView;
    DatabaseHelper dbHelper;
    List<Bill> billList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Billing History");
        }

        listView = findViewById(R.id.listViewBills);
        dbHelper = new DatabaseHelper(this);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("BILL_ID", billList.get(position).getId());
            startActivity(intent);
        });
    }

    // Refresh data whenever user return to this page
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Load data to display month and final cost
    private void loadData() {
        billList = dbHelper.getAllBills();
        ArrayList<String> displayList = new ArrayList<>();
        for (Bill b : billList) {
            displayList.add(b.getMonth() + " - RM " + String.format("%.2f", b.getFinalCost()));
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}