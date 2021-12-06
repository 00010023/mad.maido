package com.uwussimo.maido.Middlewares;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.uwussimo.maido.Model.TodoModel;
import com.uwussimo.maido.R;
import com.uwussimo.maido.Utils.DatabaseHandler;
import com.uwussimo.maido.Utils.DialogCloseListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private EditText newTaskSubText;
    private RadioGroup newTaskPriority;
    private Button newTaskSaveButton;
    private RadioButton newHigh;
    private RadioButton newNormal;
    private RadioButton newLow;
    private Button newDateTimeButton;
    private TextView newDateTime;

    private DatabaseHandler db;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        Objects.requireNonNull(getDialog()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = requireView().findViewById(R.id.newTaskText);
        newTaskSaveButton = requireView().findViewById(R.id.newTaskButton);
        newTaskSubText = requireView().findViewById(R.id.newTaskDetailText);
        newTaskPriority = requireView().findViewById(R.id.newTaskRadioLayout);
        newHigh = requireView().findViewById(R.id.radioHigh);
        newNormal = requireView().findViewById(R.id.radioNormal);
        newLow = requireView().findViewById(R.id.radioLow);
        newDateTimeButton = requireView().findViewById(R.id.dateTimePicker);
        newDateTime = requireView().findViewById(R.id.newTodoDate);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            String notes = bundle.getString("notes");
            String priority = bundle.getString("priority");
            String date = bundle.getString("date");
            newTaskText.setText(task);
            newTaskSubText.setText(notes);
            newDateTime.setText(date);

            if (priority.matches("Normal")) {
                newNormal.setChecked(true);
            } else if (priority.matches("Low")) {
                newLow.setChecked(true);
            } else {
                newHigh.setChecked(true);
            }

            newTaskSaveButton.setText("Edit");

            assert task != null;
            if(task.length()>0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
                else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        newDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                final Calendar c = Calendar.getInstance();

                try {
                    Log.d("variable", newDateTime.getText().toString());
                    c.setTime(Objects.requireNonNull(sdf.parse(newDateTime.getText().toString())));
                    c.add(Calendar.MONTH, 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String response = year + "-" + month + "-" + dayOfMonth;
                        newDateTime.setText(response);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                String note = newTaskSubText.getText().toString();
                String date = newDateTime.getText().toString();
                String priority = "";
                if (newTaskPriority.getCheckedRadioButtonId() != -1) {
                    int id = newTaskPriority.getCheckedRadioButtonId();
                    View radiobutton = newTaskPriority.findViewById(id);
                    int radioId = newTaskPriority.indexOfChild(radiobutton);
                    RadioButton btn = (RadioButton) newTaskPriority.getChildAt(radioId);
                    priority = (String) btn.getText();
                }

                if (finalIsUpdate){
                    db.updateTask(bundle.getInt("id"), text);
                    db.updatePriority(bundle.getInt("id"), priority);
                    db.updateNotes(bundle.getInt("id"), note);
                    db.updateDate(bundle.getInt("id"), date);
                }
                else {
                    TodoModel task = new TodoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    task.setPriority(priority);
                    task.setNotes(note);
                    task.setDate(date);
                    db.insertTask(task);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}
