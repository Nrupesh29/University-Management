package com.nrupeshpatel.university.activity.admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.ExpandableListAdapter;
import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;

public class UnitActivity extends AppCompatActivity {

    FloatingActionsMenu menu;
    String subject_code, unit_name, sub_topics;
    AlertDialog alert;
    ProgressDialog loading;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ProgressBar pBar;
    LinearLayout noData;
    List<String> child = new ArrayList<>();
    List<String> subList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        subject_code = getIntent().getStringExtra("subject_code");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        menu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        noData = (LinearLayout) findViewById(R.id.noDataFound);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        new getUnits().execute();

        final FloatingActionButton addUnit = (FloatingActionButton) findViewById(R.id.addUnit);
        addUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu.collapse();

                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.popup_add_unit, null);

                alert = new AlertDialog.Builder(UnitActivity.this, R.style.AppCompatAlertDialogStyle)
                        .setView(v)
                        .setCancelable(false)
                        .setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", null)
                        .create();

                alert.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something
                                EditText name = (EditText) v.findViewById(R.id.name);
                                EditText topics = (EditText) v.findViewById(R.id.topics);
                                unit_name = name.getText().toString().trim();
                                sub_topics = topics.getText().toString().trim();
                                subList = Arrays.asList(sub_topics.split(","));
                                if (!unit_name.isEmpty() && !sub_topics.isEmpty()) {
                                    alert.dismiss();
                                    new AddUnit().execute();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Enter Details!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                alert.show();
            }
        });
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

    private class AddUnit extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(UnitActivity.this, null, "Adding Unit...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new getUnits().execute();
                Toast.makeText(getApplicationContext(), "Unit Added!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();

            for (int j = 0; j < subList.size(); j++) {
                data.put("name", unit_name);
                data.put("sub_topic", subList.get(j).trim());
                data.put("subject_code", subject_code);

                JSON_STRING = rh.sendPostRequest(Config.ADD_UNIT, data);
            }

            return JSON_STRING;
        }
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
                noData.setVisibility(View.VISIBLE);
                expListView.setVisibility(View.INVISIBLE);
                pBar.setVisibility(View.INVISIBLE);
            } else {
                listAdapter = new ExpandableListAdapter(UnitActivity.this, listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
                pBar.setVisibility(View.INVISIBLE);
                expListView.setVisibility(View.VISIBLE);
                noData.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<>();
            data.put("subject_code", subject_code);

            JSON_STRING = rh.sendPostRequest(Config.GET_UNIT, data);
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();
            int header = -1;

            try {
                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("unit");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("unit_name");
                    String topic = jo.getString("sub_topic");
                    //List<String> subList = Arrays.asList(topic.split(","));

                    if (listDataHeader.contains(name)) {
                        child.add(topic);
                    } else {
                        child = new ArrayList<String>();
                        listDataHeader.add(name);
                        child.add(topic);
                        header++;
                    }

                    listDataChild.put(listDataHeader.get(header), child);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
