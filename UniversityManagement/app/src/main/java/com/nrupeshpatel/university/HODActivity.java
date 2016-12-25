package com.nrupeshpatel.university;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import com.nrupeshpatel.university.activity.hod.DisplayFacultyActivity;
import com.nrupeshpatel.university.activity.hod.DisplaySemesterActivity;
import com.nrupeshpatel.university.adapter.Faculty;
import com.nrupeshpatel.university.adapter.Class;
import com.nrupeshpatel.university.fragment.hod.HomeFragment;
import com.nrupeshpatel.university.fragment.hod.ClassFragment;
import com.nrupeshpatel.university.fragment.hod.StudentFragment;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

public class HODActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, StudentFragment.OnFragmentInteractionListener, ClassFragment.OnFragmentInteractionListener {

    ProgressDialog loading;
    AlertDialog alert;
    public static String class_total, student_total;
    public static ViewPager viewPager;
    private TabLayout tabLayout;
    List<String> semesterList = new ArrayList<String>();
    String class_name, students_number, branch_semesters, student_name, student_dob, student_phone, student_enroll, student_email, student_address, parent_phone;
    static SessionManager session;
    Calendar myCalendar;
    FloatingActionsMenu menu;
    int[] icon = {R.drawable.ic_tab_home, R.drawable.ic_tab_branch, R.drawable.ic_tab_hod};
    DatePickerDialog.OnDateSetListener date;

