package com.artsovalov.windenergyrss.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private DatePickerDialog.OnDateSetListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerDialog.OnDateSetListener)
            mListener = (DatePickerDialog.OnDateSetListener) context;
        else
            throw new RuntimeException(context.toString()
            + " must implement DatePickerDialog.OnDateSetListener");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        long cd = 0;
        if (getArguments() != null)
            cd = getArguments().getLong(MainActivity.ARG_PUB_DATE, 0);
        if (cd != 0)
            calendar.setTimeInMillis(cd);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        if (mListener != null)
            mListener.onDateSet(view, year, month, day);
    }
}
