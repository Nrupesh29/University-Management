package com.nrupeshpatel.university.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.nrupeshpatel.university.R;

public class FacultyRecentAdapter extends RecyclerView.Adapter<FacultyRecentAdapter.MyViewHolder> {

    private List<Faculty> facultyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }


    public FacultyRecentAdapter(List<Faculty> facultyList) {
        this.facultyList = facultyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faculty_recent_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Faculty faculty = facultyList.get(position);
        holder.title.setText(faculty.getTitle());
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }
}