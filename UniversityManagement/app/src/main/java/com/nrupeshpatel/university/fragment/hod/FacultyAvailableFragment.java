package com.nrupeshpatel.university.fragment.hod;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.activity.hod.FacultySubjectActivity;
import com.nrupeshpatel.university.adapter.Subject;
import com.nrupeshpatel.university.adapter.SubjectDisplayAdapter;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;
import com.nrupeshpatel.university.helper.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FacultyAvailableFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FacultyAvailableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FacultyAvailableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    ArrayList<Subject> subjectList = new ArrayList<>();
    static SubjectDisplayAdapter mAdapter;
    ProgressDialog loading;
    public static ProgressBar pBar;
    public static LinearLayout noDataFound;
    String subject_code;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FacultyAvailableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AvailableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FacultyAvailableFragment newInstance(String param1, String param2) {
        FacultyAvailableFragment fragment = new FacultyAvailableFragment();
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

        View v = inflater.inflate(R.layout.fragment_available, container, false);

        pBar = (ProgressBar) v.findViewById(R.id.progressBar);
        noDataFound = (LinearLayout) v.findViewById(R.id.noDataFound);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mAdapter = new SubjectDisplayAdapter(FacultySubjectActivity.subjectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Subject subject = FacultySubjectActivity.subjectList.get(position);
                subject_code = subject.getCode();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Allocate " + subject.getTitle() + "!")
                        .setMessage("Do you want to allocate "+subject.getTitle()+" to your branch?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AllocateSubject().execute();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return v;
    }

    public static void setData() {
        if (FacultySubjectActivity.subjectList.isEmpty()) {
            noDataFound.setVisibility(View.VISIBLE);
        } else {
            noDataFound.setVisibility(View.INVISIBLE);
        }
        mAdapter.notifyDataSetChanged();
        pBar.setVisibility(View.INVISIBLE);
    }

    private class AllocateSubject extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), null, "Allocating Subject...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new FacultySubjectActivity.getSubjects().execute();
            } else {
                Toast.makeText(getActivity(), "Error!!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {


            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<>();
            SessionManager session = new SessionManager(getActivity().getApplicationContext());
            String branch_code = session.getUserDetails().get(SessionManager.KEY_DEPARTMENT);

            data.put("branch_code", branch_code);
            data.put("subject_code", subject_code);
            data.put("semester_number", FacultySubjectActivity.semester_number);
            data.put("faculty_code", FacultySubjectActivity.faculty_code);

            return rh.sendPostRequest(Config.ALLOCATE_SUBJECT_FACULTY, data);
        }
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
}
