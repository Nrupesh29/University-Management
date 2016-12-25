package com.nrupeshpatel.university.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.nrupeshpatel.university.R;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.MyViewHolder> {

    private List<Semester> semesterList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }


    public SemesterAdapter(List<Semester> semesterList) {
        this.semesterList = semesterList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.semester_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Semester semester = semesterList.get(position);
        holder.title.setText("Semester "+semester.getTitle());
    }

    @Override
    public int getItemCount() {
        return semesterList.size();
    }
}