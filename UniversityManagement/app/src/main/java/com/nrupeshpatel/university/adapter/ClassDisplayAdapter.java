package com.nrupeshpatel.university.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.nrupeshpatel.university.R;

public class ClassDisplayAdapter extends RecyclerView.Adapter<ClassDisplayAdapter.MyViewHolder> {

    private List<Class> classList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, semester;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            semester = (TextView) view.findViewById(R.id.semester);
        }
    }


    public ClassDisplayAdapter(List<Class> classList) {
        this.classList = classList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_display_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Class branch = classList.get(position);
        holder.title.setText(branch.getTitle());
        holder.semester.setText(branch.getSemester());
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }
}