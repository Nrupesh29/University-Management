package com.nrupeshpatel.university;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class LoginActivity extends AppCompatActivity {

    String username, password, department, semester;
    ProgressDialog loading;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        setContentView(R.layout.activity_login);

        final EditText name = (EditText) findViewById(R.id.username);
        final EditText pass = (EditText) findViewById(R.id.password);
        Button login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = name.getText().toString();
                password = pass.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter credentials!!", Toast.LENGTH_SHORT).show();
                } else {
                    /*ConnectionDetector cd = new ConnectionDetector(LoginActivity.this);
                    Boolean isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {*/
                        new Login().execute();
                    /*} else {
                        Toast.makeText(getApplicationContext(), "No Internet Connectivity!!", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        });

        Button contact = (Button) findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent("android.intent.action.SENDTO");
                localIntent.setData(Uri.parse("mailto:" + Uri.encode("admin@itmuniverse.com") + "?subject=" + Uri.encode("ITM Universe Application Query") + "&body=" + Uri.encode("")));
                startActivity(Intent.createChooser(localIntent, "Send mail..."));
            }
        });

        Button forgot = (Button) findViewById(R.id.btnForgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
                finish();
            }
        });
    }

    private class Login extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;
        String user_type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(LoginActivity.this, null, "Authenticating User...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Invalid username or password")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray("result");

                    JSONObject jo = result.getJSONObject(0);
                    user_type = jo.getString("user_type");
                    if (user_type.equals("Head of department") || user_type.equals("Faculty")) {
                        department = jo.getString("department");
                        semester = jo.getString("branch_semester");
                    } else {
                        department = "";
                        semester = "";
                    }
                    session.createLoginSession(username, user_type, department, semester);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent i = null;

                if (user_type.equals("Admin")) {
                    i = new Intent(LoginActivity.this, MainActivity.class);
                } else if (user_type.equals("Head of department")) {
                    i = new Intent(LoginActivity.this, HODActivity.class);
                } else if (user_type.equals("Faculty")) {
                    i = new Intent(LoginActivity.this, FacultyActivity.class);
                } else if (user_type.equals("Student")) {
                    i = new Intent(LoginActivity.this, StudentActivity.class);
                }
                startActivity(i);
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);

            JSON_STRING = rh.sendPostRequest(Config.LOGIN_VERIFY, data);

            return JSON_STRING;
        }
    }
}
