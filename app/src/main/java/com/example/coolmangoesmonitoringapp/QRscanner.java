package com.example.coolmangoesmonitoringapp;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class QRscanner extends AppCompatActivity {

    // UI Views
    private MaterialButton cameraBtn;
    private MaterialButton galleryBtn;
    private ImageView imageIv;
    private MaterialButton scanBtn;
    private TextView resultTv;

    // To handle the result of camera/gallery permissions in onRequestPermissionResults
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    // Arrays of permissions required to pick image from camera/gallery
    private String[] cameraPermissions;
    private String[] storagePermissions;

    // Uri of the image that we will take from camera/gallery
    private Uri imageUri = null;

    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;

    private static final String TAG ="MAIN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        // init UI views
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        imageIv = findViewById(R.id.imageIv);
        scanBtn = findViewById(R.id.scanBtn);
        resultTv = findViewById(R.id.resultTv);

        // init the arrays of permissions required to pick image from camera/gallery
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}; // Image from camera: Camera and WRITE_EXTERNAL_STORAGE
        cameraPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}; // Image from gallery: WRITE_EXTERNAL_STORAGE permission only

        /** init/setup BarcodeScannerOptions, put comma seperated types in .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS) or add Barcode.FORMAT_ALL_FORMATS
         * If you want to scan all formats
         * The following formats are supported:
         * Code 128 (FORMAT_CODE_128), Code 39 (FORMAT_CODE_39), Code 93 (FORMAT_CODE_93), Codabar (FORMAT_CODABAR), EAN-13 (FORMAT_EAN_13)
         * EAN-8
         * **/
        barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        // init BarcodeScanner with BarcodeScannerOptions
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);

        // Handle cameraBtn click, check permissions related to camera (i.e. WRITE STORAGE & CAMERA) and take image from camera
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermission()){
                    //permission required for camera already granted, launch camera intent
                    pickImageCamera();
                }else{
                    //permission required for camera already not granted, request permissions
                    requestCameraPermission();
                }
            }
        });

        // Handle galleryBtn click, check permission related to gallery (i.e. WRITE STORAGE) and take image from camera
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkStoragePermission()){
                    //permission required for camera already granted, launch camera intent
                    pickImageGallery();
                }else{
                    //permission required for camera already not granted, request permissions
                    requestStoragePermission();
                }
            }
        });

        // Handle scanBtn click, scan the barcode/QR code from image picked from camera/gallery
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri == null){
                    // Image is not picked yet
                    Toast.makeText(QRscanner.this, "Pick image first...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QRscanner.this, Dashboard.class);
                    startActivity(intent);
                }else{
                    // Image was picked, start scanning barcode/QR code
                    detectResultFromImage();
                }

            }
        });
    }

    private void detectResultFromImage(){
        try{
            // Prepare image from image url
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);
            // Start scanning the Barcode/QR code data from image
            Task<List<Barcode>> barcodeResult = barcodeScanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            // Task completed successfully, we can get detailed info now
                            extractBarCodeQRCodeInfo(barcodes);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception, we can't get any detail
                            Toast.makeText(QRscanner.this, "Failed scanning due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }catch(Exception e){
            // Failed with an exception either due to preparing InputImage or issue in BarcodeScanner init
            Toast.makeText(this, "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void extractBarCodeQRCodeInfo(List<Barcode> barcodes) {
        // Get information from barcodes
        for (Barcode barcode: barcodes){
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            // Raw info scanned from teh Barcode/QR code
            String rawValues = barcode.getRawValue();
            Log.d(TAG, "extractBarCodeQRCodeInfo: rawValue: "+ rawValues);


            int valueType = barcode.getValueType();

            // Manage each type seperately, done four types to save time, but can manage all
            switch (valueType) {
                case Barcode.TYPE_WIFI: {

                    // To get WIFI related data
                    Barcode.WiFi typeWifi = barcode.getWifi();
                    // To get all the info about the WIFI
                    String ssid = "" + typeWifi.getSsid();
                    String password = "" + typeWifi.getPassword();
                    String encryptedType = "" + typeWifi.getEncryptionType();
                    // Show in log
                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_WIFI: ");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: ssid: " + ssid);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: password: " + password);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: encryptedtype: " + encryptedType);

                    // Set to textview
                    resultTv.setText("TYPE: TYPE_WIFI \nssid:" + ssid + "\npassword; " + password + "\nencryptedType: " + encryptedType + "\nraw value: " + rawValues);
                }
                break;
                case Barcode.TYPE_URL: {
                    // To get url related data
                    Barcode.UrlBookmark typeUrl = barcode.getUrl();

                    // Get all info about the url
                    String title = typeUrl.getTitle();
                    String url = typeUrl.getUrl();

                    // Show in log
                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_URL");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: title " + title);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: url " + url);

                    // Set to textview
                    resultTv.setText("TYPE: TYPE_URL \ntitle: " + title + "\nurl: " + url + "\nraw value: " + rawValues);
                }
                break;
                case Barcode.TYPE_EMAIL: {
                    // To get email related data
                    Barcode.Email typeEmail = barcode.getEmail();

                    // Get all info about the email
                    String address = "" + typeEmail.getAddress();
                    String body = "" + typeEmail.getBody();
                    String subject = "" + typeEmail.getSubject();

                    // Show in log
                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_EMAIL");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: address " + address);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: body " + body);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: subject " + subject);

                    // Set to textview
                    resultTv.setText("TYPE: TYPE_EMAIL \naddress: " + address + "\nbody: " + body + "\nsubject: " + subject + "\nraw value: " + rawValues);
                }
                break;
                case Barcode.TYPE_CONTACT_INFO: {
                    // To get contact related data
                    Barcode.ContactInfo typeContact = barcode.getContactInfo();

                    // Get all info about the contact
                    String title = "" + typeContact.getTitle();
                    String organiser = "" + typeContact.getOrganization();
                    String name = "" + typeContact.getName().getFirst() + " " + typeContact.getName().getLast();
                    String phone = "" + typeContact.getPhones().get(0).getNumber();

                    // Show in log
                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_CONTACT_INFO");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: title " + title);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: organiser " + organiser);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: name " + name);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: phone " + phone);

                    // Set to textview
                    resultTv.setText("TYPE: TYPE_CONTACT_INFO: \ntitle: " + title + "\norganiser: " + organiser + "\nname: " + name + "\nphone: " + phone + "\nraw value: " + rawValues);
                }
                break;
                default:{
                    resultTv.setText("raw value: " + rawValues);
                }
            }
        }
    }

    private void pickImageGallery(){
        // Intent to pick image from gallery, will show all resources from where we can pick the image
        Intent intent = new Intent(Intent.ACTION_PICK);

        // Set type of file we want to pick i.e. image
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }


    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Here we will receive the image, if picked from gallery
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Image picked, get the uri of the image picked
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri" + imageUri);
                        // Set to imageview
                        imageIv.setImageURI(imageUri);
                    } else {
                        // Cancelled
                        Toast.makeText(QRscanner.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera() {
        // Get ready the image data to store in MediaStore
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");

        // Image Uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        // Intent to launch camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Here we will receive the image, if taken from camera
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Image is taken from camera
                        Intent data = result.getData();
                        // We already have the image in imageUri using function pickImageCamera()
                        Log.d(TAG, "onActivityResult: imageUri: " + imageUri);
                        // Set to imageview
                        imageIv.setImageURI(imageUri);
                    } else {
                        // Cancelled
                        Toast.makeText(QRscanner.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission() {
        /** Check if storage permission is allowed or not
         Return true if allowed, false if not allowed **/
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        // return result true if permission WRITE_EXTERNAL_STORAGE granted or false if denied
        return result;
    }

    private void requestStoragePermission() {
        // Request storage permission (for gallery image pick)
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        // Check if camera permission allowed, true if yes false if no
        boolean resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        // Check if storage permission allowed, true if yes false if no
        boolean resultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        // Return both results as true/false
        return resultCamera && resultStorage;
    }

    private void requestCameraPermission() {
        // Request camera permissions (for camera intent)
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    // Handle runtime permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // Check if some actions from permission dialog performed or not Allow/Deny
                if (grantResults.length > 0) {
                    // Check if Camera, Storage permissions granted, contains boolean results either true or false
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    // Check if both permissions are granted or not
                    if (cameraAccepted && storageAccepted) {
                        // Both permissions (Camera & Gallery) are granted, we can launch camera intent
                        pickImageCamera();

                    } else {
                        // One or both permissions are denied, can't launch camera intent
                        Toast.makeText(this, "Camera & Storage permissions are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                // Check if some actions from permission dialog performed or not Allow/Deny
                if (grantResults.length > 0) {
                    // Check if Camera, Storage permissions granted, contains boolean results either true or false
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    // Check if storage permission is granted or not
                    if (storageAccepted) {
                        // Storage permission granted, we can launch gallery intent
                        pickImageGallery();
                    } else {
                        // Storage permission denied, can't launch gallery intent
                        Toast.makeText(this, "Camera & Storage permissions are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }
}

