package com.arvindthangamani.practisesessions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.MyViewHolder> {

    private List<Recording> recordingList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date, duration;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
            duration = (TextView) view.findViewById(R.id.duration);
        }
    }

    public RecordingsAdapter(List<Recording> recordingList) {
        this.recordingList = recordingList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recording, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Recording recording = recordingList.get(position);
        holder.name.setText(recording.getName());
        holder.date.setText(String.valueOf(recording.getStartTimestamp()));
        holder.duration.setText(String.valueOf(recording.getDurationInMs()));
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }
}
