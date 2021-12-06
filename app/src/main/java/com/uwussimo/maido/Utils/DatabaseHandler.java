package com.uwussimo.maido.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uwussimo.maido.Model.TodoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String PRIORITY = "priority";
    private static final String NOTES = "notes";
    private static final String DATE = "date";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER, " + PRIORITY + " TEXT, " + DATE + " TEXT, " + NOTES + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(TodoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(PRIORITY, task.getPriority());
        cv.put(NOTES, task.getNotes());
        cv.put(DATE, task.getDate());
        db.insert(TODO_TABLE, null, cv);
    }

    @SuppressLint("Range")
    public List<TodoModel> getAllTasks(){
        List<TodoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do {
                        TodoModel task = new TodoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setPriority(cur.getString(cur.getColumnIndex(PRIORITY)));
                        task.setNotes(cur.getString(cur.getColumnIndex(NOTES)));
                        task.setDate(cur.getString(cur.getColumnIndex(DATE)));
                        taskList.add(task);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updatePriority(int id, String priority) {
        ContentValues cv = new ContentValues();
        cv.put(PRIORITY, priority);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateNotes(int id, String notes) {
        ContentValues cv = new ContentValues();
        cv.put(NOTES, notes);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateDate(int id, String date) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
