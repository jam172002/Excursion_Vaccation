package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Excursion;

public class ExcursionActivity extends AppCompatActivity {

    private EditText editExcursionTitle;
    private EditText editExcursionDate;
    private int vacationId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        editExcursionTitle = findViewById(R.id.edit_excursion_title);
        editExcursionDate = findViewById(R.id.edit_excursion_date);
        Button buttonSaveExcursion = findViewById(R.id.button_save_excursion);

        // Get the vacation ID from the intent
        vacationId = getIntent().getIntExtra("VACATION_ID", -1);

        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcursion();
            }
        });
    }

    private void saveExcursion() {
        String title = editExcursionTitle.getText().toString();
        String date = editExcursionDate.getText().toString();

        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Excursion excursion = new Excursion(vacationId,title,date);
        excursion.exTitle = title;
        excursion.exDate = date;
        excursion.vacationId = vacationId;

        new SaveExcursionTask().execute(excursion);
    }

    private class SaveExcursionTask extends AsyncTask<Excursion, Void, Void> {
        @Override
        protected Void doInBackground(Excursion... excursions) {
            AppDatabase db = AppDatabase.getInstance(ExcursionActivity.this);
            db.excursionDao().insert(excursions[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ExcursionActivity.this, "Excursion added", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous one
        }
    }
}
