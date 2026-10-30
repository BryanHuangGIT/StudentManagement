package com.example.studentmanagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextStudentId;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextCourse;
    private EditText editTextSearch;

    private Button buttonAdd;
    private Button buttonUpdate;
    private Button buttonDelete;

    private RecyclerView recyclerViewStudents;

    private FirebaseFirestore firestore;
    private ListenerRegistration studentListener;

    private final List<Student> allStudents = new ArrayList<>();
    private final List<Student> displayedStudents = new ArrayList<>();

    private StudentAdapter studentAdapter;

    private String selectedStudentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        initializeViews();
        setupRecyclerView();
        setupButtons();
        setupSearch();
        loadStudents();
    }

    private void initializeViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCourse = findViewById(R.id.editTextCourse);
        editTextSearch = findViewById(R.id.editTextSearch);

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);

        recyclerViewStudents =
                findViewById(R.id.recyclerViewStudents);
    }

    private void setupRecyclerView() {
        studentAdapter = new StudentAdapter(
                displayedStudents,
                this::selectStudent
        );

        recyclerViewStudents.setLayoutManager(
                new LinearLayoutManager(this));

        recyclerViewStudents.setAdapter(studentAdapter);
    }

    private void setupButtons() {
        buttonAdd.setOnClickListener(v -> addStudent());
        buttonUpdate.setOnClickListener(v -> updateStudent());
        buttonDelete.setOnClickListener(v -> deleteStudent());
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start,
                    int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start,
                    int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean validateInputs() {

        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String course = editTextCourse.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email address");
            editTextEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(course)) {
            editTextCourse.setError("Course is required");
            editTextCourse.requestFocus();
            return false;
        }

        return true;
    }

    private void addStudent() {

        if (!validateInputs()) {
            return;
        }

        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String course = editTextCourse.getText().toString().trim();

        // Generate a unique student ID
        String studentId = firestore.collection("students")
                .document()
                .getId();

        Student student = new Student(
                studentId,
                name,
                email,
                course
        );

        firestore.collection("students")
                .document(studentId)
                .set(student)
                .addOnSuccessListener(unused -> {
                    showMessage("Student added successfully");
                    clearFields();
                })
                .addOnFailureListener(e ->
                        showMessage("Add failed: " + e.getMessage()));
    }

    private void updateStudent() {

        if (selectedStudentId == null) {
            showMessage("Select a student to update");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        Student updatedStudent = new Student(
                selectedStudentId,
                editTextName.getText().toString().trim(),
                editTextEmail.getText().toString().trim(),
                editTextCourse.getText().toString().trim()
        );

        firestore.collection("students")
                .document(selectedStudentId)
                .set(updatedStudent)
                .addOnSuccessListener(unused -> {
                    showMessage("Student updated successfully");
                    clearFields();
                })
                .addOnFailureListener(e ->
                        showMessage("Update failed: " + e.getMessage()));
    }

    private void deleteStudent() {

        if (selectedStudentId == null) {
            showMessage("Select a student to delete");
            return;
        }

        String idToDelete = selectedStudentId;

        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage(
                        "Are you sure you want to delete student "
                                + idToDelete + "?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    firestore.collection("students")
                            .document(idToDelete)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                showMessage(
                                        "Student deleted successfully");
                                clearFields();
                            })
                            .addOnFailureListener(e ->
                                    showMessage(
                                            "Delete failed: "
                                                    + e.getMessage()));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadStudents() {

        studentListener = firestore.collection("students")
                .addSnapshotListener((snapshots, error) -> {

                    if (error != null) {
                        showMessage(
                                "Unable to load students: "
                                        + error.getMessage());
                        return;
                    }

                    if (snapshots == null) {
                        return;
                    }

                    allStudents.clear();

                    for (DocumentSnapshot document : snapshots) {

                        Student student =
                                document.toObject(Student.class);

                        if (student != null) {
                            allStudents.add(student);
                        }
                    }

                    allStudents.sort((a, b) ->
                            safeString(a.getName())
                                    .compareToIgnoreCase(
                                            safeString(b.getName())));

                    filterStudents(editTextSearch.getText()
                            .toString());
                });
    }

    private void filterStudents(String query) {

        displayedStudents.clear();

        String searchText = query.toLowerCase(
                Locale.ROOT).trim();

        for (Student student : allStudents) {

            String id = safeString(student.getStudentId())
                    .toLowerCase(Locale.ROOT);
            String name = safeString(student.getName())
                    .toLowerCase(Locale.ROOT);
            String course = safeString(student.getCourse())
                    .toLowerCase(Locale.ROOT);

            if (id.contains(searchText)
                    || name.contains(searchText)
                    || course.contains(searchText)) {
                displayedStudents.add(student);
            }
        }

        studentAdapter.notifyDataSetChanged();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private void selectStudent(Student student) {

        selectedStudentId = student.getStudentId();

        editTextName.setText(student.getName());
        editTextEmail.setText(student.getEmail());
        editTextCourse.setText(student.getCourse());

        showMessage("Student selected");
    }

    private void clearFields() {

        selectedStudentId = null;

        editTextName.setText("");
        editTextEmail.setText("");
        editTextCourse.setText("");

        editTextName.clearFocus();
    }

    private void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (studentListener != null) {
            studentListener.remove();
        }
    }
}
