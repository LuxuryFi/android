package com.example.coursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class YogaCourseAdapter extends RecyclerView.Adapter<YogaCourseAdapter.YogaCourseViewHolder> {

    private final Context context;
    private List<YogaCourse> yogaCourses;
    private final OnItemClickListener onItemClickListener;

    public YogaCourseAdapter(Context context, List<YogaCourse> yogaCourses, OnItemClickListener listener) {
        this.context = context;
        this.yogaCourses = yogaCourses != null ? yogaCourses : new ArrayList<>();
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public YogaCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_yoga_course, parent, false);
        return new YogaCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaCourseViewHolder holder, int position) {
        YogaCourse yogaCourse = yogaCourses.get(position);

        if (yogaCourse != null) {
            holder.classType.setText(yogaCourse.getClassType());
            holder.dayOfWeek.setText(yogaCourse.getDayOfWeek());
            holder.teacher.setText(yogaCourse.getTeacher());

            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(yogaCourse.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return yogaCourses != null ? yogaCourses.size() : 0;
    }

    public void updateYogaCourses(List<YogaCourse> newYogaCourses) {
        if (newYogaCourses != null) {
            yogaCourses.clear(); // Clear the existing list
            yogaCourses.addAll(newYogaCourses); // Add the new courses to the list
        } else {
            yogaCourses.clear(); // If the new list is null, clear the list
        }
        notifyDataSetChanged(); // Notify RecyclerView to update the UI
    }

    // ViewHolder for each item in the RecyclerView
    public static class YogaCourseViewHolder extends RecyclerView.ViewHolder {
        TextView classType;
        TextView dayOfWeek;
        TextView teacher;

        public YogaCourseViewHolder(View itemView) {
            super(itemView);
            classType = itemView.findViewById(R.id.classType);
            dayOfWeek = itemView.findViewById(R.id.dayOfWeek);
            teacher = itemView.findViewById(R.id.teacher);
        }
    }

    // Interface to handle item click events
    public interface OnItemClickListener {
        void onItemClick(int yogaCourseId);
    }
}
