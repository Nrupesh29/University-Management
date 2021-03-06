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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Class;
import com.nrupeshpatel.university.adapter.ClassDisplayAdapter;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class DisplayClassActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ClassDisplayAdapter mAdapter;
    ArrayList<Class> classList = new ArrayList<>();
    SessionManager session;
    String semester_number;
    ProgressBar pBar;

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
        setContentView(R.layout.activity_display_class);

        semester_number = getIntent().getStringExtra("semester_number");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Class-Student Allocation");

        pBar = (ProgressBar) findViewById(R.id.progressBar);

        session = new SessionManager(getApplicationContext());

        new getClass().execute();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ClassDisplayAdapter(classList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Class faculty = classList.get(position);
                Intent i = new Intent(DisplayClassActivity.this, StudentClassActivity.class);
                i.putExtra("class_id", faculty.getId());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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

    private class getClass extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            classList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mAdapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<>();
            data.put("branch_code", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));
            data.put("semester_number", semester_number);

            JSON_STRING = rh.sendPostRequest(Config.GET_CLASS, data);

            try {
                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("class");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("class_name");
                    String code = jo.getString("class_id");

                    Class faculty = new Class(name, code);
                    classList.add(faculty);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
