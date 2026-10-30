package com.example.studentmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter
        extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private final List<Student> studentList;
    private final OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    public StudentAdapter(List<Student> studentList,
                          OnStudentClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);

        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull StudentViewHolder holder, int position) {

        Student student = studentList.get(position);

        holder.textViewName.setText(student.getName());
        holder.textViewId.setText(
                "Student ID: " + student.getStudentId());
        holder.textViewEmail.setText(
                "Email: " + student.getEmail());
        holder.textViewCourse.setText(
                "Course: " + student.getCourse());

        holder.itemView.setOnClickListener(v ->
                listener.onStudentClick(student));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder
            extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewId;
        TextView textViewEmail;
        TextView textViewCourse;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(
                    R.id.textViewStudentName);
            textViewId = itemView.findViewById(
                    R.id.textViewStudentId);
            textViewEmail = itemView.findViewById(
                    R.id.textViewStudentEmail);
            textViewCourse = itemView.findViewById(
                    R.id.textViewStudentCourse);
        }
    }
}
