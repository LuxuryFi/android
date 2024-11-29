package com.example.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class YogaCourseDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yogaCourses1.db";
    private static final int DATABASE_VERSION = 10;

    public static final String TABLE_YOGA_COURSE = "yoga_course";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_CLASS_TYPE = "class_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_IMAGE = "image";  // Added image column
    public static final String COLUMN_POSITION = "position";  // Added image column

    public YogaCourseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the yoga_course table with the image column
        String CREATE_YOGA_COURSE_TABLE = "CREATE TABLE " + TABLE_YOGA_COURSE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DAY_OF_WEEK + " TEXT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_CAPACITY + " INTEGER, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_PRICE + " REAL, "
                + COLUMN_CLASS_TYPE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_TEACHER + " TEXT, "
                + COLUMN_IMAGE + " TEXT," // Added image column
                + COLUMN_POSITION + " TEXT" // Added image column
                + ")";
        db.execSQL(CREATE_YOGA_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add the image column in case it's missing in an older version of the database
        if (oldVersion < newVersion) {
            db.execSQL("ALTER TABLE " + TABLE_YOGA_COURSE + " ADD COLUMN position TEXT");
        }
    }

    public void addYogaCourse(YogaCourse yogaCourse) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DAY_OF_WEEK, yogaCourse.getDayOfWeek());
            values.put(COLUMN_TIME, yogaCourse.getTime());
            values.put(COLUMN_CAPACITY, yogaCourse.getCapacity());
            values.put(COLUMN_DURATION, yogaCourse.getDuration());
            values.put(COLUMN_PRICE, yogaCourse.getPrice());
            values.put(COLUMN_CLASS_TYPE, yogaCourse.getClassType());
            values.put(COLUMN_DESCRIPTION, yogaCourse.getDescription());
            values.put(COLUMN_TEACHER, yogaCourse.getTeacher());
            values.put(COLUMN_IMAGE, yogaCourse.getImage());  // Insert image field
            values.put(COLUMN_POSITION, yogaCourse.getPosition());
            db.insert(TABLE_YOGA_COURSE, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public YogaCourse getYogaCourseById(int id) {
        YogaCourse yogaCourse = null;
        String query = "SELECT * FROM " + TABLE_YOGA_COURSE + " WHERE " + COLUMN_ID + " = ?";

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)})) {
            if (cursor != null && cursor.moveToFirst()) {
                yogaCourse = cursorToYogaCourse(cursor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yogaCourse;
    }

    public List<YogaCourse> getAllYogaCourses() {
        List<YogaCourse> yogaCourses = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_YOGA_COURSE;

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    yogaCourses.add(cursorToYogaCourse(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yogaCourses;
    }

    public boolean updateYogaCourse(YogaCourse yogaCourse) {
        boolean isUpdated = false;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DAY_OF_WEEK, yogaCourse.getDayOfWeek());
            values.put(COLUMN_TIME, yogaCourse.getTime());
            values.put(COLUMN_CAPACITY, yogaCourse.getCapacity());
            values.put(COLUMN_DURATION, yogaCourse.getDuration());
            values.put(COLUMN_PRICE, yogaCourse.getPrice());
            values.put(COLUMN_CLASS_TYPE, yogaCourse.getClassType());
            values.put(COLUMN_DESCRIPTION, yogaCourse.getDescription());
            values.put(COLUMN_TEACHER, yogaCourse.getTeacher());
            values.put(COLUMN_IMAGE, yogaCourse.getImage());

            int rowsAffected = db.update(TABLE_YOGA_COURSE, values, COLUMN_ID + " = ?", new String[]{String.valueOf(yogaCourse.getId())});
            isUpdated = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    public boolean deleteYogaCourse(int id) {
        boolean isDeleted = false;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            int rowsAffected = db.delete(TABLE_YOGA_COURSE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            isDeleted = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    private YogaCourse cursorToYogaCourse(Cursor cursor) {
        return new YogaCourse(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),  // Retrieve the image field
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSITION))  // Retrieve the image field
        );
    }
}
