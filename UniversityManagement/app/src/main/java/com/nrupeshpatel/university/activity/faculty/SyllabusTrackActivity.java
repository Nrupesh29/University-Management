package com.nrupeshpatel.university.activity.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.ExpandableListAdapter;
import com.nrupeshpatel.university.adapter.ExpandableListTrackAdapter;
import com.nrupeshpatel.university.adapter.Unit;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;

public class SyllabusTrackActivity extends AppCompatActivity {

    ExpandableListTrackAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Unit>> listDataChild;
    List<String> child = new ArrayList<>();
    String subject_code, class_id;
    public static List<Unit> unitList = new ArrayList<>();
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_track);

        subject_code = getIntent().getStringExtra("subject_code");
        class_id = getIntent().getStringExtra("class_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Syllabus Tracking");

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        new getUnits().execute();
    }

    private class getUnits extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (listDataHeader.isEmpty()) {
                expListView.setVisibility(View.INVISIBLE);
            } else {
                listAdapter = new ExpandableListTrackAdapter(SyllabusTrackActivity.this, listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
                expListView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<>();
            data.put("subject_code", subject_code);
            data.put("class_id", class_id);

            JSON_STRING = rh.sendPostRequest(Config.SYLLABUS_TRACK, data);
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<Unit>>();
            int header = -1;

            try {
                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("unit");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("unit_name");
                    String topic = jo.getString("sub_topic");
                    String id = jo.getString("unit_id");
                    String status = jo.getString("status");

                    Unit unit = new Unit(name, topic, id, status);

                    if (listDataHeader.contains(name)) {
                        unitList.add(unit);
                    } else {
                        unitList = new ArrayList<>();
                        listDataHeader.add(name);
                        unitList.add(unit);
                        header++;
                    }

                    listDataChild.put(listDataHeader.get(header), unitList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class Track extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(SyllabusTrackActivity.this, null, "Tracking Syllabus...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                Intent i = new Intent(SyllabusTrackActivity.this, AttendaceActivity.class);
                i.putExtra("subject_code", subject_code);
                i.putExtra("class_id", class_id);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();

            for (int i = 0; i<ExpandableListAdapter.completed.size(); i++) {
                Unit unit = ExpandableListAdapter.completed.get(i);
                data.put("id", unit.getCode());
                data.put("class_id", class_id);
                JSON_STRING = rh.sendPostRequest(Config.TRACK, data);
            }

            return JSON_STRING;
        }
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
}
