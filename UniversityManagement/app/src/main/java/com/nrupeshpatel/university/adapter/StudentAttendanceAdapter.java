package com.nrupeshpatel.university.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.nrupeshpatel.university.R;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.MyViewHolder> {

    private List<Faculty> facultyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, code;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            code = (TextView) view.findViewById(R.id.code);
        }
    }


    public StudentAttendanceAdapter(List<Faculty> facultyList) {
        this.facultyList = facultyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_attendance_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Faculty faculty = facultyList.get(position);
        holder.title.setText(faculty.getTitle());
        holder.code.setText(faculty.getCode());
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }
}