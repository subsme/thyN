package com.thyn.task;


import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.annotation.TargetApi;

import android.view.MenuItem;

import android.util.Log;
import android.app.Activity;

import java.util.Calendar;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import com.google.api.client.util.DateTime;
import com.thyn.android.backend.myTaskApi.model.APIGeneralResult;
import com.thyn.collection.Task;
import com.thyn.collection.MyTaskLab;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.field.AddressActivity;
import com.thyn.field.AddressFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thyn.android.backend.myTaskApi.MyTaskApi;
import com.thyn.android.backend.myTaskApi.model.MyTask;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.thyn.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskFragment extends Fragment {
    private static final String TAG = "TaskFragment";
    public static final String EXTRA_TASK_ID =
            "com.android.android.thyn.task_id";

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE=1;

    private TextInputLayout descriptionLayoutName;
    private TextInputLayout titleLayoutName;
    private Task mTask;
    private TextView mQuickTip;
    private EditText mTaskDescriptionField;
    private EditText mTitle;
    private CheckBox mTaskDateRange;
    private CheckBox mTimeFlexible;
    private TextInputLayout dateLayoutName;
    private EditText mTaskDate;
    private TextInputLayout timeLayoutName;
    private EditText mTaskTime;
    private TextInputLayout locationLayoutName;
    private EditText mTaskLocation;
    private Button mTaskDone;
    private CheckBox mUseMyAddress;
    private static Date startDate = null;
    private static Date endDate = null;

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 2;

    private MyTaskLab myTaskLab = null;
    private static double mLAT;
    private static double mLONG;
    private static String mCity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myTaskLab = MyTaskLab.get(getActivity());
        Bundle args = getArguments();
        if(args != null) {
            long taskID = args.getLong(EXTRA_TASK_ID, -1);
            if (taskID != -1) {
                mTask = myTaskLab.getTask(taskID);
                Log.d(TAG,"Retrieved task: " + mTask.getTaskDescription());
                mLAT = mTask.getLAT();
                mLONG = mTask.getLONG();
                mCity = mTask.getCity();
            }
        }
    }

    public static TaskFragment newInstance(Long taskId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TASK_ID, taskId);

        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_randomtask, container, false);

        descriptionLayoutName = (TextInputLayout) v.findViewById(R.id.description_input_layout);
        mTaskDescriptionField = (EditText)v.findViewById(R.id.t_description);
        if(mTask != null) mTaskDescriptionField.setText(mTask.getTaskDescription());
        mTaskDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTask.setTaskDescription(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mQuickTip = (TextView)v.findViewById(R.id.quick_tip);
        mQuickTip.setText(Html.fromHtml(getString(R.string.quick_tip)));

        titleLayoutName = (TextInputLayout) v.findViewById(R.id.title_input_layout);
        mTitle = (EditText)v.findViewById(R.id.t_title);
        mTitle.setText(mTask.getTaskTitle());

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTask.setTaskTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dateLayoutName = (TextInputLayout) v.findViewById(R.id.date_input_layout);
        mTaskDate = (EditText)v.findViewById(R.id.t_date);
        startDate = mTask.getTaskFromDate();
        endDate = mTask.getTaskToDate();
        String fromDate = convertDateToString(startDate);
        String toDate = "";
        if(endDate != null)
            toDate = " - " + convertDateToString(endDate);
        fromDate = fromDate + toDate;
        mTaskDate.setText(fromDate);
        mTaskDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearDateText();
                        mTaskDateRange.setChecked(false);
                        FragmentManager fm = getActivity()
                                .getSupportFragmentManager();
                        DateRangePickerFragment dialog = DateRangePickerFragment.newInstance(false);
                        dialog.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                        dialog.show(fm, DIALOG_DATE);
                    }
                }
        );
        mTaskDateRange = (CheckBox)v.findViewById(R.id.t_date_range);
        mTaskDateRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clearDateText();
                    FragmentManager fm = getActivity()
                            .getSupportFragmentManager();
                    DateRangePickerFragment dialog = DateRangePickerFragment.newInstance(true);
                    dialog.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                    dialog.show(fm, DIALOG_DATE);

                }
            }
        });

        timeLayoutName = (TextInputLayout) v.findViewById(R.id.time_input_layout);
        mTaskTime = (EditText)v.findViewById(R.id.t_time);

        String timeRange = mTask.getTaskTimeRange();
        mTaskTime.setText(timeRange);

        long fromTime = stringToValue(true,timeRange.substring(0, timeRange.indexOf("-")));
        long toTime = stringToValue(false, timeRange.substring(timeRange.indexOf("-") + 1));
        Log.d(TAG, "fromTime is: " + fromTime);
        Log.d(TAG, "toTime is: " +  toTime);

        // Setup the new range seek bar
        long min_val = 0;
        final long max_val = 24*4;
        final RangeSeekBar<Long> rangeSeekBar = new RangeSeekBar<Long>(getActivity());
        // Set the range

        rangeSeekBar.setRangeValues(min_val, max_val);
        rangeSeekBar.setSelectedMinValue(fromTime);
        rangeSeekBar.setSelectedMaxValue(toTime);
        //final String timeStr = "";
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<Long> bar, Long minValue, Long maxValue) {
                mTaskTime.setTextColor(Color.BLACK);
                String timeStr = rangeSeekBar.valueToString(minValue) + " - " + rangeSeekBar.valueToString(maxValue);
                mTaskTime.setText(timeStr);
            }
        });
