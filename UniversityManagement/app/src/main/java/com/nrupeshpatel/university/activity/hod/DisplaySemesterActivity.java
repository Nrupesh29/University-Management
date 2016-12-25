package com.nrupeshpatel.university.activity.hod;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Semester;
import com.nrupeshpatel.university.adapter.SemesterAdapter;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class DisplaySemesterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SemesterAdapter mAdapter;
    ArrayList<Semester> semesterList = new ArrayList<>();
    String semester_number, faculty_code, type;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_semester);

        final String intent_type = getIntent().getStringExtra("CLASS");
        faculty_code = getIntent().getStringExtra("faculty_code");
        type = getIntent().getStringExtra("type");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (type.equals("faculty_subject")) {
            getSupportActionBar().setTitle("Faculty-Subject Allocation");
        } else if (type.equals("class_student")) {
            getSupportActionBar().setTitle("Class-Student Allocation");
        }

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        session = new SessionManager(getApplicationContext());

        new getSemesters().execute();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new SemesterAdapter(semesterList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Semester semester = semesterList.get(position);
                semester_number = semester.getNumber();
                if (intent_type != null) {
                    Intent i = new Intent(DisplaySemesterActivity.this, DisplayClassActivity.class);
                    i.putExtra("semester_number", semester_number);
                    startActivity(i);
                } else {
                    if (faculty_code != null) {
                        Intent i = new Intent(DisplaySemesterActivity.this, FacultySubjectActivity.class);
                        i.putExtra("semester_number", semester_number);
                        i.putExtra("faculty_code", faculty_code);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(DisplaySemesterActivity.this, BranchSubjectActivity.class);
                        i.putExtra("semester_number", semester_number);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private class getSemesters extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            semesterList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            pBar.setVisibility(View.INVISIBLE);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<>();
            data.put("branch_code", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));

            JSON_STRING = rh.sendPostRequest(Config.GET_SEMESTER, data);

            for (int j = 1; j <= Integer.parseInt(JSON_STRING); j++) {
                Semester semester = new Semester(String.valueOf(j), String.valueOf(j));
                semesterList.add(semester);
            }

            return null;
        }
    }
}
