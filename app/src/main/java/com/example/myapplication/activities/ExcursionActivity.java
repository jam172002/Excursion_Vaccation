package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Excursion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExcursionActivity extends AppCompatActivity {

    private EditText editExcursionTitle;
    private EditText editExcursionDate;
    private int vacationId;
    private TextView tv_vaccation_title;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        editExcursionTitle = findViewById(R.id.edit_excursion_title);
        editExcursionDate = findViewById(R.id.edit_excursion_date);
        Button buttonSaveExcursion = findViewById(R.id.button_save_excursion);
        tv_vaccation_title = findViewById(R.id.tv_vacccation_title);

        // Get vacation data from the intent
        vacationId = getIntent().getIntExtra("VACATION_ID", -1);
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String startDate = getIntent().getStringExtra("START_DATE");
        String endDate = getIntent().getStringExtra("END_DATE");

        if (vacationTitle != null) {
            tv_vaccation_title.setText(vacationTitle);
        } else {
            tv_vaccation_title.setText("No Title Provided");
        }

        // Handle save button click
        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcursion(startDate, endDate); // Pass startDate and endDate to the saveExcursion method
            }
        });
    }

    private void saveExcursion(String startDate, String endDate) {
        String title = editExcursionTitle.getText().toString();
        String date = editExcursionDate.getText().toString();

        // Check if all fields are filled
        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date format
        if (!isValidDateFormat(date)) {
            Toast.makeText(this, "Invalid excursion date format. Please use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate if the excursion date falls within the vacation range
        if (startDate != null && endDate != null && !isDateWithinVacationRange(date, startDate, endDate)) {
            Toast.makeText(this, "Excursion date must be between vacation start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the excursion if validation passes
        Excursion excursion = new Excursion(vacationId, title, date);
        new SaveExcursionTask().execute(excursion);
    }

    // Helper method to validate date format
    private boolean isValidDateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Helper method to check if the excursion date is within the vacation date range
    private boolean isDateWithinVacationRange(String excursionDate, String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date excursion = sdf.parse(excursionDate);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            return excursion != null && start != null && end != null &&
                    !excursion.before(start) && !excursion.after(end);
        } catch (ParseException e) {
            return false;
        }
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
