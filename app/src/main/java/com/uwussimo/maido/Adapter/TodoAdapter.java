package com.uwussimo.maido.Adapter;

/**
 * Copyrighted to 00010023 UwUssimo
 * To avoid high plagiarism check issues
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uwussimo.maido.Middlewares.AddNewTask;
import com.uwussimo.maido.Middlewares.MainActivity;
import com.uwussimo.maido.Model.TodoModel;
import com.uwussimo.maido.R;
import com.uwussimo.maido.Utils.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private List<TodoModel> todoList;
    private final DatabaseHandler db;
    private final MainActivity activity;

    public TodoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();

        final TodoModel item = todoList.get(position);
        holder.task.setText(item.getTask());

        if (item.getStatus() == 0) {
            holder.task.setPaintFlags(0);
        } else {
            holder.task.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.task.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    db.updateStatus(item.getId(), 1);

                } else {
                    holder.task.setPaintFlags(0);
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
        holder.subtitle.setText(item.getNotes());

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
        final Calendar c = Calendar.getInstance();

        try {
            c.setTime(Objects.requireNonNull(sdf.parse(item.getDate())));
            c.add(Calendar.MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.date.setText(formatter.format(c.getTime()));

        int color = 0;
        String type = item.getPriority();
        if (type.matches("Normal")) {
            color = Color.parseColor("#009EE3");
        } else if (type.matches("Low")) {
            color = Color.parseColor("#33AA77");
        } else {
            color = Color.parseColor("#FF7799");
        }
        ((GradientDrawable) holder.priority.getBackground()).setColor(color);
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<TodoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        TodoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        TodoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("priority", item.getPriority());
        bundle.putString("notes", item.getNotes());
        bundle.putString("date", item.getDate());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView subtitle;
        ImageView priority;
        TextView date;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            subtitle = view.findViewById(R.id.todoTextBoxDescription);
            priority = view.findViewById(R.id.todoPriorityColor);
            date = view.findViewById(R.id.todoTimeBoxTime);
        }
    }

}
