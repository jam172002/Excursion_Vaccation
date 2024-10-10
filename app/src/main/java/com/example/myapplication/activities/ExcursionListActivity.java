package com.example.myapplication.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Excursion;
import com.example.myapplication.db.Vacation;

import java.util.List;

public class ExcursionListActivity extends AppCompatActivity {

    private TextView textViewVacationTitle;
    private TextView textViewHotel;
    private TextView textViewDates;
   private Button buttonEdit;
   private Button buttonDelete;
    private ListView listViewExcursions;
    private int vacationId;
    private ArrayAdapter<Excursion> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        textViewVacationTitle = findViewById(R.id.text_view_vacation_title);
        textViewHotel = findViewById(R.id.text_view_hotel);
        textViewDates = findViewById(R.id.text_view_dates);
       listViewExcursions = findViewById(R.id.list_view_excursions);

       buttonEdit = findViewById(R.id.button_edit);
         buttonDelete = findViewById(R.id.button_delete);

        listViewExcursions.setOnItemClickListener((parent, view, position, id) -> {
            Excursion selectedExcursion = adapter.getItem(position);
            if (selectedExcursion != null) {
                Log.d("ExcursionListActivity", "Selected Excursion ID: " + selectedExcursion.id);
                Intent intent = new Intent(ExcursionListActivity.this, ExcursionDetailActivity.class);
                intent.putExtra("EXCURSION_ID", selectedExcursion.id);
                startActivity(intent);
            } else {
                Toast.makeText(ExcursionListActivity.this, "Excursion not found", Toast.LENGTH_SHORT).show();
            }
        });




        vacationId = getIntent().getIntExtra("VACATION_ID", -1);
        if (vacationId != -1) {
            loadVacationDetails(vacationId);
            loadExcursions(vacationId);
        } else {
            Toast.makeText(this, "Invalid vacation ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }

        buttonEdit.setOnClickListener(v -> {
            VacationEditDialog.show(this, vacationId, this::updateVacation);
        });

        buttonDelete.setOnClickListener(v -> deleteVacation(vacationId));
    }


    private void loadVacationDetails(int vacationId) {
        new LoadVacationTask().execute(vacationId);
    }

    private void loadExcursions(int vacationId) {
        new LoadExcursionsTask().execute(vacationId);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadVacationTask extends AsyncTask<Integer, Void, Vacation> {
        @Override
        protected Vacation doInBackground(Integer... ids) {
            AppDatabase db = AppDatabase.getInstance(ExcursionListActivity.this);
            return db.vacationDao().getVacationById(ids[0]);
        }

        @Override
        protected void onPostExecute(Vacation vacation) {
            if (vacation != null) {
                textViewVacationTitle.setText(vacation.title);
                textViewHotel.setText(vacation.hotel);
                textViewDates.setText(vacation.startDate + " to " + vacation.endDate);
            } else {
                Toast.makeText(ExcursionListActivity.this, "Vacation not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExcursions(vacationId);
    }

    private class LoadExcursionsTask extends AsyncTask<Integer, Void, List<Excursion>> {
        @Override
        protected List<Excursion> doInBackground(Integer... ids) {
            AppDatabase db = AppDatabase.getInstance(ExcursionListActivity.this);
            return db.excursionDao().getExcursionsByVacationId(0);
        }

        @Override
        protected void onPostExecute(List<Excursion> excursions) {
            if (excursions != null && !excursions.isEmpty()) {
                adapter = new ArrayAdapter<>(ExcursionListActivity.this,
                        android.R.layout.simple_list_item_1, excursions);
                listViewExcursions.setAdapter(adapter);
            } else {
                Toast.makeText(ExcursionListActivity.this, "No excursions found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateVacation(Vacation updatedVacation) {
        new UpdateVacationTask().execute(updatedVacation);
    }

    private class UpdateVacationTask extends AsyncTask<Vacation, Void, Void> {
        @Override
        protected Void doInBackground(Vacation... vacations) {
            AppDatabase db = AppDatabase.getInstance(ExcursionListActivity.this);
            db.vacationDao().update(vacations[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ExcursionListActivity.this, "Vacation updated", Toast.LENGTH_SHORT).show();
            loadVacationDetails(vacationId); // Refresh the details
        }
    }

    private void deleteVacation(int vacationId) {
        new DeleteVacationTask().execute(vacationId);
    }

    private class DeleteVacationTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... ids) {
            AppDatabase db = AppDatabase.getInstance(ExcursionListActivity.this);
            Vacation vacation = db.vacationDao().getVacationById(ids[0]);
            if (vacation != null) {
                db.vacationDao().delete(vacation);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ExcursionListActivity.this, "Vacation deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