// Add to layout
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.seekbar_placeholder);
        layout.addView(rangeSeekBar);
        final TimeFoo tfoo= new TimeFoo();
        mTimeFlexible = (CheckBox)v.findViewById(R.id.t_time_flex);

        mTimeFlexible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rangeSeekBar.setVisibility(View.INVISIBLE);
                    tfoo.setTime(mTaskTime.getText().toString());
                    mTaskTime.setTextColor(Color.argb(1, 197, 110, 72));
                    mTaskTime.setText("Time");
                } else {
                    rangeSeekBar.setVisibility(View.VISIBLE);
                    mTaskTime.setTextColor(Color.BLACK);
                    mTaskTime.setText(tfoo.getTime());
                }
            }
        });

        locationLayoutName = (TextInputLayout) v.findViewById(R.id.location_input_layout);
        mTaskLocation = (EditText)v.findViewById(R.id.t_location);
        mTaskLocation.setText(mTask.getBeginLocation());
        mTaskLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPlaceAutocompleteActivityIntent();
            }
        });

        mUseMyAddress = (CheckBox)v.findViewById(R.id.t_my_address);
        mUseMyAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTaskLocation.setText(MyServerSettings.getUserAddress(getActivity()));
                }
            }
        });

        mTaskDone = (Button)v.findViewById(R.id.task_done);
        mTaskDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Don't submit the form if the validation fails.
                if (!validateForm()) return;

              /*  if(NavUtils.getParentActivityName(getActivity()) != null) {
                    callPlaceAutocompleteActivityIntent();
                    NavUtils.navigateUpFromSameTask(getActivity());*/

                Log.d(TAG, "Done button clicked. Task id is: " + mTask.getId());
                Log.d(TAG, "LAT/Long/city is" + mLAT + " " + mLONG + " " + mCity);

                mTask.setTaskTitle(mTitle.getText().toString());
                mTask.setTaskDescription(mTaskDescriptionField.getText().toString());
                mTask.setTaskFromDate(startDate);
                mTask.setTaskToDate(endDate);
                mTask.setTaskTimeRange(mTaskTime.getText().toString());
                mTask.setBeginLocation(mTaskLocation.getText().toString());
                mTask.setLAT(mLAT);
                mTask.setLONG(mLONG);
                mTask.setCity(mCity);
                new UpdateAsyncTask().execute(mTask);
                //}

                Intent intent =
                        new Intent(getActivity(), ThumbsUpActivity.class);
                startActivity(intent);
            }
        });

    return v;
    }

    private boolean validateForm(){
        if( !validateTitle()
                ||  !validateDescription()
                ||  !validateDate()
                ||  !validateTime()
                ||  !validateAddress())
            return false;
        return true;
    }
    private boolean validateTitle(){
        if (mTitle.getText().toString().trim().isEmpty()) {
            titleLayoutName.setError(getString(R.string.err_msg_title));
            requestFocus(mTitle);
            return false;
        }else {
            titleLayoutName.setError(null);
        }
        return true;
    }
    private boolean validateDescription(){
        if (mTaskDescriptionField.getText().toString().trim().isEmpty()) {
            descriptionLayoutName.setError(getString(R.string.err_msg_description));
            requestFocus(mTaskDescriptionField);
            return false;
        }else {
            descriptionLayoutName.setError(null);
        }

        return true;
    }
    private boolean validateDate(){
        if(mTaskDate.getText().toString().trim().isEmpty()){
            dateLayoutName.setError(getString(R.string.err_msg_date));
            requestFocus(mTaskDate);
            return false;
        }else {
            dateLayoutName.setError(null);
        }
        return true;
    }
    private boolean validateTime(){
        if(mTaskTime.getText().toString().trim().isEmpty()){
            timeLayoutName.setError(getString(R.string.err_msg_time));
            requestFocus(mTaskTime);
            return false;
        }else {
            timeLayoutName.setError(null);
        }
        return true;
    }
    private boolean validateAddress(){
        if(mTaskLocation.getText().toString().trim().isEmpty()){
            locationLayoutName.setError(getString(R.string.err_msg_location));
            requestFocus(mTaskLocation);
            return false;
        }else {
            locationLayoutName.setError(null);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public long stringToValue(boolean first, String time){
        if(time == null) return -1;
        time = time.trim();
        if(!time.contains(":")) return -1;

        Log.d(TAG, "time is: " + time);
        String ampm = time.substring(time.indexOf(" ")).trim();

        long hour = Long.parseLong(time.substring(0,time.indexOf(":")));
        String min = time.substring(time.indexOf(":"));

        long val = 0;
        if(ampm.equalsIgnoreCase("AM")){
                val = hour *4;
            if(!first && hour == 12) val = 24 * 4;
        }
        else if(ampm.equalsIgnoreCase("PM")){
            val = (hour+12)*4;
        }
        switch(min){
            case "15": val = val + 15; break;
            case "30": val = val + 30; break;
            case "45": val = val + 45; break;
        }
        Log.d(TAG, "the val is: " + val);
        return val;
    }
    private String convertDateToString(Date d){
        if(d == null) return null;
        DateFormat converter = new SimpleDateFormat("MMM dd");
        converter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return converter.format(d);
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "In resume");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_DATE) {
            startDate = (Date) data
                    .getSerializableExtra(DateRangePickerFragment.EXTRA_START_DATE);

            endDate = (Date) data
                    .getSerializableExtra(DateRangePickerFragment.EXTRA_END_DATE);

            /*
             setting date text
             */
            setDateText();

        }

        else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place:" + place.toString());
                mTaskLocation.setText(place.getAddress());

                LatLng LatLong = place.getLatLng();
                mLAT = LatLong.latitude;
                mLONG = LatLong.longitude;
                Geocoder mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    List<Address> addresses = mGeocoder.getFromLocation(mLAT, mLONG, 1);
                    if (addresses != null && addresses.size() > 0) {
                        this.mCity = addresses.get(0).getLocality();
                        Log.i(TAG, "The city is: " + this.mCity);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.i(TAG, status.getStatusMessage());
            } else if (requestCode == Activity.RESULT_CANCELED) {

            }
        }

    }

    private void clearDateText(){
        mTaskDate.setText("");
    }
    private void setDateText(){
        String startDateText = new SimpleDateFormat("MMM d").format(startDate);
        String endDateText = "";
        if(endDate != null){
            endDateText = " - " + new SimpleDateFormat("MMM d").format(endDate);
        }
        mTaskDate.setText(startDateText + endDateText);
        mTask.setTaskFromDate(startDate);
        mTask.setTaskToDate(endDate);
    }
    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, 1);
        //PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    private class TimeFoo{
        private String timeStr;
        public void setTime(String str){
            timeStr = str;
        }
        public String getTime(){return timeStr;}
    }
    private class UpdateAsyncTask extends AsyncTask<Task, String, Void> {


        @Override
        protected Void doInBackground(Task... params) {
            try {
                MyTask a = copyToEndpointTask(params[0]);

                Log.d(TAG, "Sending task:" + a.getId());
                String socialID = MyServerSettings.getUserSocialId(getActivity());
                int socialType = MyServerSettings.getUserSocialType(getActivity());
                APIGeneralResult result = GoogleAPIConnector.connect_TaskAPI().updateMyTask(socialType, socialID, a).execute();
                Log.d(TAG, "response from the server is: " + result.getMessage());
            } catch (IOException e) {
                e.getMessage();
            }
            return null;
        }


        private MyTask copyToEndpointTask(Task task){
            MyTask myTask = new MyTask();
            myTask.setId(task.getId());
            myTask.setTaskTitle(task.getTaskTitle());
            myTask.setTaskDescription(task.getTaskDescription());
            myTask.setBeginLocation(task.getBeginLocation());
            myTask.setLat(task.getLAT());
            myTask.setLong(task.getLONG());
            myTask.setCity(task.getCity());

            myTask.setServiceDate(new DateTime(task.getTaskFromDate()));
            myTask.setServiceToDate(new DateTime(dateToString(task.getTaskToDate())));
            myTask.setServiceTimeRange(task.getTaskTimeRange());

            return myTask;
        }
        private String dateToString(Date d){
            if(d == null) return null;
            DateFormat converter = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
            converter.setTimeZone(TimeZone.getTimeZone("GMT"));
            return converter.format(d);
        }
    }
}
