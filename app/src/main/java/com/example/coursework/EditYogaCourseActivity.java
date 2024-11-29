package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditYogaCourseActivity extends AppCompatActivity {

    private YogaCourseDBHelper dbHelper;
    private int yogaCourseId;
    private EditText editClassType, editTeacher, editDayOfWeek, editTime, editCapacity, editPrice, editDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_yoga_course);

        dbHelper = new YogaCourseDBHelper(this);

        yogaCourseId = getIntent().getIntExtra("yogaCourseId", -1);
        if (yogaCourseId == -1) {
            Toast.makeText(this, "Invalid course ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editClassType = findViewById(R.id.editClassType);
        editTeacher = findViewById(R.id.editTeacher);
        editDayOfWeek = findViewById(R.id.editDayOfWeek);
        editTime = findViewById(R.id.editTime);
        editCapacity = findViewById(R.id.editCapacity);
        editPrice = findViewById(R.id.editPrice);
        editDescription = findViewById(R.id.editDescription);

        YogaCourse yogaCourse = dbHelper.getYogaCourseById(yogaCourseId);
        if (yogaCourse == null) {
            Toast.makeText(this, "Course not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Pre-fill the fields with the current course details
        editClassType.setText(yogaCourse.getClassType());
        editTeacher.setText(yogaCourse.getTeacher());
        editDayOfWeek.setText(yogaCourse.getDayOfWeek());
        editTime.setText(yogaCourse.getTime());
        editCapacity.setText(String.valueOf(yogaCourse.getCapacity()));
        editPrice.setText(String.valueOf(yogaCourse.getPrice()));
        editDescription.setText(yogaCourse.getDescription());

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveYogaCourse());

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveYogaCourse() {
        String classType = editClassType.getText().toString().trim();
        String teacher = editTeacher.getText().toString().trim();
        String dayOfWeek = editDayOfWeek.getText().toString().trim();
        String time = editTime.getText().toString().trim();
        String capacityStr = editCapacity.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(classType) || TextUtils.isEmpty(teacher) || TextUtils.isEmpty(dayOfWeek) ||
                TextUtils.isEmpty(time) || TextUtils.isEmpty(capacityStr) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse capacity and price to numeric values
        int capacity;
        double price;
        try {
            capacity = Integer.parseInt(capacityStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format for capacity or price!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the updated course object
        YogaCourse updatedCourse = new YogaCourse(yogaCourseId, dayOfWeek, time, capacity, 60, price, classType, description, teacher, "test", "test");
        boolean isUpdated = dbHelper.updateYogaCourse(updatedCourse);

        if (isUpdated) {
            Toast.makeText(this, "Course updated successfully!", Toast.LENGTH_SHORT).show();

            // Notify MainActivity to reload data by returning RESULT_OK
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);  // Pass the result to MainActivity

            // Clear the activity stack and navigate directly to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // This clears the activity stack
            startActivity(intent); // Start MainActivity directly
        } else {
            Toast.makeText(this, "Failed to update course!", Toast.LENGTH_SHORT).show();
        }

        finish(); // Close the EditYogaCourseActivity
    }
}
