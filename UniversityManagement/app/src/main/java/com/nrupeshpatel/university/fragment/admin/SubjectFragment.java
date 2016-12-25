package com.nrupeshpatel.university.fragment.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nrupeshpatel.university.MainActivity;
import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Subject;
import com.nrupeshpatel.university.adapter.SubjectAdapter;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;

public class SubjectFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Subject> subjectList = new ArrayList<>();
    static TextView subjectTotal;
    private RecyclerView recyclerView;
    private static SubjectAdapter mAdapter;
    static ProgressBar pBar;
    static LinearLayout contentMain;
    ProgressDialog loading;
    String subject_code, subjectName, subjectCode, subjectCredits, oldSubjectCode;
    AlertDialog alert;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SubjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubjectFragment newInstance(String param1, String param2) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View main = inflater.inflate(R.layout.fragment_subject, container, false);

        subjectTotal = (TextView) main.findViewById(R.id.totalSubjects);
        contentMain = (LinearLayout) main.findViewById(R.id.mainContent);
        pBar = (ProgressBar) main.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) main.findViewById(R.id.recycler_view);
        mAdapter = new SubjectAdapter(MainActivity.subjectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Subject subject = MainActivity.subjectList.get(position);
                subject_code = subject.getCode();
                ImageView delete = (ImageView) view.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                                .setTitle("Delete?")
                                .setMessage("Are you sure you want to delete this?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new DeleteSubject().execute();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create()
                                .show();
                    }
                });
                ImageView edit = (ImageView) view.findViewById(R.id.edit);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View vv = inflater.inflate(R.layout.popup_update_subject, null);

                        alert = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                                .setView(vv)
                                .setCancelable(false)
                                .setPositiveButton("Update", null)
                                .setNegativeButton("Cancel", null)
                                .create();

                        alert.setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(final DialogInterface dialog) {

                                EditText name = (EditText) vv.findViewById(R.id.name);
                                EditText code = (EditText) vv.findViewById(R.id.code);
                                EditText credit = (EditText) vv.findViewById(R.id.Credit);

                                name.setText(subject.getTitle());
                                code.setText(subject.getCode());
                                credit.setText(subject.getCredit());

                                Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                                b.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        // TODO Do something
                                        EditText name = (EditText) vv.findViewById(R.id.name);
                                        EditText code = (EditText) vv.findViewById(R.id.code);
                                        EditText credit = (EditText) vv.findViewById(R.id.Credit);
                                        subjectName = name.getText().toString().trim();
                                        subjectCode = code.getText().toString().trim();
                                        subjectCredits = credit.getText().toString().trim();
                                        oldSubjectCode = subject.getCode();
                                        if (!subjectName.isEmpty() && !subjectCode.isEmpty() && !subjectCredits.isEmpty()) {
                                            alert.dismiss();
                                            new AddSubject().execute();
                                        } else {
                                            Toast.makeText(getActivity(), "Enter details!!", Toast.LENGTH_SHORT).show();
                                        }
                                        //alert.dismiss();
                                    }
                                });
                            }
                        });

                        alert.show();
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return main;
    }

    public static void setData() {
        subjectTotal.setText(MainActivity.subject_total);
        mAdapter.notifyDataSetChanged();
        pBar.setVisibility(View.INVISIBLE);
        contentMain.setVisibility(View.VISIBLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class DeleteSubject extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), null, "Removing Subject...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new MainActivity.getData().execute();
                Toast.makeText(getActivity().getApplicationContext(), "Subject Removed!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("subject_code", subject_code);

            JSON_STRING = rh.sendPostRequest(Config.REMOVE_SUBJECT, data);

            return JSON_STRING;
        }
    }

    private class AddSubject extends AsyncTask<String, Void, String> {

        String JSON_STRING = null;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), null, "Updating Subject...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new MainActivity.getData().execute();
                Toast.makeText(getActivity(), "Subject Updated!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            RequestHandler rh = new RequestHandler();

            HashMap<String, String> data = new HashMap<>();
            data.put("name", subjectName);
            data.put("code", subjectCode);
            data.put("old_code", oldSubjectCode);
            data.put("credits", subjectCredits);

            JSON_STRING = rh.sendPostRequest(Config.UPDATE_SUBJECT, data);

            return JSON_STRING;
        }
    }
}
