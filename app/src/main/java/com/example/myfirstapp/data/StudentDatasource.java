package com.example.myfirstapp.data;

import android.content.Context;
import android.content.res.Resources;
import com.example.myfirstapp.R;
import com.example.myfirstapp.model.Student;

import java.util.HashMap;

public class StudentDatasource {
    private static HashMap<String, Student> allStudents;

    private static void loadStudents(Context c) {
        allStudents = new HashMap<>();
        Resources res = c.getResources();
        String[] student_ids = res.getStringArray(R.array.student_id);
        for (String sid : student_ids) {
            allStudents.put(sid, new Student(sid));
            // Currently students are *only* their student IDs, but in the future there may be
            // more to them.
        }
    }

    public static HashMap<String, Student> getAllStudents(Context c) {
        if (allStudents == null) {
            loadStudents(c);
        }
        return allStudents;
    }

    public static HashMap<String, Student> getAllStudents() {
        return allStudents;
    }
}