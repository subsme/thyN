package photogallery.android.bignerdranch.com.thyn.form;


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
import java.text.ParseException;
import java.util.Date;

import android.os.Build;
import android.annotation.TargetApi;

import android.view.MenuItem;

import android.util.Log;
import android.app.Activity;

import java.util.Calendar;

import photogallery.android.bignerdranch.com.thyn.DatePickerFragment;
import photogallery.android.bignerdranch.com.thyn.R;
import photogallery.android.bignerdranch.com.thyn.collection.Task;
import photogallery.android.bignerdranch.com.thyn.collection.MyTaskLab;
import photogallery.android.bignerdranch.com.thyn.TimePickerFragment;
import photogallery.android.bignerdranch.com.thyn.field.AddressActivity;
import photogallery.android.bignerdranch.com.thyn.field.AddressFragment;
import photogallery.android.bignerdranch.com.thyn.user.LoginActivity;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.thyn.backend.myTaskApi.MyTaskApi;
import com.thyn.backend.myTaskApi.model.MyTask;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;


/**
 * A placeholder fragment containing a simple view.
 */
public class TaskFragment extends Fragment {
    private static final String TAG = "TaskFragment";
    public static final String EXTRA_TASK_ID =
            "com.android.thyn.task_id";
    private static final int ADDRESS_ACTIVITY_REQUEST_CODE=1;
    private static final int DESCRIPTION_ACTIVITY_REQUEST_CODE=1;

    private Task mTask;
    private EditText mTaskDescriptionField;
    private EditText mTaskFromAddress;
    private EditText mTaskToAddress;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mTaskDone;

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 2;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Long taskID = (Long)getArguments().getSerializable(EXTRA_TASK_ID);
        mTask = MyTaskLab.get(getActivity()).getTask(taskID);
//         Log.d(TAG,"Retrieving task id: " + mTask.getId());
        setHasOptionsMenu(true);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                    Log.d(TAG, "Menu item clicked");
                    MyTaskLab.get(getActivity()).removeTask(mTask);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        mTaskFromAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "From was clicked");
                Intent i = new Intent(getActivity(), AddressActivity.class);
                i.putExtra("t","from");
                startActivityForResult(i, ADDRESS_ACTIVITY_REQUEST_CODE);
            }
        });
        mTaskToAddress    = (EditText)v.findViewById(R.id.task_to);
        mTaskToAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "to was clicked");
                Intent i = new Intent(getActivity(), AddressActivity.class);
                i.putExtra("t","to");
                startActivityForResult(i, ADDRESS_ACTIVITY_REQUEST_CODE);
            }
        });
        mTaskDone = (Button)v.findViewById(R.id.task_done);
        mTaskDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                    Log.d(TAG, "Done button clicked");
                    new SendToServerAsyncTask().execute(mTask);
                }
            }
        });
    return v;
    }
    private void updateDate(){
        if(mTask.getServiceDate() == null) mTask.setServiceDate(new Date());
        SimpleDateFormat sdFormat = new SimpleDateFormat("EEE, MMM d");
        mDateButton.setText(sdFormat.format(mTask.getServiceDate()));
    }
    private void updateTime(){
        if(mTask.getServiceDate() == null) mTask.setServiceDate(new Date());
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

    private class SendToServerAsyncTask extends AsyncTask<Task, Void, Void> {


        @Override
        protected Void doInBackground(Task... params) {
            if (myApiService == null) {  // Only do this once
                MyTaskApi.Builder builder = new MyTaskApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            // context = params[0].first;
            // String name = params[0].second;

            try {
                MyTask a = copyToEndpointTask(params[0]);
                //Log.d(TAG, params[0].toString());
                //a.setTaskDescription("take off homie 2");
                //return myApiService.replay(2,a).execute().getTaskDescription();
                myApiService.insertMyTask(a).execute();
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
            myTask.setUserProfileKey(
                    PreferenceManager
                            .getDefaultSharedPreferences(getActivity())
                            .getLong(LoginActivity.PREF_USERPROFILE_ID, -1)
            );
            return myTask;
        }
    }
}
