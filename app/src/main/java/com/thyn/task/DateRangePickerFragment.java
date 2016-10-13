package com.thyn.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

import com.squareup.timessquare.CalendarPickerView;
import com.thyn.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by shalu on 8/23/16.
 */
public class DateRangePickerFragment extends DialogFragment {
    private CalendarPickerView calendar;
    public static final String EXTRA_START_DATE =
            "com.android.thyn.daterangepickerfragment.startdate";
    public static final String EXTRA_END_DATE =
            "com.android.thyn.daterangepickerfragment.enddate";
    public static final String EXTRA_CALENDAR_SELECTION_TYPE =
            "com.android.thyn.calendar_selection_type";
    private Date mStartDate;
    private Date mEndDate;
    public static DateRangePickerFragment newInstance(Boolean isRangeSelection){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CALENDAR_SELECTION_TYPE, isRangeSelection);

        DateRangePickerFragment fragment = new DateRangePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void sendResult(int resultCode){
        if(getTargetFragment() == null){
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_START_DATE, mStartDate);
        i.putExtra(EXTRA_END_DATE, mEndDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Boolean isRangeSelection = (Boolean)getArguments().getSerializable(EXTRA_CALENDAR_SELECTION_TYPE);
        super.onCreate(savedInstanceState);
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.activity_date_picker, null);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        calendar = (CalendarPickerView) v.findViewById(R.id.calendar_view);
        Date today = new Date();
        if(isRangeSelection)
            calendar.init(today, nextYear.getTime())
                    .withSelectedDate(today)
                    .inMode(CalendarPickerView.SelectionMode.RANGE);
        else
            calendar.init(today, nextYear.getTime())
                    .withSelectedDate(today)
                    .inMode(CalendarPickerView.SelectionMode.SINGLE);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                        // .setPositiveButton(android.R.string.ok, null) big nerd ranch page 222
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                List<Date> selectedDates = calendar.getSelectedDates();
                                mStartDate = selectedDates.get(0);
                                mEndDate = selectedDates.size() == 2 ? selectedDates.get(1):null;
                                sendResult(Activity.RESULT_OK);
                            }
                        }

                )
                .create();

    }



}
