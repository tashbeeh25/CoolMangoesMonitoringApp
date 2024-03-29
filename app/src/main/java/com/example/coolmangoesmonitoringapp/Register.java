package com.example.coolmangoesmonitoringapp;

import static android.content.ContentValues.TAG;
import static com.example.coolmangoesmonitoringapp.NewGroup.name;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    EditText first_name, last_name, username, email, password, occupation, phone, repassword;
    Spinner age, gender;
    String userId;
    private static final String API_URL = "https://api2.charlie-iot.com/api/register/";
    CircleImageView profile_img;
    Uri imageURI;
    String imageUri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    TextView log_txt;
    String ageSelectedItem, genderSelectedItem;

    Button register;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing The Account");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        profile_img = findViewById(R.id.profile);
        register = (Button) findViewById(R.id.reg_btn);
        first_name = findViewById(R.id.fname);
        last_name = findViewById(R.id.lname);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        occupation = findViewById(R.id.occupation);
        repassword = findViewById(R.id.repassword);
        password = findViewById(R.id.password);
        log_txt = findViewById(R.id.log_text);

        log_txt.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        String[] ageItems = {"Age", "range: 18-24", "range: 25-34", "range: 35-44", "range: 45-54", "range: 55-64", "range: over 60"};

        // Create an ArrayAdapter and set it on the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ageItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        age.setAdapter(adapter);

        age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item
                ageSelectedItem = ageItems[position];

                // Do something with the selected item, such as displaying it in a Toast
                Toast.makeText(Register.this, "Selected Item: " + ageSelectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here if nothing is selected
            }
        });


        String[] genderItems = {"Gender", "Male", "Female"};

        // Create an ArrayAdapter and set it on the Spinner
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderItems);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter2);

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item
                genderSelectedItem = genderItems[position];

                // Do something with the selected item, such as displaying it in a Toast
                Toast.makeText(Register.this, "Selected Item: " + genderSelectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here if nothing is selected
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String gn = name.toString();

                String fn = first_name.getText().toString();
                String ln = last_name.getText().toString();
                String usern = username.getText().toString();
                String emaill = email.getText().toString();
                String passwd = password.getText().toString();
                String repasswd = repassword.getText().toString();
                String phonee = phone.getText().toString();
                String occup = occupation.getText().toString();
                String ageSelected = ageSelectedItem;
                String genderSelected = genderSelectedItem;


                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String status = "Hey I am a new USER";


                if (TextUtils.isEmpty(fn) || (TextUtils.isEmpty(ln)) ||
                        (TextUtils.isEmpty(usern)) || (TextUtils.isEmpty(emaill)) ||
                        (TextUtils.isEmpty(passwd))) {
                    Toast.makeText(Register.this, "Please enter a valid information", Toast.LENGTH_SHORT).show();


                }
                else if(!passwd.matches(repasswd)) {
                    email.setError("The password do not match");

                }else if(passwd.length()<6){
                    password.setError("Password must be 6 characters or more");

                }else{
                    auth.createUserWithEmailAndPassword(emaill, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {
                            makeRegistrationRequest(fn, ln, usern, emaill, passwd, phonee, occup,  ageSelected, genderSelected);

                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();

                                DatabaseReference reference = database.getReference().child("user").child(id);
                                DatabaseReference reference1 = database.getReference().child("group").child(id);



                                StorageReference storageReference = storage.getReference().child("Upload").child(id);


                                if (imageURI!=null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageUri = uri.toString();
                                                        Users users = new Users(id, usern, fn, ln, emaill, passwd, imageUri,status);
                                                        //Groups groups = new Groups(id, gn);

                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete( Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(Register.this,Login.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(Register.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status = "Hey I'm Using This Application";
                                    imageUri = "https://firebasestorage.googleapis.com/v0/b/cool-mangoes-fc.appspot.com/o/360_F_328113542_31B2IVU37qZ09cXXA6iMSXs62Optrwok.jpg?alt=media&token=02be7394-47e5-4f9c-abb0-81698a8acbc2";
                                    //     public Users(String user_id, String username, String first_name, String last_name,  String email, String password, String profilePic, String status) {
                                    Users users = new Users(id, usern, fn, ln, emaill, passwd, imageUri, status);
                                    //Groups groups = new Groups(id, gn);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.show();
                                                Intent intent = new Intent(Register.this, Login.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(Register.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });

    }
    private void showSuccessToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Successfully Registered. Please login", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void showFailedToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Could not successfully register", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void makeRegistrationRequest(String fn, String ln, String usern, String emaill, String passwd, String age, String occup, String phonee, String gender) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonBody = new JSONObject();
        JSONObject profileObject = new JSONObject();

        try {
            // Adding user information to the main JSON object
            jsonBody.put("username", usern);
            jsonBody.put("first_name", fn);
            jsonBody.put("last_name", ln);
            jsonBody.put("email", emaill);
            jsonBody.put("password", passwd);

            // Adding profile information to the profile JSON object
            profileObject.put("age", age);
            profileObject.put("gender", gender);
            profileObject.put("phone", phonee);
            profileObject.put("occupation", occup);

            // Adding the profile JSON object to the main JSON object
            jsonBody.put("profile", profileObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(mediaType, jsonBody.toString());

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleSuccessfulResponse(responseBody);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSuccessToast();

                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                        }
                    });


                } else {
                    // Handle error
                    //Log.e("Registration", "HTTP Error: " + response.code());
                    // Call onFailure with a custom IOException to trigger the onFailure callback
                    onFailure(call, new IOException("HTTP Error: " + response.code()));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //showFailedToast();

                        }

                    });

                    Intent intent = new Intent(Register.this, Register.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //Log.e("Registration", "Network Failure: " + e.getMessage());
            }
        });
    }

    private void handleSuccessfulResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            // Parse and handle the response JSON as needed
            String message = jsonResponse.optString("message");

            // Assuming the response JSON contains a "message" key with a success message
            if ("Registration successful".equals(message)) {
                // Start the Dashboard activity

            } else {
                // Handle other cases or show a toast message
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
        }
    }

}