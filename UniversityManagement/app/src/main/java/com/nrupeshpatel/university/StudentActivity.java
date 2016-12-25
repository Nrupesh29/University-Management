package com.nrupeshpatel.university;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.activity.student.DetailAttendance;
import com.nrupeshpatel.university.activity.student.SubjectSyllabusStudentActivity;
import com.nrupeshpatel.university.adapter.Attendance;
import com.nrupeshpatel.university.adapter.Subject;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class StudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;
    private PieChart mPie;
    ProgressBar pBar;
    public static List<Attendance> attendanceList = new ArrayList<>();
    public static List<Subject> subjectList = new ArrayList<>();
    int finalAttendance = 0, totalLectures = 0, absentLectures = 0;
    double percentage;
    TextView attended, total;
    public static String class_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
        welcomeText.setText("WELCOME " +session.getUserDetails().get(SessionManager.KEY_USERNAME).toUpperCase());

        attended = (TextView) findViewById(R.id.present);
        total = (TextView) findViewById(R.id.total);

        new getData().execute();

        mPie = (PieChart) findViewById(R.id.chart2);
        mPie.setDescription("");
        mPie.setExtraOffsets(5.0F, 10.0F, 5.0F, 5.0F);
        mPie.setDragDecelerationFrictionCoef(0.95F);
        mPie.setCenterTextColor(Color.parseColor("#335269"));
        mPie.setCenterTextSize(50.0F);
        mPie.setDrawHoleEnabled(true);
        //mPie.setHoleColorTransparent(true);
        mPie.setTransparentCircleColor(-1);
        mPie.setTransparentCircleAlpha(110);
        mPie.setHoleRadius(80.0F);
        mPie.setTransparentCircleRadius(60.0F);
        mPie.setRotationAngle(0.0F);
        mPie.setRotationEnabled(true);
        mPie.setHighlightPerTapEnabled(true);
        mPie.setTouchEnabled(false);
        mPie.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mPie.getLegend().setEnabled(false);
        mPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentActivity.this, DetailAttendance.class));
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final SessionManager session = new SessionManager(StudentActivity.this);
            new AlertDialog.Builder(StudentActivity.this, R.style.AppCompatAlertDialogStyle)
                    .setTitle("Logout?")
                    .setMessage("Press yes if you want to logout.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            session.logoutUser();
                        }
                    })
                    .setNegativeButton("No", null)
                    .create()
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_syllabusTrack) {
            Intent i = new Intent(StudentActivity.this, SubjectSyllabusStudentActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setPieData(int paramInt, float paramFloat) {
        ArrayList localArrayList1 = new ArrayList();
        localArrayList1.add(new Entry(finalAttendance, 0));
        localArrayList1.add(new Entry(absentLectures, 0));
        ArrayList localArrayList2 = new ArrayList();
        localArrayList2.add("0");
        localArrayList2.add("1");
        PieDataSet localPieDataSet = new PieDataSet(localArrayList1, "");
        localPieDataSet.setSliceSpace(0.0F);
        localPieDataSet.setSelectionShift(5.0F);
        ArrayList localArrayList3 = new ArrayList();
        localArrayList3.add(Integer.valueOf(Color.parseColor("#EB6F5D")));
        localArrayList3.add(Integer.valueOf(Color.rgb(186, 186, 186)));
        localPieDataSet.setColors(localArrayList3);
        PieData localPieData = new PieData(localArrayList2, localPieDataSet);
        localPieData.setValueTextColor(0);
        this.mPie.setData(localPieData);
        this.mPie.highlightValues(null);
        this.mPie.invalidate();
    }

    public static float getPercentage(int attended, int total) {
        float proportionCorrect = ((float) attended) / ((float) total);
        return proportionCorrect * 100;
    }

    private class getData extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pBar.setVisibility(View.VISIBLE);
            attendanceList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            pBar.setVisibility(View.INVISIBLE);
            percentage = getPercentage(finalAttendance, totalLectures);
            percentage = (double) Math.round(percentage * 100) / 100;
            absentLectures = totalLectures - finalAttendance;
            mPie.setCenterText(percentage+"%");
            setPieData(3, 100.0F);
            attended.setText(" "+String.valueOf(finalAttendance));
            total.setText(" "+String.valueOf(totalLectures));
            //Toast.makeText(StudentActivity.this, String.valueOf(finalAttendance), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("student_enrollment", session.getUserDetails().get(SessionManager.KEY_USERNAME));

            JSON_STRING = rh.sendPostRequest(Config.STUDENT_DATA, data);

            try {

                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("attendance");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String total = jo.getString("total");
                    String name = jo.getString("name");
                    String code = jo.getString("code");
                    String lecture = jo.getString("lecture");
                    class_id = jo.getString("class_id");

                    finalAttendance = finalAttendance + Integer.parseInt(total);
                    totalLectures = totalLectures + Integer.parseInt(lecture);
                    Subject subject = new Subject(name, code);
                    subjectList.add(subject);
                    Attendance attendance = new Attendance(total, name, code, lecture);
                    attendanceList.add(attendance);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
