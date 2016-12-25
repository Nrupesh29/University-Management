package com.nrupeshpatel.university.activity.hod;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Faculty;
import com.nrupeshpatel.university.fragment.hod.StudentAllocatedFragment;
import com.nrupeshpatel.university.fragment.hod.StudentAvailableFragment;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;

public class StudentClassActivity extends AppCompatActivity implements StudentAvailableFragment.OnFragmentInteractionListener, StudentAllocatedFragment.OnFragmentInteractionListener{

    ViewPager viewPager;
    TabLayout tabLayout;
    int[] icon = {R.drawable.ic_tab_subject, R.drawable.ic_tab_hod};
    public static String class_id;
    public static ArrayList<Faculty> studentList = new ArrayList<>();
    public static ArrayList<Faculty> studentAllocatedList = new ArrayList<>();

    @Override
    public void onFragmentInteraction(Uri uri) {

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
        viewPager.addFragment(new StudentAllocatedFragment(), "Allocated");
        viewPager.addFragment(new StudentAvailableFragment(), "Available");
        paramViewPager.setAdapter(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class);

        class_id = getIntent().getStringExtra("class_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Class-Student Allocation");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        new getStudents().execute();

    }

    public static class getStudents extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            studentList.clear();
            studentAllocatedList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            StudentAvailableFragment.setData();
            StudentAllocatedFragment.setData();
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("class_id", class_id);

            JSON_STRING = rh.sendPostRequest(Config.GET_STUDENTS, data);

            try {

                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("student");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("student_name");
                    String code = jo.getString("student_enrollment");

                    Faculty student = new Faculty(name, code);
                    studentList.add(student);
                }

                result = jsonObject.getJSONArray("student_allocated");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("student_name");
                    String code = jo.getString("student_enrollment");

                    Faculty student = new Faculty(name, code);
                    studentAllocatedList.add(student);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
