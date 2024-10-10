package com.example.myapplication.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

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

public class ExcursionDetailActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewDescription;
    private Button buttonEdit;
    private Button buttonDelete;
    private int excursionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_detail);

        textViewTitle = findViewById(R.id.text_view_excursion_title);
        textViewDescription = findViewById(R.id.text_view_excursion_description);
        buttonEdit = findViewById(R.id.button_edit_excursion);
        buttonDelete = findViewById(R.id.button_delete_excursion);

        excursionId = getIntent().getIntExtra("EXCURSION_ID", -1);
        if (excursionId != -1) {
            loadExcursionDetails (excursionId);
        } else {
            Toast.makeText(this, "Invalid excursion ID", Toast.LENGTH_SHORT).show();

        }

        buttonEdit.setOnClickListener(v -> {
            ExcursionEditDialog.show(this, excursionId, updatedExcursion -> {
                // Update the UI or do anything else needed after updating the excursion
                loadExcursionDetails(excursionId); // Refresh the details
            });
        });


        buttonDelete.setOnClickListener(v -> deleteExcursion(excursionId));
    }

    private void showEditDialog(Excursion excursion) {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_excursion, null);

        EditText editTextTitle = dialogView.findViewById(R.id.edit_text_title);
        EditText editTextDescription = dialogView.findViewById(R.id.edit_text_date);

        editTextTitle.setText(excursion.exTitle);
        editTextDescription.setText(excursion.exDate); // Ensure you have a description field

        builder.setView(dialogView)
                .setTitle("Edit Excursion")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get updated values
                    String updatedTitle = editTextTitle.getText().toString();
                    String updatedDescription = editTextDescription.getText().toString();

                    // Update the excursion in the database
                    updateExcursion(excursion.id, updatedTitle, updatedDescription);
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }




    private void updateExcursion(int excursionId, String title, String description) {
        new UpdateExcursionTask().execute(new Excursion(excursionId, title, description));
    }

    private class UpdateExcursionTask extends AsyncTask<Excursion, Void, Void> {
        @Override
        protected Void doInBackground(Excursion... excursions) {
            AppDatabase db = AppDatabase.getInstance(ExcursionDetailActivity.this);
            db.excursionDao().update(excursions[0]); // Assuming you have an update method in your DAO
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ExcursionDetailActivity.this, "Excursion updated", Toast.LENGTH_SHORT).show();
            loadExcursionDetails(excursionId); // Refresh details
        }
    }












    private void loadExcursionDetails(int excursionId) {
        new LoadExcursionTask().execute(excursionId);
    }

    private class LoadExcursionTask extends AsyncTask<Integer, Void, Excursion> {
        @Override
        protected Excursion doInBackground(Integer... ids) {
            AppDatabase db = AppDatabase.getInstance(ExcursionDetailActivity.this);
            return db.excursionDao().getExcursionById(ids[0]);
        }

        @Override
        protected void onPostExecute(Excursion excursion) {
            if (excursion != null) {
                textViewTitle.setText(excursion.exTitle);
                textViewDescription.setText(excursion.exDate);
            } else {
                Toast.makeText(ExcursionDetailActivity.this, "Excursion not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteExcursion(int excursionId) {
        new DeleteExcursionTask().execute(excursionId);
    }

    private class DeleteExcursionTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... ids) {
            AppDatabase db = AppDatabase.getInstance(ExcursionDetailActivity.this);
            Excursion excursion = db.excursionDao().getExcursionById(ids[0]);
            if (excursion != null) {
                db.excursionDao().delete(excursion);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ExcursionDetailActivity.this, "Excursion deleted", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after deletion
        }
    }
}
