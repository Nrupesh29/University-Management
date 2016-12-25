package com.nrupeshpatel.university.activity.faculty;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Subject;
import com.nrupeshpatel.university.adapter.SubjectDisplayAdapter;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class SubjectSyllabusActivity extends AppCompatActivity {

    List<Subject> subjectList = new ArrayList<>();
    RecyclerView recyclerView;
    SubjectDisplayAdapter mAdapter;
    ProgressBar pBar;
    SessionManager session;

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
        setContentView(R.layout.activity_syllabus_subject);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Syllabus Tracking");

        session = new SessionManager(getApplicationContext());

        pBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new SubjectDisplayAdapter(subjectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Subject subject = subjectList.get(position);
                Intent i = new Intent(SubjectSyllabusActivity.this, ClassSyllabusActivity.class);
                i.putExtra("subject_code", subject.getCode());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        new getData().execute();
    }

    private class getData extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.INVISIBLE);
            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("faculty_username", session.getUserDetails().get(SessionManager.KEY_USERNAME));

            JSON_STRING = rh.sendPostRequest(Config.FACULTY_DATA, data);

            Log.i("JSON", JSON_STRING);

            try {

                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("subject");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("subject_name");
                    String code = jo.getString("subject_code");

                    Subject subject = new Subject(name, code);
                    subjectList.add(subject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
