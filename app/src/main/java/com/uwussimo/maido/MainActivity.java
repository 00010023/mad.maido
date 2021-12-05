
package com.uwussimo.maido;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.uwussimo.maido.Adapter.TodoAdapter;
import com.uwussimo.maido.Model.TodoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private TodoAdapter taskAdapter;
    private List<TodoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = new ArrayList<>();

        // Wrapper
        getSupportActionBar().hide();
        taskRecyclerView = findViewById(R.id.tasksRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Model Manager
        taskAdapter = new TodoAdapter(this);
        taskRecyclerView.setAdapter(taskAdapter);

        // Sample Data to test
        TodoModel task = new TodoModel();
        task.setTask("A test task to test");
        task.setStatus(0);
        task.setId(1);

        // Adding Sample
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);

        // Stimuling Datas
        taskAdapter.setTasks(taskList);
    }
}