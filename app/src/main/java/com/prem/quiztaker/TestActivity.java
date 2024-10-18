package com.prem.quiztaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAPTURE_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int REQUEST_CHECK_SETTINGS = 1;

    private ImageView imageViewCaptured;
    private Uri photoUri;
    private String currentPhotoPath;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView coordinatesTextView;
    private double latitude;
    private double longitude;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ImageView btnBack = findViewById(R.id.imageView);
        CardView addImage = findViewById(R.id.addimage);
        imageViewCaptured = findViewById(R.id.imageViewCaptured);
        CardView OpenMap = findViewById(R.id.OpenMap);
        coordinatesTextView = findViewById(R.id.coordinateX);
        CardView Get_Coordinates = findViewById(R.id.Get_Coordinates);

        btnBack.setOnClickListener(view -> finish());

        addImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        OpenMap.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {

                openGoogleMaps();
            }
        });


        Get_Coordinates.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                getCoordinates();

            }
        });

    }
        private void getCoordinates () {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            coordinatesTextView.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                        } else {
                            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Method to open coordinates in Google Maps
        private void openGoogleMaps () {
            // Construct the URI
            String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude;

            // Create an Intent to view the location
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

            try {
                // Start Google Maps if available
                startActivity(intent);
            } catch (Exception e) {
                // If Maps is not installed, open it in the Play Store
                Toast.makeText(this, "Google Maps not found. Redirecting to Play Store...", Toast.LENGTH_SHORT).show();
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
                startActivity(playStoreIntent);
            }
        }

        private void openCamera () {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    File photoFile = createImageFile();
                    photoUri = FileProvider.getUriForFile(this,
                            "com.prem.quiztaker.fileprovider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
                } catch (IOException ex) {
                    Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private File createImageFile () throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "QuizTaker");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        }

        private void scanFile (String path){
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            scanIntent.setData(uri);
            sendBroadcast(scanIntent);
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
                if (photoUri != null) {
                    imageViewCaptured.setVisibility(View.VISIBLE);
                    imageViewCaptured.setImageURI(photoUri);
                    scanFile(currentPhotoPath);
                } else {
                    Toast.makeText(this, "Image not captured", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == CAMERA_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCoordinates();
                } else {
                    Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
                }
            }
        }

}

