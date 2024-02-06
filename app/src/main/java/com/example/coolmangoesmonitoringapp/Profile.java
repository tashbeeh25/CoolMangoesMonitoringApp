package com.example.coolmangoesmonitoringapp;
import static com.example.coolmangoesmonitoringapp.Login.usernam;

import androidx.appcompat.app.AppCompatActivity;
import static com.example.coolmangoesmonitoringapp.Login.email;
import static com.example.coolmangoesmonitoringapp.Login.userId;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.app.AlertDialog;

public class Profile extends AppCompatActivity {

    public String API_URL;

    String body;
    TextView pair1, pair2, pair3, pair4, pair5, pair6, pair7, pair8, pair9;

    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9;

    public static String new_username;





    public static String new_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentUserInfo();

        Button logout_button = (Button) findViewById(R.id.logout);
        Button chat_button = (Button) findViewById(R.id.chatBtn);
        Button home_button = (Button) findViewById(R.id.dashboardBtn);

        pair1 = findViewById(R.id.pair1);
        image1 = findViewById(R.id.image1);
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = "new_username";
                API_URL = "https://api2.charlie-iot.com/api/update-username/" + userId + "/" ;

                // Pass the current value of pair1 to the dialog
                showCustomDialog1(pair1.getText().toString());
            }
        });

        pair2 = findViewById(R.id.pair2);
        image2 = findViewById(R.id.image2);
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = "new_fn";
                API_URL = "https://api2.charlie-iot.com/api/update-fn/" + userId + "/" ;
                // Pass the current value of pair1 to the dialog
                showCustomDialog2(pair2.getText().toString());        }
        });

        pair3 = findViewById(R.id.pair3);

        image3 = findViewById(R.id.image3);
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = "new_ln";

                API_URL = "https://api2.charlie-iot.com/api/update-ln/" + userId + "/" ;

                // Pass the current value of pair1 to the dialog
                showCustomDialog3(pair3.getText().toString());        }
        });

        pair4 = findViewById(R.id.pair4);

        image4 = findViewById(R.id.image4);
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = "new_email";
                API_URL = "https://api2.charlie-iot.com/api/update-email/" + userId + "/" ;
                // Pass the current value of pair1 to the dialog
                showCustomDialog4(pair4.getText().toString());        }
        });

        pair5 = findViewById(R.id.pair5);

        image5 = findViewById(R.id.image5);
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = "new_occup";
                API_URL = "https://api2.charlie-iot.com/api/update-occup/" + userId + "/" ;
                // Pass the current value of pair1 to the dialog
                showCustomDialog5(pair5.getText().toString());        }
        });

        pair6 = findViewById(R.id.pair6);

        image6 = findViewById(R.id.image6);
        image6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                body = "new_gender";
                API_URL = "https://api2.charlie-iot.com/api/update-gender/" + userId + "/" ;
                // Pass the current value of pair1 to the dialog
                showCustomDialog6(pair6.getText().toString());        }
        });


        pair7 = findViewById(R.id.pair7);

        image7 = findViewById(R.id.image7);
        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = "new_phone";
                API_URL = "https://api2.charlie-iot.com/api/update-phone/" + userId + "/" ;
                // Pass the current value of pair1 to the dialog
                showCustomDialog7(pair7.getText().toString());        }
        });

        pair8 = findViewById(R.id.pair8);

        image8 = findViewById(R.id.image8);
        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = "new_age";
                API_URL = "https://api2.charlie-iot.com/api/update-age/" + userId + "/" ;
                // Pass the current value of pair1 to the dialog
                showCustomDialog8(pair8.getText().toString());        }
        });

        pair9 = findViewById(R.id.pair9);
        image9 = findViewById(R.id.image9);
        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pass the current value of pair1 to the dialog
                showCustomDialog9(pair9.getText().toString());        }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
            }
        });


        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Dashboard.class);
                startActivity(intent);
            }
        });

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ChatsDashboard.class);
                startActivity(intent);
            }
        });
    }

    private void currentUserInfo() {
        OkHttpClient client3 = new OkHttpClient();

        String apiUrl = "https://api2.charlie-iot.com/api/get-user-by-email/" + email;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client3.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String username = json.getString("username");
                        String firstname = json.getString("first_name");
                        String lastname = json.getString("last_name");
                        String email = json.getString("email");
                        String occup = json.getString("occupation");
                        String gender = json.getString("gender");
                        String phone = json.getString("phone");
                        String age = json.getString("age");
                        //String password = json.getString("password");


                        runOnUiThread(() -> {
                            pair1.setText(username);
                            pair2.setText(firstname);
                            pair3.setText(lastname);
                            pair4.setText(email);
                            pair5.setText(occup);
                            pair8.setText(age);
                            pair7.setText(phone);
                            pair6.setText(gender);
                            //pair9.setText(password);

                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error
                }
            }
        });
    }

    public void showCustomDialog1(String currentPair1Value) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Username")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput1 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair1.setText(userInput1);
                    updateField(userInput1);
                    new_username = userInput1;
                    usernam = new_username;
                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog2(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change First Name")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput2 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair2.setText(userInput2);
                    updateField(userInput2);

                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog3(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Last Name")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput3 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair3.setText(userInput3);
                    updateField(userInput3);

                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog4(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Email")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput4 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair4.setText(userInput4);
                    updateField(userInput4);
                    email = userInput4;
                    new_email = email;

                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog5(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Occupation")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput5 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair5.setText(userInput5);
                    updateField(userInput5);

                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog6(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Gender")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput6 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair6.setText(userInput6);
                    updateField(userInput6);

                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog7(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Phone")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput7 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair7.setText(userInput7);
                    updateField(userInput7);

                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog8(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Age")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput8 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair8.setText(userInput8);
                    updateField(userInput8);

                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    public void showCustomDialog9(String currentPair1Value2) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the current value of pair1 to the EditText
        editText.setText(currentPair1Value2);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String userInput9 = editText.getText().toString();
                    // Update pair1 with the new value
                    pair9.setText(userInput9);
                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        dialog.show();
    }

    private static void showToast(Context context, String message) {
        // Replace this with your desired way of displaying the message
        // (e.g., Toast, Snackbar, updating a TextView)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }



    public void updateField(String updatedValue) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = "{\"" + body +"\": \"" + updatedValue + "\"}";

        RequestBody requestBody = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url(API_URL) // Assuming your API requires a resource ID in the URL
                .put(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        // Use the client to execute the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                // Handle the response here
                if (response.isSuccessful()) {
                    // Update successful
                    // You may want to process the response body if needed
                    //Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();

                } else {
                    // Update failed
                    // You can check the response code and handle errors accordingly
                    //Toast.makeText(getApplicationContext(), "Update was not successfull", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle network failure
                e.printStackTrace();
            }
        });
    }
}