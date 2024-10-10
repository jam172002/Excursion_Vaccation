package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.Excursion;

import java.util.concurrent.Executors;

public class ExcursionEditDialog {



    public static void show(Context context, int excursionId, OnExcursionUpdatedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_edit_excursion, null);

        EditText editTextTitle = view.findViewById(R.id.edit_text_title);
        EditText editTextDate = view.findViewById(R.id.edit_text_date);

       
        // Load the existing excursion details in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Excursion excursion = db.excursionDao().getExcursionById(excursionId);

            // Update the UI on the main thread
            if (excursion != null) {
                ((Activity) context).runOnUiThread(() -> {
                    editTextTitle.setText(excursion.exTitle);
                    editTextDate.setText(excursion.exDate);
                });
            }
        });

        builder.setView(view)
                .setTitle("Edit Excursion")
                .setPositiveButton("Save", (dialog, which) -> {


                    String title = editTextTitle.getText().toString();
                    String date = editTextDate.getText().toString();

                    // Save the updated excursion details on a background thread
                    Executors.newSingleThreadExecutor().execute(() -> {
                        Excursion updatedExcursion = new Excursion(excursionId, title, date);
                        AppDatabase.getInstance(context).excursionDao().update(updatedExcursion);

                        // Notify the listener on the main thread
                        ((Activity) context).runOnUiThread(() -> {
                            listener.onExcursionUpdated(updatedExcursion);
                        });
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    public interface OnExcursionUpdatedListener {
        void onExcursionUpdated(Excursion excursion);
    }
}
