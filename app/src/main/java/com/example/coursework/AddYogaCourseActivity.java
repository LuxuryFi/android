package com.example.coursework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.Manifest;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class AddYogaCourseActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;

    private EditText dayOfWeekEditText, timeEditText, capacityEditText, durationEditText, priceEditText,
            classTypeEditText, descriptionEditText, teacherEditText, imageText, positionText;
    private Button saveButton, backButton;
    private YogaCourseDBHelper dbHelper;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextureView textureView;

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"
    };

    private ImageView imageView;
    private EditText inputPictureUri;

    CameraDevice cameraDevice;
    String cameraId;
    Size imageDimensions;
    CaptureRequest.Builder captureRequestBuilder;

    File file;
    Handler backgroundHandler;
    HandlerThread handlerThread;


    CameraCaptureSession cameraSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_yoga_course);

        dbHelper = new YogaCourseDBHelper(this);

        // Initialize EditText views
        dayOfWeekEditText = findViewById(R.id.dayOfWeekEditText);
        timeEditText = findViewById(R.id.timeEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        durationEditText = findViewById(R.id.durationEditText);
        priceEditText = findViewById(R.id.priceEditText);
        classTypeEditText = findViewById(R.id.classTypeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        teacherEditText = findViewById(R.id.teacherEditText);
        imageText = findViewById(R.id.textImage);
        textureView = findViewById(R.id.texture);
        positionText = findViewById(R.id.positionEditText);

        inputPictureUri = findViewById(R.id.textImage);
        Button btn = findViewById(R.id.buttonAction);

        btn.setOnClickListener(v -> {


            try {
                takePicture();
                fetchLocation();
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//// Button to fetch and display location
//        Button locationButton = findViewById(R.id.locationButton);  // Add this button in your layout
//        locationButton.setOnClickListener(v -> fetchLocation());

        textureView.setSurfaceTextureListener(surfaceTextureListener);

        // Initialize Save button
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            try {
                saveYogaCourse();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        // Initialize Back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Close the activity without saving
    }

    private void saveYogaCourse() throws JSONException {
        String dayOfWeek = dayOfWeekEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String capacityStr = capacityEditText.getText().toString();
        String durationStr = durationEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String classType = classTypeEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String teacher = teacherEditText.getText().toString();
        String image = imageText.getText().toString();
        String position = positionText.getText().toString();

        if (dayOfWeek.isEmpty() || time.isEmpty() || capacityStr.isEmpty() || durationStr.isEmpty() ||
                priceStr.isEmpty() || classType.isEmpty() || description.isEmpty() || teacher.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonPayload = new JSONObject();
        try {
            jsonPayload.put("userId", "minh");
            jsonPayload.put("dayOfWeek", dayOfWeek);
            jsonPayload.put("time", time);
            jsonPayload.put("capacity", capacityStr);
            jsonPayload.put("duration", durationStr);
            jsonPayload.put("price", priceStr);
            jsonPayload.put("classType", classType);
            jsonPayload.put("description", description);
            jsonPayload.put("teacher", teacher);
            jsonPayload.put("image", image);
            jsonPayload.put("position", position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

// Print out the JSON for debugging
        Log.e("TAG", "Sending JSON: " + jsonPayload.toString());

// Retrofit setup
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

// Call API with the JSON payload
        Call<Upload> call = apiInterface.getUserInformation(jsonPayload);
        call.enqueue(new Callback<Upload>() {
            @Override
            public void onResponse(Call<Upload> call, Response<Upload> response) {
                if (response.isSuccessful()) {
                    Log.e("TAG", "Upload successful");
                } else {
                    Log.e("TAG", "Upload failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Upload> call, Throwable t) {
                Log.e("TAG", "Upload failed: " + t.getMessage());
            }
        });
        int capacity = 0;
        int duration = 0;
        double price = 0.0;

        try {
            capacity = Integer.parseInt(capacityStr);
            duration = Integer.parseInt(durationStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format. Please enter valid values.", Toast.LENGTH_SHORT).show();
            return;
        }

        YogaCourse newCourse = new YogaCourse(0, dayOfWeek, time, capacity, duration, price, classType, description, teacher, image, position);

        // Using ExecutorService to perform the task in background
        executor.execute(() -> {
            dbHelper.addYogaCourse(newCourse);  // Add the course to the database

            runOnUiThread(() -> {
                // Show success message
                Toast.makeText(AddYogaCourseActivity.this, "Yoga class added successfully!", Toast.LENGTH_SHORT).show();

                // Send result back to MainActivity to reload the data
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();  // Close AddYogaCourseActivity and return to MainActivity
            });
        });
    }

    private void selectPictureFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launchActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imageView.setImageBitmap(bitmap);
                        long timestamp = System.currentTimeMillis();
                        File file = bitmapToFile(bitmap, getApplicationContext().getFilesDir().getAbsolutePath() + File.pathSeparator + String.valueOf(timestamp));
                        inputPictureUri.setText(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private File bitmapToFile(Bitmap bitmap, String filepath) {
        File file = null;
        try {
            file = new File(filepath + ".png");  // Append .png extension
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    @SuppressLint("MissingPermission")
    private void openCamera() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraId = cameraManager.getCameraIdList()[0];

        CameraCharacteristics cc = cameraManager.getCameraCharacteristics(cameraId);
        StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];


        cameraManager.openCamera(cameraId, stateCallback, null);
    }

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            try {
                startCameraPreview();
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void startCameraPreview() throws CameraAccessException {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());
        Surface surface = new Surface(texture);

        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(surface);

        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                if (cameraDevice == null) {
                    return;
                }
                cameraSession = session;
                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        }, null);
    }

    private void updatePreview() throws CameraAccessException {
        if (cameraDevice == null) {
            return;
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        cameraSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
    }

    protected void onResume() {
        super.onResume();
        startBackgroundTread();

        if(textureView.isAvailable()) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    private void startBackgroundTread() {
        handlerThread = new HandlerThread("Camera Background");
        handlerThread.start();

        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    protected  void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        if (handlerThread != null) {
            handlerThread.quitSafely();  // Properly stop the thread without using stop()
            backgroundHandler = null;
            handlerThread = null;
        }
    }

    private void takePicture() throws CameraAccessException {
        long ts = System.currentTimeMillis();
        String name = Environment.getExternalStorageDirectory() + "/" + ts + ".jpg";
        inputPictureUri.setText(name);
        file = new File(name);
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Request location
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    // Display location in Toast
                    String message = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    Log.d("LocationPermission", "Permission granted" + message);
                    positionText.setText(message);
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }
}
