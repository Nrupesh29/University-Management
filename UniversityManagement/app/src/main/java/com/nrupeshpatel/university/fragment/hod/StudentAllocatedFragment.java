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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.activity.hod.StudentClassActivity;
import com.nrupeshpatel.university.adapter.Faculty;
import com.nrupeshpatel.university.adapter.FacultyDisplayAdapter;
import com.nrupeshpatel.university.helper.Config;
import com.nrupeshpatel.university.helper.RequestHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentAllocatedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentAllocatedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentAllocatedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String student_code;
    ProgressDialog loading;
    RecyclerView recyclerView;
    static FacultyDisplayAdapter mAdapter;
    static ArrayList<Faculty> studentAllocatedList = new ArrayList<>();
    public static ProgressBar pBar;
    public static LinearLayout noDataFound;
    public static TextView text;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StudentAllocatedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllocatedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentAllocatedFragment newInstance(String param1, String param2) {
        StudentAllocatedFragment fragment = new StudentAllocatedFragment();
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

        View v = inflater.inflate(R.layout.fragment_allocated, container, false);
        pBar = (ProgressBar) v.findViewById(R.id.progressBar);
        noDataFound = (LinearLayout) v.findViewById(R.id.noDataFound);
        text = (TextView) v.findViewById(R.id.allocated);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mAdapter = new FacultyDisplayAdapter(StudentClassActivity.studentAllocatedList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Faculty student = StudentClassActivity.studentAllocatedList.get(position);
                student_code = student.getCode();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove " + student.getTitle() + "!")
                        .setMessage("Do you want to remove "+student.getTitle()+" from your branch?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new RemoveStudent().execute();
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

    private class RemoveStudent extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), null, "Removing Student...", true, true);
            loading.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equals("Successful")) {
                new StudentClassActivity.getStudents().execute();
            } else {
                Toast.makeText(getActivity(), "Error!!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {


            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<>();

            data.put("class_id", StudentClassActivity.class_id);
            data.put("student_enrollment", student_code);

            return rh.sendPostRequest(Config.REMOVE_CLASS_STUDENT, data);
        }
    }

    public static void setData() {
        if (StudentClassActivity.studentAllocatedList.isEmpty()) {
            text.setText("No Student Allocated!!");
            noDataFound.setVisibility(View.VISIBLE);
        } else {
            noDataFound.setVisibility(View.INVISIBLE);
        }
        mAdapter.notifyDataSetChanged();
        pBar.setVisibility(View.INVISIBLE);
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
