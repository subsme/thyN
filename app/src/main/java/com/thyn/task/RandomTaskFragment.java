package com.thyn.task;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.api.client.util.DateTime;
import com.thyn.R;
import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.field.AddressActivity;
import com.thyn.field.AddressFragment;
import com.thyn.navigate.NavigationActivity;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONObject;

import android.graphics.PorterDuff.Mode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Subu sundaram on 8/23/16.
 */
public class RandomTaskFragment extends Fragment {

    private TextInputLayout descriptionLayoutName;
    private EditText mTaskDescription;
    private TextView mQuickTip;
    private TextInputLayout titleLayoutName;
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
    private final String timeStr=null;
    private static final int REQUEST_DATE = 0;
    //private static final int ADDRESS_ACTIVITY_REQUEST_CODE=1;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE=1;
    private static final String DIALOG_DATE = "date";
    private static Date startDate = null;
    private static Date endDate = null;
    private static final String TAG = "RandomTaskFragment";
    private static double mLAT;
    private static double mLONG;
    private static String mCity;


    private ProgressDialog dialog;
    /*subu end private members for autocomplete done */
    private Task mTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mTask == null){
            mTask = new Task();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_randomtask, container, false);

        descriptionLayoutName = (TextInputLayout) v.findViewById(R.id.description_input_layout);
        mTaskDescription = (EditText)v.findViewById(R.id.t_description);
        mTaskDescription.setHint(getString(R.string.hint_detailed_description));
        mTaskDescription.addTextChangedListener(new TextWatcher() {
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
        mTitle.setHintTextColor(ContextCompat.getColor(getContext(), R.color.greyseparator));
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
        mTaskDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearDateText();
                        mTaskDateRange.setChecked(false);
                        FragmentManager fm = getActivity()
                                .getSupportFragmentManager();
                        DateRangePickerFragment dialog = DateRangePickerFragment.newInstance(false);
                        dialog.setTargetFragment(RandomTaskFragment.this, REQUEST_DATE);
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
                    dialog.setTargetFragment(RandomTaskFragment.this, REQUEST_DATE);
                    dialog.show(fm, DIALOG_DATE);

                }
            }
        });
        timeLayoutName = (TextInputLayout) v.findViewById(R.id.time_input_layout);
        // Setup the new range seek bar
        final long min_val = 0;
        final long max_val = 24*4;
        final long lmin = 40;
        final long lmax = 60;
        final RangeSeekBar<Long> rangeSeekBar = new RangeSeekBar<Long>(getActivity());
        // Set the range

        rangeSeekBar.setRangeValues(min_val, max_val);
        rangeSeekBar.setSelectedMinValue(lmin);
        rangeSeekBar.setSelectedMaxValue(lmax);
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

        mTaskTime = (EditText)v.findViewById(R.id.t_time);
        mTaskTime.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeSeekBar.setSelectedMinValue(lmin);
                            rangeSeekBar.setSelectedMaxValue(lmax);
                            mTaskTime.setTextColor(Color.BLACK);
                            String timeStr = rangeSeekBar.valueToString(lmin) + " - " + rangeSeekBar.valueToString(lmax);
                            mTaskTime.setText(timeStr);
                        }
                    }
            );


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
                if(!validateForm()) return;

              /*  if(NavUtils.getParentActivityName(getActivity()) != null) {
                    callPlaceAutocompleteActivityIntent();
                    NavUtils.navigateUpFromSameTask(getActivity());*/

                    mTask = new Task(
                             mTitle.getText().toString()
                            , mTaskDescription.getText().toString()
                            , startDate
                            , endDate
                            , mTaskTime.getText().toString()
                            , mTaskLocation.getText().toString()
                            , mLAT
                            , mLONG
                            , mCity);
                    new InsertAsyncTask().execute(mTask);
                //}

                /*Intent intent =
                        new Intent(getActivity(), ThumbsUpActivity.class);
                startActivity(intent);*/
                ThumbsUpFragment thumbsUpFragment = ThumbsUpFragment.newInstance(null,null);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.navigation_fragment_container,
                        thumbsUpFragment,
                        thumbsUpFragment.getTag()).commit();
            }
        });
        /* Subu Hiding the floating action button
        * that was created in NavigationActivity.
         */
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).hideFloatingActionButton();
        }

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
        if (mTaskDescription.getText().toString().trim().isEmpty()) {
            descriptionLayoutName.setError(getString(R.string.err_msg_description));
            requestFocus(mTaskDescription);
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
    @Override
    /*Subu Oct 05 Had to dismiss the dialog i create in AsyncTask here because the activity could get destroyed before the asynctask
    see this issue here - http://stackoverflow.com/questions/2224676/android-view-not-attached-to-window-manager
     */
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    private class TimeFoo{
        private String timeStr;
        public void setTime(String str){
            timeStr = str;
        }
        public String getTime(){return timeStr;}
    }
    private class InsertAsyncTask extends AsyncTask<Task, String, Void> {



        /** progress dialog to show user that the backup is processing. */
        /** application context. */
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Please wait");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Task... params) {
            try {
                MyTask a = copyToEndpointTask(params[0]);
                //Log.d(TAG, params[0].toString());
                //a.setTaskDescription("take off homie 2");
                //return myApiService.replay(2,a).execute().getTaskDescription();
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                String socialID = MyServerSettings.getUserSocialId(getActivity());
                int socialType = MyServerSettings.getUserSocialType(getActivity());
                //Log.d(TAG, "Sending user profile id:" + userprofileid);
                //GoogleAPIConnector.connect_TaskAPI().insertMyTask(userprofileid, a).execute();
                Log.d(TAG, "Sending user profile id:" + (socialType==0?"facebook":"google") + ", socialID: " + socialID );
                GoogleAPIConnector.connect_TaskAPI().insertMyTaskWithSocialID(socialID, socialType, a).execute();
                //return myApiService2.sayHi("joojoo").execute().getData();
            } catch (IOException e) {
                e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }

        private MyTask copyToEndpointTask(Task task){
            MyTask myTask = new MyTask();
            myTask.setTaskTitle(task.getTaskTitle());
            myTask.setTaskDescription(task.getTaskDescription());
            myTask.setBeginLocation(task.getBeginLocation());
            myTask.setLat(task.getLAT());
            myTask.setLong(task.getLONG());
            myTask.setCity(task.getCity());
            //myTask.setEndLocation(task.getEndLocation());
            myTask.setIsSolved(task.isSolved());
            myTask.setWaitResponseTime(task.getmWaitResponseTime());
            myTask.setServiceDate(new DateTime(task.getTaskFromDate()));
            //myTask.setServiceFromDate(dateToString(task.getTaskFromDate()));

            if(task.getTaskToDate() != null) myTask.setServiceToDate(new DateTime(task.getTaskToDate()));
            myTask.setServiceTimeRange(task.getTaskTimeRange());
//            myTask.setServiceDate(task.getServiceDate().toString());
            //DateFormat converter = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
            //converter.setTimeZone(TimeZone.getTimeZone("GMT"));
            // myTask.setServiceDate(converter.format(task.getServiceDate()));

            myTask.setUserProfileKey(
                    MyServerSettings.getUserProfileId(getActivity())
            );
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
