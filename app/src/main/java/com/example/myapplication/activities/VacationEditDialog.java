package com.example.myapplication.activities;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Vacation;

import java.util.concurrent.Executors;

public class VacationEditDialog {

    public static void show(Context context, int vacationId, OnVacationUpdatedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_edit_vacation, null);

        EditText editTextTitle = view.findViewById(R.id.edit_text_title);
        EditText editTextHotel = view.findViewById(R.id.edit_text_hotel);
        EditText editTextStartDate = view.findViewById(R.id.edit_text_start_date);
        EditText editTextEndDate = view.findViewById(R.id.edit_text_end_date);

        // Load the existing vacation details in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Vacation vacation = db.vacationDao().getVacationById(vacationId);

            // Update the UI on the main thread
            if (vacation != null) {
                ((Activity) context).runOnUiThread(() -> {
                    editTextTitle.setText(vacation.title);
                    editTextHotel.setText(vacation.hotel);
                    editTextStartDate.setText(vacation.startDate);
                    editTextEndDate.setText(vacation.endDate);
                });
            }
        });

        builder.setView(view)
                .setTitle("Edit Vacation")
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = editTextTitle.getText().toString();
                    String hotel = editTextHotel.getText().toString();
                    String startDate = editTextStartDate.getText().toString();
                    String endDate = editTextEndDate.getText().toString();

                    // Save the updated vacation details on a background thread
                    Executors.newSingleThreadExecutor().execute(() -> {
                        Vacation updatedVacation = new Vacation(vacationId, title, hotel, startDate, endDate);
                        AppDatabase.getInstance(context).vacationDao().update(updatedVacation);

                        // Notify the listener on the main thread
                        ((Activity) context).runOnUiThread(() -> {
                            listener.onVacationUpdated(updatedVacation);
                        });
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    public interface OnVacationUpdatedListener {
        void onVacationUpdated(Vacation vacation);
    }
}

