package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Vacation;

public class VacationDetailActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editHotel;
    private EditText editStartDate;
    private EditText editEndDate;
    private int currentVacationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_detail);

        editTitle = findViewById(R.id.edit_title);
        editHotel = findViewById(R.id.edit_hotel);
        editStartDate = findViewById(R.id.edit_start_date);
        editEndDate = findViewById(R.id.edit_end_date);

        Button buttonSaveVacation = findViewById(R.id.button_save_vacation);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button addExcursion=findViewById(R.id.button_add_excursion);
        addExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(VacationDetailActivity.this, ExcursionActivity.class);
                intent.putExtra("VACATION_ID", currentVacationId); // Pass the current vacation ID
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

        Vacation vacation = new Vacation(title,hotel,startDate,endDate);
        vacation.title=title;
        vacation.hotel=hotel;
        vacation.startDate=startDate;
        vacation.endDate=endDate;

        new SaveVacationTask().execute(vacation);
    }

    private class SaveVacationTask extends AsyncTask<Vacation, Void, Void> {
        @Override
        protected Void doInBackground(Vacation... vacations) {
            AppDatabase db = AppDatabase.getInstance(VacationDetailActivity.this);
            db.vacationDao().insert(vacations[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(VacationDetailActivity.this, "Vacation saved", Toast.LENGTH_SHORT).show();
            clearFields();   // Clear input fields after saving
        }
    }

    private void clearFields() {
        editTitle.setText("");
        editHotel.setText("");
        editStartDate.setText("");
        editEndDate.setText("");
    }
}
