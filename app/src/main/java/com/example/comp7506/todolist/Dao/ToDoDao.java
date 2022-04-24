package com.example.comp7506.todolist.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.comp7506.todolist.DBHelper.MyDatabaseHelper;
import com.example.comp7506.todolist.Bean.Todos;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ToDoDao {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public ToDoDao(Context context) {
        dbHelper = new MyDatabaseHelper(context.getApplicationContext(),"Data.db", null, 2);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long create(Todos todos) {

        open();
        ContentValues values = new ContentValues();
        values.put("todotitle", todos.getTitle());
        values.put("tododsc", todos.getDesc());
        values.put("tododate", todos.getDate());
        values.put("todotime", todos.getTime());
        values.put("remindTime", todos.getRemindTime());
        values.put("isAlerted", todos.getisAlerted());
        values.put("isRepeat", todos.getIsRepeat());
        values.put("imgId", todos.getImgId());
        values.put("objectId",todos.getObjectId());
        long id = db.insert("Todo", null, values);
        close();
        return id;
    }

    public List<Todos> getNotAlertTodos(){
        open();
        List<Todos> allTodos = new ArrayList<Todos>();
        Cursor cursor = db.query("Todo",
                null, "isAlerted = ?", new String[] { "0" }, null, null, "remindTime");
        while (cursor.moveToNext()) {
            Todos data = new Todos();
            data.setId(cursor.getInt(cursor.getColumnIndex("id")));
            data.setTitle(cursor.getString(cursor.getColumnIndex("todotitle")));
            data.setDesc(cursor.getString(cursor.getColumnIndex("tododsc")));
            data.setDate(cursor.getString(cursor.getColumnIndex("tododate")));
            data.setTime(cursor.getString(cursor.getColumnIndex("todotime")));
            data.setRemindTime(cursor.getLong(cursor.getColumnIndex("remindTime")));
            data.setRemindTimeNoDay(cursor.getLong(cursor.getColumnIndex("remindTimeNoDay")));
            data.setisAlerted(cursor.getInt(cursor.getColumnIndex("isAlerted")));
            data.setIsRepeat(cursor.getInt(cursor.getColumnIndex("isRepeat")));
            data.setImgId(cursor.getInt(cursor.getColumnIndex("imgId")));
            allTodos.add(data);
        }

        cursor.close();
        close();
        return allTodos;
    }

    public List<Todos> getAllTask() {
        open();
        List<Todos> todosList = new ArrayList<Todos>();
        Cursor cursor=db.rawQuery("SELECT * FROM Todo", null);
        while(cursor.moveToNext()) {
            Todos data = new Todos();
            data.setId(cursor.getInt(cursor.getColumnIndex("id")));
            data.setTitle(cursor.getString(cursor.getColumnIndex("todotitle")));
            data.setDesc(cursor.getString(cursor.getColumnIndex("tododsc")));
            data.setDate(cursor.getString(cursor.getColumnIndex("tododate")));
            data.setTime(cursor.getString(cursor.getColumnIndex("todotime")));
            data.setRemindTime(cursor.getLong(cursor.getColumnIndex("remindTime")));
            data.setRemindTimeNoDay(cursor.getLong(cursor.getColumnIndex("remindTimeNoDay")));
            data.setisAlerted(cursor.getInt(cursor.getColumnIndex("isAlerted")));
            data.setIsRepeat(cursor.getInt(cursor.getColumnIndex("isRepeat")));
            data.setImgId(cursor.getInt(cursor.getColumnIndex("imgId")));
            data.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
            todosList.add(data);
        }
        // make sure to close the cursor

        cursor.close();
        close();
        return todosList;
    }

    public Todos getTask(int id) {
        open();
        Todos data = new Todos();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo WHERE id =" + id, null);
        if (cursor.moveToNext()) {
            data.setId(cursor.getInt(cursor.getColumnIndex("id")));
            data.setTitle(cursor.getString(cursor.getColumnIndex("todotitle")));
            data.setDesc(cursor.getString(cursor.getColumnIndex("tododsc")));
            data.setDate(cursor.getString(cursor.getColumnIndex("tododate")));
            data.setTime(cursor.getString(cursor.getColumnIndex("todotime")));
            data.setRemindTime(cursor.getLong(cursor.getColumnIndex("remindTime")));
            data.setRemindTimeNoDay(cursor.getLong(cursor.getColumnIndex("remindTimeNoDay")));
            data.setisAlerted(cursor.getInt(cursor.getColumnIndex("isAlerted")));
            data.setIsRepeat(cursor.getInt(cursor.getColumnIndex("isRepeat")));
            data.setImgId(cursor.getInt(cursor.getColumnIndex("imgId")));
            data.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
        }
        cursor.close();
        close();
        return data;
    }

    public void setisAlerted(int id){
        open();
        Log.i(TAG, "Data updated");
        ContentValues values = new ContentValues();
        values.put("isAlerted", 1);
        Log.i(TAG, String.valueOf(id));
        db.update("Todo", values, "id = ?", new String[]{id + ""});
        close();

    }

    public void saveAll(List<Todos> list) {
        for (Todos todos : list) {
            create(todos);
        }
    }

    public void clearAll() {
        open();
        db.execSQL("delete from Todo");
        db.execSQL("update sqlite_sequence set seq = 0 where name = 'Todo' ");
        close();
    }


}