    public static List<Class> classList = new ArrayList<>();
    public static List<Class> recentClassList = new ArrayList<>();
    public static List<Faculty> studentList = new ArrayList<>();
    public static List<Faculty> recentStudentList = new ArrayList<>();

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
        tabLayout.getTabAt(2).setIcon(icon[2]);
    }

    private void setupViewPager(ViewPager paramViewPager) {
        ViewPagerAdapter viewPager = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.addFragment(new HomeFragment(), "Home");
        viewPager.addFragment(new ClassFragment(), "Class");
        viewPager.addFragment(new StudentFragment(), "Student");
        paramViewPager.setAdapter(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hod);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());

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
        viewPager.setOffscreenPageLimit(3);
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

        for (int i = 1 ; i<=Integer.parseInt(session.getUserDetails().get(SessionManager.KEY_SEMESTER)); i++){
            semesterList.add(String.valueOf(i));
        }

        final FloatingActionButton addBranch = (FloatingActionButton) findViewById(R.id.addClass);
        addBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu.collapse();

                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.popup_add_class, null);

                alert = new AlertDialog.Builder(HODActivity.this, R.style.AppCompatAlertDialogStyle)
                        .setView(v)
                        .setCancelable(false)
                        .setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", null)
                        .create();

                final Spinner semester = (Spinner) v.findViewById(R.id.spinnerSemesters);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HODActivity.this, android.R.layout.simple_spinner_item, semesterList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                semester.setAdapter(dataAdapter);

                alert.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something
                                EditText name = (EditText) v.findViewById(R.id.name);
                                EditText code = (EditText) v.findViewById(R.id.studentsNumber);
                                class_name = name.getText().toString().trim();
                                students_number = code.getText().toString().trim();
                                branch_semesters = semester.getSelectedItem().toString();
                                if (!class_name.isEmpty() && !students_number.isEmpty() && !branch_semesters.isEmpty()) {
                                    alert.dismiss();
                                    new AddClass().execute();
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

        FloatingActionButton addFaculty = (FloatingActionButton) findViewById(R.id.addStudent);
        addFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu.collapse();

                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.popup_add_student, null);

                final EditText dob = (EditText) v.findViewById(R.id.dob);
                dob.setEnabled(false);
                ImageView dateDOB = (ImageView) v.findViewById(R.id.dateDOB);
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

                        new DatePickerDialog(HODActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                alert = new AlertDialog.Builder(HODActivity.this, R.style.AppCompatAlertDialogStyle)
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
                                EditText studentPhone = (EditText) v.findViewById(R.id.studentPhone);
                                EditText parentPhone = (EditText) v.findViewById(R.id.parentPhone);
                                EditText enrollment = (EditText) v.findViewById(R.id.enroll);
                                EditText email = (EditText) v.findViewById(R.id.email);
                                EditText address = (EditText) v.findViewById(R.id.address);
                                student_name = name.getText().toString().trim();
                                student_dob = dob.getText().toString().trim();
                                student_phone = studentPhone.getText().toString().trim();
                                parent_phone = parentPhone.getText().toString().trim();
                                student_enroll = enrollment.getText().toString().trim();
                                student_email = email.getText().toString().trim();
                                student_address = address.getText().toString().trim();
                                if (!student_name.isEmpty() && !student_dob.isEmpty() && !student_phone.isEmpty() && !student_enroll.isEmpty() && !student_email.isEmpty() && !student_address.isEmpty() && !parent_phone.isEmpty()) {
                                    alert.dismiss();
                                    new AddStudent().execute();
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
            final SessionManager session = new SessionManager(HODActivity.this);
            new AlertDialog.Builder(HODActivity.this, R.style.AppCompatAlertDialogStyle)
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

        if (id == R.id.nav_branchSubject) {
            Intent i = new Intent(HODActivity.this, DisplaySemesterActivity.class);
            i.putExtra("type", "branch_subject");
            startActivity(i);
        } else if (id == R.id.nav_facultySubject) {
            Intent i = new Intent(HODActivity.this, DisplayFacultyActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_classStudent) {
            Intent i = new Intent(HODActivity.this, DisplaySemesterActivity.class);
            i.putExtra("type", "class_student");
            i.putExtra("CLASS", "CLASS");
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AddClass extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(HODActivity.this, null, "Adding Class...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new getData().execute();
                Toast.makeText(getApplicationContext(), "Class Added!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("name", class_name);
            data.put("branch_code", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));
            data.put("number_students", students_number);
            data.put("semester", branch_semesters);

            JSON_STRING = rh.sendPostRequest(Config.ADD_CLASS, data);

            return JSON_STRING;
        }
    }

    private class AddStudent extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(HODActivity.this, null, "Adding Student...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new getData().execute();
                Toast.makeText(getApplicationContext(), "Student Added!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("name", student_name);
            data.put("dob", student_dob);
            data.put("studentPhone", student_phone);
            data.put("enrollment", student_enroll);
            data.put("email", student_email);
            data.put("address", student_address);
            data.put("parentPhone", parent_phone);
            data.put("department", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));

            JSON_STRING = rh.sendPostRequest(Config.ADD_STUDENT, data);

            return JSON_STRING;
        }
    }

    public static class getData extends AsyncTask<String, Void, Object> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            classList.clear();
            recentClassList.clear();
            studentList.clear();
            recentStudentList.clear();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            HomeFragment.setData();
            ClassFragment.setData();
            StudentFragment.setData();
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("branch_code", session.getUserDetails().get(SessionManager.KEY_DEPARTMENT));
            JSON_STRING = rh.sendPostRequest(Config.HOD_DATA, data);

            Log.i("JSON", JSON_STRING);

            try {

                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = jsonObject.getJSONArray("class");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String name = jo.getString("class_name");
                    String code = jo.getString("class_id");
                    String semester = jo.getString("semester_number");
                    String students = jo.getString("students_number");

                    Class subject = new Class(name, code, semester, students);
                    classList.add(subject);
                }

                result = jsonObject.getJSONArray("class_total");
                JSONObject jo = result.getJSONObject(0);
                class_total = jo.getString("total");

                result = jsonObject.getJSONArray("recent_class");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject j = result.getJSONObject(i);
                    String name = j.getString("class_name");
                    String code = j.getString("class_id");
                    String semester = j.getString("semester_number");
                    String students = j.getString("students_number");

                    Class subject = new Class(name, code, semester, students);
                    recentClassList.add(subject);
                }

                result = jsonObject.getJSONArray("student");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jjj = result.getJSONObject(i);
                    String name = jjj.getString("name");

                    Faculty faculty = new Faculty(name, "");
                    studentList.add(faculty);
                }

                result = jsonObject.getJSONArray("student_total");
                JSONObject a = result.getJSONObject(0);
                student_total = a.getString("total");

                result = jsonObject.getJSONArray("recent_student");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject j = result.getJSONObject(i);
                    String name = j.getString("name");

                    Faculty faculty = new Faculty(name, "");
                    recentStudentList.add(faculty);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
