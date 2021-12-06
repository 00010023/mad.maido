
package com.uwussimo.maido.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uwussimo.maido.Adapter.TodoAdapter;
import com.uwussimo.maido.Model.TodoModel;
import com.uwussimo.maido.R;
import com.uwussimo.maido.Utils.DatabaseHandler;
import com.uwussimo.maido.Utils.DialogCloseListener;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private TodoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private FloatingActionButton help;

    private List<TodoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TodoAdapter(db,MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.fab);
        help = findViewById(R.id.help);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(tasksAdapter.getContext());
               builder.setTitle("Welcome to our Todo Application!");
               builder.setMessage(Html.fromHtml("<b>We have some gestures that you would consider to use:</b>" +
                       "<br>Swipe Left - <i>Delete a todo</i>" +
                       "<br>Swipe Right - <i>Edit a todo</i>" +
                       "<br>\"+\" button - <i>Add a new todo</i>" +
                       "<br>" +
                       "<br><b>Thank you for using our application!</b>"
               ));
               builder.setCancelable(true);
               builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               });

               AlertDialog alert = builder.create();
               alert.show();
           }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}