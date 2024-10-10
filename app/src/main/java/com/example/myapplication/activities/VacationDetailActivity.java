package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class VacationDetailActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editHotel;
    private EditText editStartDate;
    private EditText editEndDate;
    private int currentVacationId = -1;  // Initialize as -1 to check if vacation is saved
    private Button addExcursion;
    private Button setCustomAlert;
    private Button shareVacationDetails;
    Button buttonSaveVacation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_detail);

        editTitle = findViewById(R.id.edit_title);
        editHotel = findViewById(R.id.edit_hotel);
        editStartDate = findViewById(R.id.edit_start_date);
        editEndDate = findViewById(R.id.edit_end_date);
        setCustomAlert = findViewById(R.id.button_set_alert);
        shareVacationDetails = findViewById(R.id.button_share_details);

         buttonSaveVacation = findViewById(R.id.button_save_vacation);
        addExcursion = findViewById(R.id.button_add_excursion);

        // Initially disable the "Add Excursion" button
        addExcursion.setEnabled(false);

        addExcursion.setOnClickListener(v -> {
            if (currentVacationId != -1) {
                Intent intent = new Intent(VacationDetailActivity.this, ExcursionActivity.class);

                intent.putExtra("VACATION_TITLE", editTitle.getText().toString());
                intent.putExtra("VACATION_ID", currentVacationId); // Pass the current vacation ID
                intent.putExtra("START_DATE", editStartDate.getText().toString()); // Pass start date
                intent.putExtra("END_DATE", editEndDate.getText().toString()); // Pass end date
                startActivity(intent);
            }
        });

        buttonSaveVacation.setOnClickListener(v -> saveVacation());
    }

    private void saveVacation() {
        String title = editTitle.getText().toString();
        String hotel = editHotel.getText().toString();
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();

        if (title.isEmpty() || hotel.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date format
        if (!isValidDateFormat(startDate)) {
            Toast.makeText(this, "Invalid start date format. Please use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDateFormat(endDate)) {
            Toast.makeText(this, "Invalid end date format. Please use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate that start date is before end date
        if (!isStartDateBeforeEndDate(startDate, endDate)) {
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return;
        }

        Vacation vacation = new Vacation(title, hotel, startDate, endDate);

        // Save vacation in the background
        new SaveVacationTask().execute(vacation);
    }

    private boolean isValidDateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false); // This makes sure the date is strictly parsed
        try {
            sdf.parse(date); // Parse the date to check format
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isStartDateBeforeEndDate(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return start != null && end != null && start.before(end);
        } catch (ParseException e) {
            return false; // If parsing fails, return false
        }
    }

    private class SaveVacationTask extends AsyncTask<Vacation, Void, Long> {
        @Override
        protected Long doInBackground(Vacation... vacations) {
            AppDatabase db = AppDatabase.getInstance(VacationDetailActivity.this);
            return db.vacationDao().insert(vacations[0]); // Insert returns the auto-generated ID
        }

        @Override
        protected void onPostExecute(Long vacationId) {
            if (vacationId != null) {
                currentVacationId = vacationId.intValue(); // Set the current vacation ID

                editTitle.setEnabled(false);
                editHotel.setEnabled(false);
                editStartDate.setEnabled(false);
                editEndDate.setEnabled(false);
                buttonSaveVacation.setEnabled(false);
                Toast.makeText(VacationDetailActivity.this, "Vacation saved", Toast.LENGTH_SHORT).show();

                // Enable the "Add Excursion" button after saving
                addExcursion.setEnabled(true);
            } else {
                Toast.makeText(VacationDetailActivity.this, "Failed to save vacation", Toast.LENGTH_SHORT).show();
            }

            //clearFields(); // Clear input fields after saving
        }
    }

    private void clearFields() {
        editTitle.setText("");
        editHotel.setText("");
        editStartDate.setText("");
        editEndDate.setText("");
    }
}
