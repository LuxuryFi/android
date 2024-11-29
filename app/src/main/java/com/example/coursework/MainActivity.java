package com.example.coursework;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private YogaCourseAdapter adapter;
    private List<YogaCourse> yogaCourses = new ArrayList<>();
    private List<YogaCourse> originalCourses = new ArrayList<>();
    private YogaCourseDBHelper dbHelper;

    private ActivityResultLauncher<Intent> addCourseLauncher;
    private ActivityResultLauncher<Intent> viewCourseLauncher;
    private ActivityResultLauncher<Intent> editCourseLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new YogaCourseDBHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadYogaCourses();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterYogaCourses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterYogaCourses(newText);
                return false;
            }
        });

        addCourseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadYogaCourses();
                    }
                });

        viewCourseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadYogaCourses();
                    }
                });

        editCourseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadYogaCourses();
                    }
                });

        findViewById(R.id.addButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddYogaCourseActivity.class);
            addCourseLauncher.launch(intent);
        });
    }

    private void filterYogaCourses(String query) {
        List<YogaCourse> filteredCourses = new ArrayList<>();

        if (query.isEmpty()) {
            filteredCourses.addAll(originalCourses);
        } else {
            for (YogaCourse course : originalCourses) {
                if (course.getClassType().toLowerCase().contains(query.toLowerCase()) ||
                        course.getTeacher().toLowerCase().contains(query.toLowerCase()) ||
                        course.getDayOfWeek().toLowerCase().contains(query.toLowerCase())) {
                    filteredCourses.add(course);
                }
            }
        }

        // Ensure the adapter is initialized before updating
        if (adapter != null) {
            adapter.updateYogaCourses(filteredCourses);
        }
    }

    private void loadYogaCourses() {
        new LoadYogaCoursesTask(this).execute();
    }

    private static class LoadYogaCoursesTask extends AsyncTask<Void, Void, List<YogaCourse>> {

        private WeakReference<MainActivity> activityReference;

        LoadYogaCoursesTask(MainActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected List<YogaCourse> doInBackground(Void... voids) {
            MainActivity activity = activityReference.get();
            if (activity != null) {
                return activity.dbHelper.getAllYogaCourses();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<YogaCourse> result) {
            MainActivity activity = activityReference.get();
            if (activity != null) {
                activity.originalCourses.clear();
                activity.originalCourses.addAll(result);
                activity.yogaCourses.clear();
                activity.yogaCourses.addAll(result);

                // Initialize the adapter if it is not already initialized
                if (activity.adapter == null) {
                    activity.adapter = new YogaCourseAdapter(activity, activity.yogaCourses, new YogaCourseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int yogaCourseId) {
                            activity.openYogaCourseDetail(yogaCourseId);
                        }
                    });
                    activity.recyclerView.setAdapter(activity.adapter);
                } else {
                    activity.adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void openYogaCourseDetail(int yogaCourseId) {
        Intent intent = new Intent(MainActivity.this, YogaCourseDetailActivity.class);
        intent.putExtra("yogaCourseId", yogaCourseId);
        viewCourseLauncher.launch(intent);
    }

    public void openYogaCourseEdit(int yogaCourseId) {
        Intent intent = new Intent(MainActivity.this, EditYogaCourseActivity.class);
        intent.putExtra("yogaCourseId", yogaCourseId);
        editCourseLauncher.launch(intent);
    }
}
