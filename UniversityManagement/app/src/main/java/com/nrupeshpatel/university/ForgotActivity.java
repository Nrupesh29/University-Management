package com.nrupeshpatel.university;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;

public class ForgotActivity extends AppCompatActivity {

    ProgressDialog loading;
    String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        final EditText email = (EditText) findViewById(R.id.username);

        Button reset = (Button) findViewById(R.id.btnForgot);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress = email.getText().toString();
                if (!emailAddress.isEmpty()) {
                    new ResetPassword().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter email address!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private class ResetPassword extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ForgotActivity.this, null, "Sending mail...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            Toast.makeText(getApplicationContext(), "New password sent to email address!!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("email", emailAddress);

            JSON_STRING = rh.sendPostRequest(Config.RESET_PASSWORD, data);

            return JSON_STRING;
        }
    }
}
