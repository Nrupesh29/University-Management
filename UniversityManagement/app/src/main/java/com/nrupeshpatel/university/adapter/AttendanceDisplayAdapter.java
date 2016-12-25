package com.nrupeshpatel.university.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.nrupeshpatel.university.R;

public class AttendanceDisplayAdapter extends RecyclerView.Adapter<AttendanceDisplayAdapter.MyViewHolder> {

    private List<Attendance> classList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, code, total, present, percent;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            code = (TextView) view.findViewById(R.id.code);
            total = (TextView) view.findViewById(R.id.total);
            present = (TextView) view.findViewById(R.id.present);
            percent = (TextView) view.findViewById(R.id.percent);
        }
    }


    public AttendanceDisplayAdapter(List<Attendance> classList) {
        this.classList = classList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_display_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Attendance attend = classList.get(position);
        holder.title.setText(attend.getName());
        holder.code.setText(attend.getCode());
        holder.total.setText(attend.getLecture());
        holder.present.setText(attend.getTotal());
        double percentage = (double) Math.round(getPercentage(Integer.parseInt(attend.getTotal()), Integer.parseInt(attend.getLecture())) * 100) / 100;
        holder.percent.setText(String.valueOf(percentage)+"%");
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static float getPercentage(int attended, int total) {
        float proportionCorrect = ((float) attended) / ((float) total);
        return proportionCorrect * 100;
    }
}