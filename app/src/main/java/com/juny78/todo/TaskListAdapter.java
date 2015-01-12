package com.juny78.todo;

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

    public TaskListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        String task = cursor.getString( cursor.getColumnIndex( TaskDBHelper.COLUMN_TASK ));
        String status = cursor.getString( cursor.getColumnIndex( TaskDBHelper.COLUMN_STATUS ));
        Log.d("TaskListAdapter", "task=" + task + ", status=" + status);

        // Set the task text field
        TextView textViewTask = (TextView) view.findViewById(R.id.taskTextView);
        textViewTask.setText(task);
        if (status.equals("done")) {
            textViewTask.setPaintFlags(textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Set the done button
        Button button = (Button) view.findViewById(R.id.doneButton);
        if (status.equals("done")) {
            button.setText("Open");
        } else {
            button.setText("Done");
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.task_view, parent, false);
    }

}
