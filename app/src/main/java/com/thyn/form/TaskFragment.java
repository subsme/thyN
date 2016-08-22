package com.thyn.form;


import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;

import java.io.IOException;
import java.util.Date;

import android.os.Build;
import android.annotation.TargetApi;

import android.view.MenuItem;

import android.util.Log;
import android.app.Activity;

import java.util.Calendar;

import com.thyn.DatePickerFragment;

import com.thyn.collection.Task;
import com.thyn.collection.MyTaskLab;
import com.thyn.TimePickerFragment;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.field.AddressActivity;
import com.thyn.field.AddressFragment;
import com.thyn.user.LoginActivity;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.thyn.android.backend.myTaskApi.MyTaskApi;
import com.thyn.android.backend.myTaskApi.model.MyTask;

import java.text.SimpleDateFormat;
import com.thyn.R;
import java.text.DateFormat;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskFragment extends Fragment {
    private static final String TAG = "TaskFragment";
    public static final String EXTRA_TASK_ID =
            "com.android.android.thyn.task_id";
    public static final String OPERATION = "com.android.android.thyn.TaskFragment.Operation";
    private static final int ADDRESS_ACTIVITY_REQUEST_CODE=1;
    private static final int DESCRIPTION_ACTIVITY_REQUEST_CODE=1;

    private Task mTask;
    private String operation;
    private EditText mTaskDescriptionField;
    private EditText mTaskFromAddress;
    private EditText mTaskToAddress;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mTaskDone;

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 2;

    private MyTaskLab myTaskLab = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myTaskLab = MyTaskLab.get(getActivity());
        Bundle args = getArguments();
        if(args != null) {
            long taskID = args.getLong(EXTRA_TASK_ID, -1);
            if (taskID != -1) {
                mTask = myTaskLab.getTask(taskID);
            }
        }
        //operation = (String)getArguments().getSerializable(OPERATION);
        //Log.d(TAG, "The operation is: " + operation);
        //mTask = MyTaskLab.get(getActivity()).getTask(taskID);
//         Log.d(TAG,"Retrieving task id: " + mTask.getId());
        //setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                    Log.d(TAG, "Menu item clicked");
                   // MyTaskLab.get(getActivity()).removeTask(mTask);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static TaskFragment newInstance(Long taskId, String operation){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TASK_ID, taskId);
        args.putSerializable(OPERATION, operation);

        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        if(mTask!=null)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if(NavUtils.getParentActivityName(getActivity())!=null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mTaskDescriptionField = (EditText)v.findViewById(R.id.task_description);
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
        mDateButton = (Button)v.findViewById(R.id.task_date);
        //mDateButton.setText(mCrime.getmDate().toString()); Encapsulating the code page 223 big nerd ranch
        if(mTask != null) updateDate();
        //mDateButton.setEnabled(false); enabling button page 214. big nerd ranch
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();

                    DatePickerFragment dialog = DatePickerFragment.newInstance(mTask.getServiceDate());
                    dialog.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                    dialog.show(fm, DIALOG_DATE);

            }
        });
        mTimeButton = (Button)v.findViewById(R.id.task_time);
        if (mTask!= null) updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();

                    TimePickerFragment dialog = TimePickerFragment.newInstance(mTask.getServiceDate());
                    dialog.setTargetFragment(TaskFragment.this, REQUEST_TIME);
                    dialog.show(fm, DIALOG_DATE);

            }
        });
        mTaskFromAddress    = (EditText)v.findViewById(R.id.task_from);
        if(mTask != null) mTaskFromAddress.setText(mTask.getBeginLocation());
        mTaskFromAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "From was clicked");
                Intent i = new Intent(getActivity(), AddressActivity.class);
                i.putExtra("t","from");
                startActivityForResult(i, ADDRESS_ACTIVITY_REQUEST_CODE);
            }
        });
       /* mTaskToAddress    = (EditText)v.findViewById(R.id.task_to);
        mTaskToAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "to was clicked");
                Intent i = new Intent(getActivity(), AddressActivity.class);
                i.putExtra("t","to");
                startActivityForResult(i, ADDRESS_ACTIVITY_REQUEST_CODE);
            }
        });*/
        mTaskDone = (Button)v.findViewById(R.id.task_done);
        mTaskDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                    Log.d(TAG, "Done button clicked");
                    Log.d(TAG, "operation value is: " + operation);
                    if(operation==null)
                        new InsertAsyncTask().execute(mTask);
                    else
                        new UpdateAsyncTask().execute(mTask);
                }
            }
        });
    return v;
    }
    private void updateDate(){
        if(mTask.getServiceDate() == null) mTask.setServiceDate(new Date());
        Log.d(TAG, "In updateDate(). The service date is: " + mTask.getServiceDate());
        SimpleDateFormat sdFormat = new SimpleDateFormat("EEE, MMM d");
        mDateButton.setText(sdFormat.format(mTask.getServiceDate()));
    }
    private void updateTime(){
        if(mTask.getServiceDate() == null) mTask.setServiceDate(new Date());
        Log.d(TAG, "In updateTime(). The service date is: " + mTask.getServiceDate());
        SimpleDateFormat sdFormat = new SimpleDateFormat("h:mm a");
        mTimeButton.setText(sdFormat.format(mTask.getServiceDate()));
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "In resume");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_ACTIVITY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String  from =data.getStringExtra(AddressFragment.EXTRA_ADDRESS_ID);
                String to = data.getStringExtra(AddressFragment.EXTRA_TO_ADDRESS_ID);
                if (from != null) {
                    mTask.setBeginLocation(from);
                    mTaskFromAddress.setText(from);
                }
                if (to != null) {
                    mTask.setEndLocation(to);
                    mTaskToAddress.setText(to);
                }
            }

        }
        else if(requestCode == REQUEST_DATE){
            Date date = (Date)data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            mTask.setServiceDate(date);
            updateDate();
        }
        else if(requestCode == REQUEST_TIME){
            Date timedate = (Date)data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            Log.d(TAG, "hour and minutes is " + timedate);

            Calendar calendarSource = Calendar.getInstance();
            calendarSource.setTime(timedate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mTask.getServiceDate());

            calendar.set(Calendar.HOUR, calendarSource.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendarSource.get(Calendar.MINUTE));

            mTask.setServiceDate(calendar.getTime());

            Log.d(TAG, "mTask datetime is" + mTask.getServiceDate());

            updateTime();
        }
    }

    private static MyTaskApi myApiService = null;

    private class InsertAsyncTask extends AsyncTask<Task, String, Void> {


        @Override
        protected Void doInBackground(Task... params) {
            try {
                MyTask a = copyToEndpointTask(params[0]);
                //Log.d(TAG, params[0].toString());
                //a.setTaskDescription("take off homie 2");
                //return myApiService.replay(2,a).execute().getTaskDescription();
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(TAG, "Sending user profile id:" + userprofileid);
                GoogleAPIConnector.connect_TaskAPI().insertMyTask(userprofileid, a).execute();
                //return myApiService2.sayHi("joojoo").execute().getData();
            } catch (IOException e) {
                e.getMessage();
            }
            return null;
        }


        private MyTask copyToEndpointTask(Task task){
            MyTask myTask = new MyTask();
            myTask.setTaskDescription(task.getTaskDescription());
            myTask.setBeginLocation(task.getBeginLocation());
            myTask.setEndLocation(task.getEndLocation());
            myTask.setIsSolved(task.isSolved());
            myTask.setWaitResponseTime(task.getmWaitResponseTime());
            myTask.setServiceDate(task.getServiceDate().toString());
            //DateFormat converter = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
            //converter.setTimeZone(TimeZone.getTimeZone("GMT"));
           // myTask.setServiceDate(converter.format(task.getServiceDate()));

            myTask.setUserProfileKey(
                    MyServerSettings.getUserProfileId(getActivity())
            );
            return myTask;
        }
    }
    private class UpdateAsyncTask extends AsyncTask<Task, String, Void> {


        @Override
        protected Void doInBackground(Task... params) {
            try {
                MyTask a = copyToEndpointTask(params[0]);
                //Log.d(TAG, params[0].toString());
                //a.setTaskDescription("take off homie 2");
                //return myApiService.replay(2,a).execute().getTaskDescription();
                //Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(TAG, "Sending task:" + a.getId());
                GoogleAPIConnector.connect_TaskAPI().updateMyTask(a).execute();
                //return myApiService2.sayHi("joojoo").execute().getData();
            } catch (IOException e) {
                e.getMessage();
            }
            return null;
        }


        private MyTask copyToEndpointTask(Task task){
            MyTask myTask = new MyTask();
            /* For update - setting this field is the difference */
            myTask.setId(task.getId());
            Log.d(TAG, "Task description: " + task.getTaskDescription());
            myTask.setTaskDescription(task.getTaskDescription());
            Log.d(TAG, "Task Begin location: " + task.getBeginLocation());
            myTask.setBeginLocation(task.getBeginLocation());
            myTask.setWaitResponseTime(task.getmWaitResponseTime());
            myTask.setServiceDate(task.getServiceDate().toString());
            //DateFormat converter = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
            //converter.setTimeZone(TimeZone.getTimeZone("GMT"));
            // myTask.setServiceDate(converter.format(task.getServiceDate()));

            /*myTask.setUserProfileKey(
                    MyServerSettings.getUserProfileId(getActivity())
            );*/
            return myTask;
        }
    }
}
