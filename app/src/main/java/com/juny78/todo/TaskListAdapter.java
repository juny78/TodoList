package com.juny78.todo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Paint;
import android.util.Log;
import com.juny78.todo.db.TaskDBHelper;

public class TaskListAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;
    private MainActivity activity;

    public TaskListAdapter(Context context, Cursor cursor, int flags, MainActivity a) {
        super(context, cursor, flags);
        activity = a;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        final String task = cursor.getString( cursor.getColumnIndex( TaskDBHelper.COLUMN_TASK ));
        final String status = cursor.getString( cursor.getColumnIndex( TaskDBHelper.COLUMN_STATUS ));
        Log.d("TaskListAdapter", "task=" + task + ", status=" + status);

        // Set the task text field
        final TextView textViewTask = (TextView) view.findViewById(R.id.taskTextView);
        textViewTask.setText(task);
        if (status.equals("done")) {
            textViewTask.setPaintFlags(textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Set the "DONE" button text
        Button button = (Button) view.findViewById(R.id.doneButton);
        if (status.equals("done")) {
            button.setText("Open");
        } else {
            button.setText("Done");
        }

        // Long press "DONE" will delete the task
        View.OnLongClickListener listener = new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Button button = (Button) v;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete the task");
                builder.setMessage("Are you really sure?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("TaskListAdapter", "Delete a task: " + task);

                        SQLiteDatabase db = activity.helper.getWritableDatabase();
                        String where = String.format("%s = '%s'", TaskDBHelper.COLUMN_TASK, task);
                        db.delete(TaskDBHelper.TABLE, where, null);

                        activity.updateTaskList();
                    }
                });

                builder.setNegativeButton("Cancel", null);

                builder.create().show();

                return true;
            }
        };
        button.setOnLongClickListener(listener);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.task_view, parent, false);
    }

}
