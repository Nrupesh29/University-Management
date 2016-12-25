package com.nrupeshpatel.university.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.nrupeshpatel.university.R;

public class BranchRecentAdapter extends RecyclerView.Adapter<BranchRecentAdapter.MyViewHolder> {

    private List<Branch> branchList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, code;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            code = (TextView) view.findViewById(R.id.code);
        }
    }


    public BranchRecentAdapter(List<Branch> branchList) {
        this.branchList = branchList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.branch_recent_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        holder.title.setText(branch.getTitle());
        holder.code.setText(branch.getCode());
    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }
}