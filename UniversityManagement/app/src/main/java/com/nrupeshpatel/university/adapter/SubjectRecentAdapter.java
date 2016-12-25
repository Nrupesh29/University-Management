package com.nrupeshpatel.university.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.nrupeshpatel.university.R;

public class SubjectRecentAdapter extends RecyclerView.Adapter<SubjectRecentAdapter.MyViewHolder> {

    private List<Subject> subjectList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, code;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            code = (TextView) view.findViewById(R.id.code);
        }
    }


    public SubjectRecentAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_recent_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.title.setText(subject.getTitle());
        holder.code.setText(subject.getCode());
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }
}