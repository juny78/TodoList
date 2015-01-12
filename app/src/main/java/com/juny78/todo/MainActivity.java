package com.juny78.todo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.EditText;
import android.content.DialogInterface.OnCancelListener;
import com.juny78.todo.db.TaskDBHelper;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;
import android.graphics.Paint;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    private TaskDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new TaskDBHelper(this);

        updateTaskList();
    }

    private void updateTaskList() {
        // Fetch all tasks from DB
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] allColumns = new String[] { TaskDBHelper.COLUMN_ID,
                                             TaskDBHelper.COLUMN_TASK,
                                             TaskDBHelper.COLUMN_STATUS };

        Cursor cursor = db.query(TaskDBHelper.TABLE,
                                 allColumns,
                                 null, null, null, null, null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.task_view,
                cursor,
                new String[] { TaskDBHelper.COLUMN_TASK },
                new int[] { R.id.taskTextView },
                0);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAddTask:
                Log.d("MainActivity","Add a new task");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add a task");
                builder.setMessage("What do you want to do?");
                final EditText taskText = new EditText(this);
                builder.setView(taskText);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = taskText.getText().toString();
                        Log.d("MainActivity", "Create a new task: " + task);

                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(TaskDBHelper.COLUMN_TASK, task);
                        values.put(TaskDBHelper.COLUMN_STATUS, "pending");

                        db.insertWithOnConflict(TaskDBHelper.TABLE,
                                                null,
                                                values,
                                                SQLiteDatabase.CONFLICT_IGNORE);

                        updateTaskList();
                    }
                });

                builder.setNegativeButton("Cancel", null);

                builder.create().show();
                return true;

            default:
                return false;
        }
    }

    public void onDoneButtonClick(View view) {
        Button doneButton = (Button) view;
        View parentView = (View) doneButton.getParent();
        TextView textView = (TextView) parentView.findViewById(R.id.taskTextView);
        String task = textView.getText().toString();

        // flag the task as done by changing the text
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        doneButton.setText("Open");

        // Update the status of the task in DB
        String sqlUpdate = String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'",
                                         TaskDBHelper.TABLE,
                                         TaskDBHelper.COLUMN_STATUS, "done",
                                         TaskDBHelper.COLUMN_TASK, task);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sqlUpdate);
    }
}
