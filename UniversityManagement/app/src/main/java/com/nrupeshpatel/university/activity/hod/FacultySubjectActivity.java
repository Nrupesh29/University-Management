package com.nrupeshpatel.university.activity.hod;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Subject;
import com.nrupeshpatel.university.fragment.hod.FacultyAllocatedFragment;
import com.nrupeshpatel.university.fragment.hod.FacultyAvailableFragment;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class FacultySubjectActivity extends AppCompatActivity implements FacultyAvailableFragment.OnFragmentInteractionListener, FacultyAllocatedFragment.OnFragmentInteractionListener{

    ViewPager viewPager;
    TabLayout tabLayout;
    int[] icon = {R.drawable.ic_tab_subject, R.drawable.ic_tab_hod};
    public static String semester_number;
    public static String faculty_code;
    static SessionManager session;
    public static ArrayList<Subject> subjectList = new ArrayList<>();
    public static ArrayList<Subject> subjectAllocatedList = new ArrayList<>();

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
    public void onFragmentInteraction(Uri uri) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(icon[0]);
        tabLayout.getTabAt(1).setIcon(icon[1]);
    }

    private void setupViewPager(ViewPager paramViewPager) {
        ViewPagerAdapter viewPager = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.addFragment(new FacultyAllocatedFragment(), "Allocated");
        viewPager.addFragment(new FacultyAvailableFragment(), "Available");
        paramViewPager.setAdapter(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_subject);

        semester_number = getIntent().getStringExtra("semester_number");
        faculty_code = getIntent().getStringExtra("faculty_code");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        session = new SessionManager(getApplicationContext());

        new getSubjects().execute();

    }

    public static class getSubjects extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            subjectList.clear();
            subjectAllocatedList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            FacultyAvailableFragment.setData();
            FacultyAllocatedFragment.setData();
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("branch_code", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));
            data.put("semester_number", semester_number);
            data.put("faculty_code", faculty_code);

            JSON_STRING = rh.sendPostRequest(Config.GET_FACULTY_SUBJECTS, data);

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

                result = jsonObject.getJSONArray("subject_allocated");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("subject_name");
                    String code = jo.getString("subject_code");

                    Subject subject = new Subject(name, code);
                    subjectAllocatedList.add(subject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
