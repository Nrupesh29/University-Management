package com.nrupeshpatel.university.activity.faculty;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Faculty;
import com.nrupeshpatel.university.adapter.StudentAttendance;
import com.nrupeshpatel.university.adapter.StudentAttendanceAdapter;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class AttendaceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StudentAttendanceAdapter mAdapter;
    ArrayList<Faculty> studentList = new ArrayList<>();
    ArrayList<StudentAttendance> attendanceList = new ArrayList<>();
    String class_id, subject_code, date_today;
    ProgressDialog loading;
    SessionManager session;
    ProgressBar pBar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendace);

        class_id = getIntent().getStringExtra("class_id");
        subject_code = getIntent().getStringExtra("subject_code");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM");

        SimpleDateFormat df2 = new SimpleDateFormat("MMMM dd, yyyy");
        String display_date = df2.format(c.getTime());

        date_today = df.format(c.getTime());

        pBar = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        dateDisplay.setText(display_date);

        session = new SessionManager(getApplicationContext());

        //Toast.makeText(getApplicationContext(), session.getUserDetails().get(SessionManager.KEY_DEPARTMENT), Toast.LENGTH_SHORT).show();

        Button submit = (Button) findViewById(R.id.submitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AttendaceActivity.this, R.style.AppCompatAlertDialogStyle)
                        .setTitle("Submit Attendance?")
                        .setMessage("Do you want to submit current attendance?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new sendAttendance().execute();
                                //Toast.makeText(getApplicationContext(), attendanceList.toString(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
        });
        Button cancel = (Button) findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AttendaceActivity.this, R.style.AppCompatAlertDialogStyle)
                        .setTitle("Cancel Attendance?")
                        .setMessage("Do you want to cancel current attendance?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new StudentAttendanceAdapter(studentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final CheckBox cb = (CheckBox) view.findViewById(R.id.attendanceStatus);
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cb.isChecked()) {
                            attendanceList.remove(position);
                            StudentAttendance present = new StudentAttendance(studentList.get(position).getTitle(), studentList.get(position).getCode(), "1");
                            attendanceList.add(position, present);
                        } else if (!cb.isChecked()) {
                            attendanceList.remove(position);
                            StudentAttendance absent = new StudentAttendance(studentList.get(position).getTitle(), studentList.get(position).getCode(), "0");
                            attendanceList.add(position, absent);
                        }
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        new getStudents().execute();
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private class getStudents extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            studentList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            for (int i = 0; i<studentList.size(); i++) {
                StudentAttendance student = new StudentAttendance(studentList.get(i).getTitle(), studentList.get(i).getCode(), "A");
                attendanceList.add(i, student);
            }
            mAdapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<>();
            data.put("class_id", class_id);
            data.put("branch_code", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));
            data.put("subject_code", subject_code);
            data.put("date", date_today);
            Log.i("BRANCH", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));

            JSON_STRING = rh.sendPostRequest(Config.GET_ATTENDANCE_LIST, data);

            try {
                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("student");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("student_name");
                    String code = jo.getString("student_enrollment");

                    Faculty faculty = new Faculty(name, code);
                    studentList.add(faculty);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(AttendaceActivity.this, R.style.AppCompatAlertDialogStyle)
                .setTitle("Cancel Attendance?")
                .setMessage("Do you want to cancel current attendance?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    private class sendAttendance extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(AttendaceActivity.this, null, "Submitting Attendance...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                finish();
                Toast.makeText(getApplicationContext(), "Attendance Complete!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error!!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            for (int i = 0; i<attendanceList.size(); i++) {
                HashMap<String, String> data = new HashMap<>();
                data.put("class_id", class_id);
                data.put("branch_code", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));
                data.put("subject_code", subject_code);
                data.put("date", date_today);
                data.put("enrollment", attendanceList.get(i).getCode());
                data.put("status", attendanceList.get(i).getAttendance());

                JSON_STRING = rh.sendPostRequest(Config.SEND_ATTENDANCE, data);
            }

            return JSON_STRING;
        }
    }
}
