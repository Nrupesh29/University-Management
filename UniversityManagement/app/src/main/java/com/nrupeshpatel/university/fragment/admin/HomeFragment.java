package com.nrupeshpatel.university.fragment.admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.nrupeshpatel.university.MainActivity;
import com.nrupeshpatel.university.R;
import com.nrupeshpatel.university.adapter.Branch;
import com.nrupeshpatel.university.adapter.BranchRecentAdapter;
import com.nrupeshpatel.university.adapter.FacultyRecentAdapter;
import com.nrupeshpatel.university.adapter.SubjectRecentAdapter;
import com.nrupeshpatel.university.helper.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static TextView branchTotal, facultyTotal, subjectTotal;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Branch> branchList = new ArrayList<>();
    private RecyclerView recyclerView, recyclerView2;
    private static BranchRecentAdapter mAdapter;
    private static FacultyRecentAdapter mAdapter2;
    private static SubjectRecentAdapter mAdapter3;
    static ProgressBar pBar;
    static LinearLayout contentMain;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        branchTotal = (TextView) v.findViewById(R.id.totalBranch);
        facultyTotal = (TextView) v.findViewById(R.id.totalHOD);
        subjectTotal = (TextView) v.findViewById(R.id.totalSubject);
        TextView hodTotal = (TextView) v.findViewById(R.id.totalHOD);
        TextView viewBranch = (TextView) v.findViewById(R.id.viewBranch);
        TextView viewHOD = (TextView) v.findViewById(R.id.viewHOD);
        TextView viewSubject = (TextView) v.findViewById(R.id.viewSubject);
        contentMain = (LinearLayout) v.findViewById(R.id.mainContent);
        pBar = (ProgressBar) v.findViewById(R.id.progressBar);

        branchTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(1, true);
            }
        });
        viewBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(1, true);
            }
        });
        hodTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(3, true);
            }
        });
        viewHOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(3, true);
            }
        });
        subjectTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(2, true);
            }
        });
        viewSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(2, true);
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mAdapter = new BranchRecentAdapter(MainActivity.recentBranchList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        recyclerView2 = (RecyclerView) v.findViewById(R.id.recycler_view2);
        mAdapter2 = new FacultyRecentAdapter(MainActivity.recentFacultyList);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView2.setAdapter(mAdapter2);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view3);
        mAdapter3 = new SubjectRecentAdapter(MainActivity.recentSubjectList);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager3);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter3);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static void setData() {
        subjectTotal.setText(MainActivity.subject_total);
        branchTotal.setText(MainActivity.branch_total);
        facultyTotal.setText(MainActivity.faculty_total);
        mAdapter.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
        mAdapter3.notifyDataSetChanged();
        pBar.setVisibility(View.INVISIBLE);
        contentMain.setVisibility(View.VISIBLE);
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
