package com.juny78.todo;

import java.util.Calendar;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.app.Activity;
import java.text.SimpleDateFormat;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerDialogListener {
        public void onPickerDateSet(int year, int month, int day);
    }
    DatePickerDialogListener listener;

    private String dateStr;

    public DatePickerFragment(String date) {
        dateStr = date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dsf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cal.setTime(dsf.parse(dateStr));
        } catch (Exception e) {
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(),
                                    this,
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DatePickerDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DatePickerDialogListener");
        }
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {
        listener.onPickerDateSet(year, month, day);
    }
}