package com.nrupeshpatel.university;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.activity.admin.SubjectActivity;
import com.nrupeshpatel.university.adapter.Branch;
import com.nrupeshpatel.university.adapter.Faculty;
import com.nrupeshpatel.university.adapter.Subject;
import com.nrupeshpatel.university.fragment.admin.BranchFragment;
import com.nrupeshpatel.university.fragment.admin.HODFragment;
import com.nrupeshpatel.university.fragment.admin.HomeFragment;
import com.nrupeshpatel.university.fragment.admin.SubjectFragment;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BranchFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, HODFragment.OnFragmentInteractionListener, SubjectFragment.OnFragmentInteractionListener {

    FloatingActionsMenu menu;
    String credits, branch_name, branch_code, subject_name, subject_code, branch_semesters;
    String faculty_name, faculty_dob, faculty_phone, faculty_joining, faculty_email, faculty_address, faculty_department, faculty_designation;
    ProgressDialog loading;
    private TabLayout tabLayout;
    AlertDialog alert;
    public static String subject_total;
    public static List<Subject> subjectList = new ArrayList<>();
    public static List<Subject> recentSubjectList = new ArrayList<>();
    public static ViewPager viewPager;
    public static String branch_total;
    public static String faculty_total;
    public static List<Branch> branchList = new ArrayList<>();
    public static List<Faculty> facultyList = new ArrayList<>();
    public static List<Branch> recentBranchList = new ArrayList<>();
    public static List<Faculty> recentFacultyList = new ArrayList<>();
    static List<String> branchSelectList = new ArrayList<String>();
    Spinner department;
    int[] icon = {R.drawable.ic_tab_home, R.drawable.ic_tab_branch, R.drawable.ic_tab_subject, R.drawable.ic_tab_hod};
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(icon[0]);
        tabLayout.getTabAt(1).setIcon(icon[1]);
        tabLayout.getTabAt(2).setIcon(icon[2]);
        tabLayout.getTabAt(3).setIcon(icon[3]);
    }

    private void setupViewPager(ViewPager paramViewPager) {
        ViewPagerAdapter viewPager = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.addFragment(new HomeFragment(), "Home");
        viewPager.addFragment(new BranchFragment(), "Branch");
        viewPager.addFragment(new SubjectFragment(), "Subject");
        viewPager.addFragment(new HODFragment(), "Faculty");
        paramViewPager.setAdapter(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.getTabAt(1).select();
        setupTabIcons();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final FloatingActionButton addSubject = (FloatingActionButton) findViewById(R.id.addSubject);
        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu.collapse();

                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.popup_add_subject, null);

                alert = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle)
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
                                EditText code = (EditText) v.findViewById(R.id.code);
                                EditText credit = (EditText) v.findViewById(R.id.Credit);
                                subject_name = name.getText().toString().trim();
                                subject_code = code.getText().toString().trim();
                                credits = credit.getText().toString().trim();
                                if (!subject_name.isEmpty() && !subject_code.isEmpty() && !credits.isEmpty()) {
                                    alert.dismiss();
                                    new AddSubject().execute();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Enter details!!", Toast.LENGTH_SHORT).show();
                                }
                                //alert.dismiss();
                            }
                        });
                    }
                });

                alert.show();
            }
        });

        final FloatingActionButton addBranch = (FloatingActionButton) findViewById(R.id.addBranch);
        addBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu.collapse();

                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.popup_add_branch, null);

                alert = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle)
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
                                EditText code = (EditText) v.findViewById(R.id.code);
                                Spinner semester = (Spinner) v.findViewById(R.id.spinnerSemesters);
                                branch_name = name.getText().toString().trim();
                                branch_code = code.getText().toString().trim();
                                branch_semesters = semester.getSelectedItem().toString();
                                if (!branch_name.isEmpty() && !branch_code.isEmpty() && !branch_semesters.isEmpty()) {
                                    alert.dismiss();
                                    new AddBranch().execute();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Enter details!!", Toast.LENGTH_SHORT).show();
                                }
                                //alert.dismiss();
                            }
                        });
                    }
                });

                alert.show();
            }
        });

        FloatingActionButton addFaculty = (FloatingActionButton) findViewById(R.id.addFaculty);
        addFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu.collapse();

                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.popup_add_faculty, null);

                department = (Spinner) v.findViewById(R.id.spinnerDepartment);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, branchSelectList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                department.setAdapter(dataAdapter);


                final EditText dob = (EditText) v.findViewById(R.id.dob);
                dob.setEnabled(false);
                final EditText joining = (EditText) v.findViewById(R.id.joiningDate);
                joining.setEnabled(false);
                ImageView dateDOB = (ImageView) v.findViewById(R.id.dateDOB);
                ImageView dateJoining = (ImageView) v.findViewById(R.id.dateJoining);
                final RadioButton hod = (RadioButton) v.findViewById(R.id.radioHOD);
                final RadioButton faculty = (RadioButton) v.findViewById(R.id.radioFaculty);
                dateDOB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myCalendar = Calendar.getInstance();

                        date = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String displayDate = df.format(myCalendar.getTime());
                                dob.setText(displayDate);
                            }

                        };

                        new DatePickerDialog(MainActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                dateJoining.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myCalendar = Calendar.getInstance();

                        date = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String displayDate = df.format(myCalendar.getTime());
                                joining.setText(displayDate);
                            }

                        };

                        new DatePickerDialog(MainActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                alert = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle)
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
                                EditText dob = (EditText) v.findViewById(R.id.dob);
                                EditText phone = (EditText) v.findViewById(R.id.phone);
                                EditText joining = (EditText) v.findViewById(R.id.joiningDate);
                                EditText email = (EditText) v.findViewById(R.id.email);
                                EditText address = (EditText) v.findViewById(R.id.address);
                                if (hod.isChecked()) {
                                    faculty_designation = "Head of department";
                                } else if (faculty.isChecked()) {
                                    faculty_designation = "Faculty";
                                }
                                faculty_name = name.getText().toString().trim();
                                faculty_dob = dob.getText().toString().trim();
                                faculty_phone = phone.getText().toString().trim();
                                faculty_joining = joining.getText().toString().trim();
                                faculty_email = email.getText().toString().trim();
                                faculty_address = address.getText().toString().trim();
                                faculty_department = department.getSelectedItem().toString();
                                if (!faculty_name.isEmpty() && !faculty_dob.isEmpty() && !faculty_phone.isEmpty() && !faculty_joining.isEmpty() && !faculty_email.isEmpty() && !faculty_address.isEmpty() && !faculty_department.isEmpty() && !faculty_designation.isEmpty()) {
                                    alert.dismiss();
                                    new AddFaculty().execute();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Enter details!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                alert.show();
            }
        });

        new getData().execute();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (menu.isExpanded()) {
            menu.collapse();
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
            final SessionManager session = new SessionManager(MainActivity.this);
            new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle)
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
            // Handle the camera action
        } else if (id == R.id.nav_units) {
            startActivity(new Intent(MainActivity.this, SubjectActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AddBranch extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, null, "Adding Branch...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new getData().execute();
                Toast.makeText(getApplicationContext(), "Branch Added!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("name", branch_name);
            data.put("code", branch_code);
            data.put("semester", branch_semesters);

            JSON_STRING = rh.sendPostRequest(Config.ADD_BRANCH, data);

            return JSON_STRING;
        }
    }

    private class AddSubject extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, null, "Adding Subject...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new getData().execute();
                Toast.makeText(getApplicationContext(), "Subject Added!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("name", subject_name);
            data.put("code", subject_code);
            data.put("credits", credits);

            JSON_STRING = rh.sendPostRequest(Config.ADD_SUBJECT, data);

            return JSON_STRING;
        }
    }

    private class AddFaculty extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, null, "Adding Faculty...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new getData().execute();
                Toast.makeText(getApplicationContext(), "Faculty Added!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("name", faculty_name);
            data.put("dob", faculty_dob);
            data.put("phone", faculty_phone);
            data.put("joining", faculty_joining);
            data.put("email", faculty_email);
            data.put("address", faculty_address);
            data.put("designation", faculty_designation);
            data.put("department", faculty_department);

            JSON_STRING = rh.sendPostRequest(Config.ADD_FACULTY, data);

            return JSON_STRING;
        }
    }

    public static class getData extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            subjectList.clear();
            recentSubjectList.clear();
            branchList.clear();
            recentBranchList.clear();
            facultyList.clear();
            recentFacultyList.clear();
            branchSelectList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            HomeFragment.setData();
            BranchFragment.setData();
            HODFragment.setData();
            SubjectFragment.setData();
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            JSON_STRING = rh.sendGetRequest(Config.ADMIN_DATA);

            Log.i("JSON", JSON_STRING);

            try {

                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("subject");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("subject_name");
                    String code = jo.getString("subject_code");
                    String credit = jo.getString("subject_credit");

                    Subject subject = new Subject(name, code, credit);
                    subjectList.add(subject);
                }

                result = jsonObject.getJSONArray("subject_total");
                JSONObject jo = result.getJSONObject(0);
                subject_total = jo.getString("total");

                result = jsonObject.getJSONArray("recent_subject");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject j = result.getJSONObject(i);
                    String name = j.getString("subject_name");
                    String code = j.getString("subject_code");

                    Subject subject = new Subject(name, code);
                    recentSubjectList.add(subject);
                }

                result = jsonObject.getJSONArray("branch");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject joj = result.getJSONObject(i);
                    String name = joj.getString("branch_name");
                    String code = joj.getString("branch_code");
                    String semester = joj.getString("branch_semester");

                    Branch branch = new Branch(name, code, semester);
                    branchList.add(branch);
                }

                result = jsonObject.getJSONArray("branch_total");
                JSONObject jojj = result.getJSONObject(0);
                branch_total = jojj.getString("total");

                result = jsonObject.getJSONArray("recent_branch");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject j = result.getJSONObject(i);
                    String name = j.getString("branch_name");
                    String code = j.getString("branch_code");

                    Branch branch = new Branch(name, code);
                    recentBranchList.add(branch);
                }

                result = jsonObject.getJSONArray("hod");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jjj = result.getJSONObject(i);
                    String name = jjj.getString("name");
                    String code = jjj.getString("id");

                    Faculty faculty = new Faculty(name, code);
                    facultyList.add(faculty);
                }

                result = jsonObject.getJSONArray("hod_total");
                JSONObject a = result.getJSONObject(0);
                faculty_total = a.getString("total");

                result = jsonObject.getJSONArray("recent_hod");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject j = result.getJSONObject(i);
                    String name = j.getString("name");
                    String code = j.getString("id");

                    Faculty faculty = new Faculty(name, code);
                    recentFacultyList.add(faculty);
                }

                result = jsonObject.getJSONArray("branch_hod");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject j = result.getJSONObject(i);
                    String name = j.getString("branch_name");
                    String code = j.getString("branch_code");

                    branchSelectList.add(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
