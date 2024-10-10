package com.example.myapplication.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Vacation;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
public class VacationListActivity extends AppCompatActivity {

    private ListView listViewVacations;
    private Button buttonAddVacation;
    private ArrayAdapter<Vacation> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        listViewVacations = findViewById(R.id.list_view_vacations);
        buttonAddVacation = findViewById(R.id.button_add_vacation);



        listViewVacations.setOnItemClickListener((parent, view, position, id) -> {
            Vacation selectedVacation = adapter.getItem(position);
            if (selectedVacation != null) {
                Intent intent = new Intent(VacationListActivity.this, ExcursionListActivity.class);
                intent.putExtra("VACATION_ID", selectedVacation.id); // Pass the vacation ID
                startActivity(intent);
            }
        });




        buttonAddVacation.setOnClickListener(v -> {

            startActivity(new Intent(VacationListActivity.this,VacationDetailActivity.class));

        });

        loadVacations();
    }

    private void loadVacations() {
        new LoadVacationsTask().execute();
    }

    private class LoadVacationsTask extends AsyncTask<Void, Void, List<Vacation>> {
        @Override
        protected List<Vacation> doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(VacationListActivity.this);
            return db.vacationDao().getAllVacations();
        }

        @Override
        protected void onPostExecute(List<Vacation> vacations) {
            if (vacations != null && !vacations.isEmpty()) {
                adapter = new ArrayAdapter<>(VacationListActivity.this,
                        android.R.layout.simple_list_item_1, vacations);
                listViewVacations.setAdapter(adapter);
            } else {
                Toast.makeText(VacationListActivity.this, "No vacations found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}