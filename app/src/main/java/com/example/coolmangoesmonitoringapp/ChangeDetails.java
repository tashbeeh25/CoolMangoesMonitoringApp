package com.example.coolmangoesmonitoringapp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.core.view.Change;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeDetails extends AppCompatActivity {

    private static final String API_URL = "https://api2.charlie-iot.com/api/contact-create/";

    EditText name, email, phone, query;

    Button send;

    String queryMessage;
    String senderEmail, sendername, senderphone, senderquery;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_details);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        query = findViewById(R.id.query);
        send = findViewById(R.id.send_btn);

        Button logout_button = (Button) findViewById(R.id.logoutBtn);
        Button setting_button = (Button) findViewById(R.id.setting);
        Button home_button = (Button) findViewById(R.id.dashboardBtn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fn = name.getText().toString();
                String em = email.getText().toString();
                String ph = phone.getText().toString();
                String que = query.getText().toString();

                EmailDetails(fn, em, ph, que);

                queryMessage = que;
                senderEmail = em;
                sendername = fn;
                senderphone = ph;
            }
        });




        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeDetails.this, Dashboard.class);
                startActivity(intent);
            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeDetails.this, Setting.class);
                startActivity(intent);
            }
        });
    }



    private void EmailDetails(String fn,String em, String ph, String que) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", fn);
            jsonBody.put("email", em);
            jsonBody.put("phone", ph);
            jsonBody.put("query", que);
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
                            buttonSendEmail();


                            // Intent
                            Intent intent = new Intent(ChangeDetails.this, ShowInfo.class);
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
                            showFailedToast();
                            Intent intent = new Intent(ChangeDetails.this, ChangeDetails.class);
                            startActivity(intent);


                        }

                    });

                    //Intent intent = new Intent(ChangeDetails.this, ChangeDetails.class);
                    //startActivity(intent);
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
            if ("Email was sent".equals(message)) {
                // Start the Dashboard activity

            } else {
                // Handle other cases or show a toast message
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
        }
    }

    private void showSuccessToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Email was sent", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void showFailedToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Could not send email. Try again", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    public void buttonSendEmail(){

        try {
            String senderEmai = "tashbeiph@gmail.com";
            String receiverEmail = "farid@ab5consulting.com";
            String passwordSender = "beym vwku nfng bwaf\n" + "\n";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmai, passwordSender);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            message.setSubject("Subject: Cool Mangoes Query");
            message.setText(queryMessage + "\n\n" + "Sender Details\n" + "Name of sender:  " + sendername + "\n" + "Sender email:  " + senderEmail +"\n" + "Sender Phone" + senderphone);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            thread.start();
        }catch(AddressException e){
            e.printStackTrace();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}