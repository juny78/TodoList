package com.juny78.todo;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import com.juny78.todo.db.TaskDBHelper;
import android.database.Cursor;
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.view.LayoutInflater;
import android.content.Context;
import android.support.v7.app.ActionBar;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class MainActivity extends ActionBarActivity
    implements DatePickerFragment.DatePickerDialogListener {

    public TaskDBHelper helper;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the custom action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_layout, null);

        actionBar.setCustomView(v);

        // Set today's date
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
        Button dateButton = (Button) findViewById(R.id.action_bar_button_date);
        dateButton.setText(sdf2.format(c.getTime()));

        // Display the task list
        helper = new TaskDBHelper(this);
        updateTaskList();
    }

    public void updateTaskList() {
        // Fetch all tasks from DB
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] allColumns = new String[] {
                TaskDBHelper.COLUMN_ID,
                TaskDBHelper.COLUMN_TASK,
                TaskDBHelper.COLUMN_STATUS
        };
        String where = String.format("date = '%s'", currentDate);
        Cursor cursor = db.query(TaskDBHelper.TABLE,
                                 allColumns,
                                 where,
                                 null, null, null, null);

        TaskListAdapter adapter = new TaskListAdapter(this, cursor, 0, this);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    public void onAddButtonClick(View view) {
        Log.d("MainActivity", "Add a new task");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a task");
        builder.setMessage("What do you want to do?");
        final EditText taskText = new EditText(this);
        builder.setView(taskText);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String task = taskText.getText().toString();
                Log.d("MainActivity", "Create a new task: " + task + " for " + currentDate);

                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.clear();
                values.put(TaskDBHelper.COLUMN_TASK, task);
                values.put(TaskDBHelper.COLUMN_STATUS, "open");
                values.put(TaskDBHelper.COLUMN_DATE, currentDate);

                db.insertWithOnConflict(TaskDBHelper.TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                updateTaskList();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    public void onDateButtonClick(View view) {
        Log.d("MainActivity", "Pick a date");

        DialogFragment df = new DatePickerFragment(currentDate);
        df.show(getFragmentManager(), "datePicker");
    }

    public void onDoneButtonClick(View view) {
        Button button = (Button) view;
        String status = button.getText().toString().equalsIgnoreCase("DONE") ? "done" : "open";

        View parentView = (View) button.getParent();
        TextView textView = (TextView) parentView.findViewById(R.id.taskTextView);
        String task = textView.getText().toString();

        // Update the status of the task in DB
        String where = String.format("%s = '%s' AND %s = '%s'",
                                     TaskDBHelper.COLUMN_TASK, task,
                                     TaskDBHelper.COLUMN_DATE, currentDate);

        ContentValues values = new ContentValues();
        values.clear();
        values.put(TaskDBHelper.COLUMN_STATUS, status);

        SQLiteDatabase db = helper.getWritableDatabase();
        db.update(TaskDBHelper.TABLE, values, where, null);

        updateTaskList();
    }

    @Override
    public void onPickerDateSet(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(calendar.getTime());

        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
        Button dateButton = (Button) findViewById(R.id.action_bar_button_date);
        dateButton.setText(sdf2.format(calendar.getTime()));

        updateTaskList();
    }
}

