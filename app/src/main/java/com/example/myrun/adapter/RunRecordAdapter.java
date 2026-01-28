package com.example.myrun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrun.R;
import com.example.myrun.model.RunRecord;

import java.util.List;

public class RunRecordAdapter extends RecyclerView.Adapter<RunRecordAdapter.ViewHolder> {
    private List<RunRecord> recordList;
    private Context context;

    public RunRecordAdapter(List<RunRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_run_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RunRecord record = recordList.get(position);
        holder.tvDate.setText(record.getDate());
        holder.tvDistance.setText(String.format("%.2f km", record.getDistance()));
        holder.tvTime.setText(record.getFormattedTime());
        holder.tvPace.setText(record.getFormattedPace() + "/km");
        holder.tvCalories.setText(record.getCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return recordList == null ? 0 : recordList.size();
    }

    public void updateData(List<RunRecord> newList) {
        this.recordList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvDistance;
        TextView tvTime;
        TextView tvPace;
        TextView tvCalories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPace = itemView.findViewById(R.id.tv_pace);
            tvCalories = itemView.findViewById(R.id.tv_calories);
        }
    }
}