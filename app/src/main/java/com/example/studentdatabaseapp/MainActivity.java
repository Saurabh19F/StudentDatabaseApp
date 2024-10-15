package com.example.studentdatabaseapp;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    StudentDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new StudentDatabaseHelper(this);

        Button addButton = findViewById(R.id.addButton);
        Button updateButton = findViewById(R.id.updateButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button retrieveButton = findViewById(R.id.retrieveButton);

        addButton.setOnClickListener(v -> showAddStudentDialog());
        updateButton.setOnClickListener(v -> showUpdateStudentDialog());
        deleteButton.setOnClickListener(v -> showDeleteStudentDialog());
        retrieveButton.setOnClickListener(v -> showAllStudents());

        // Button scale animation
        setButtonTouchAnimation(addButton);
        setButtonTouchAnimation(updateButton);
        setButtonTouchAnimation(deleteButton);
        setButtonTouchAnimation(retrieveButton);
    }

    private void setButtonTouchAnimation(Button addButton) {
        // Use the 'addButton' that is passed as a parameter, not a null View
        addButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }


    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        EditText nameInput = view.findViewById(R.id.nameInput);
        EditText emailInput = view.findViewById(R.id.emailInput);
        EditText phoneInput = view.findViewById(R.id.phoneInput);
        EditText addressInput = view.findViewById(R.id.addressInput);
        Button submitButton = view.findViewById(R.id.submitButton);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String address = addressInput.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
                if (dbHelper.addStudent(name, email, phone, address)) {
                    Toast.makeText(this, "Student Added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to Add Student", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showUpdateStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_update_student, null);
        EditText idInput = view.findViewById(R.id.idInput);
        Button fetchButton = view.findViewById(R.id.fetchButton);
        EditText nameInput = view.findViewById(R.id.nameInput);
        EditText emailInput = view.findViewById(R.id.emailInput);
        EditText phoneInput = view.findViewById(R.id.phoneInput);
        EditText addressInput = view.findViewById(R.id.addressInput);
        Button updateButton = view.findViewById(R.id.updateButton);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        fetchButton.setOnClickListener(v -> {
            String idStr = idInput.getText().toString();
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                Cursor cursor = dbHelper.getStudent(id);
                if (cursor.moveToFirst()) {
                    nameInput.setText(cursor.getString(1));
                    emailInput.setText(cursor.getString(2));
                    phoneInput.setText(cursor.getString(3));
                    addressInput.setText(cursor.getString(4));
                } else {
                    Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter a valid ID", Toast.LENGTH_SHORT).show();
            }
        });

        updateButton.setOnClickListener(v -> {
            String idStr = idInput.getText().toString();
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String address = addressInput.getText().toString();

            if (!idStr.isEmpty() && !name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
                int id = Integer.parseInt(idStr);
                if (dbHelper.updateStudent(id, name, email, phone, address)) {
                    Toast.makeText(this, "Student Updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showDeleteStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_student, null);
        EditText idInput = view.findViewById(R.id.idInput);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        deleteButton.setOnClickListener(v -> {
            String idStr = idInput.getText().toString();

            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                if (dbHelper.deleteStudent(id)) {
                    Toast.makeText(this, "Student Deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Student Not Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter a valid ID", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showAllStudents() {
        Cursor cursor = dbHelper.getAllStudents();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            stringBuilder.append("ID: ").append(cursor.getInt(0))
                    .append(", Name: ").append(cursor.getString(1))
                    .append(", Email: ").append(cursor.getString(2))
                    .append(", Phone: ").append(cursor.getString(3))
                    .append(", Address: ").append(cursor.getString(4))
                    .append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student List");
        builder.setMessage(stringBuilder.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
