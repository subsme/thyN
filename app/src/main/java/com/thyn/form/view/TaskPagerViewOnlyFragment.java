package com.thyn.form.view;


import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


import com.thyn.collection.Task;
import com.thyn.collection.MyTaskLab;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.tab.MyTaskListActivity;
import com.thyn.user.LoginActivity;
import com.thyn.R;
/**
 * A placeholder fragment containing a simple view.
 */
public class TaskPagerViewOnlyFragment extends Fragment {
    private static final String LOG_TAG = "TaskPagerViewOnlyFragment";
    public static final String EXTRA_TASK_ID =
            "com.android.android.thyn.form.view.task_id";
    private static final String DIALOG_ACCEPT_TASK = "acceptTaskDialog";

    private Task mTask;
    private TextView mTaskDescriptionField;
    private TextView mTaskUserField;
    private TextView mTaskLocationField;
    private TextView mTaskServiceDateField;
    private TextView mTaskCreateDateField;
    private Button mButton;

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long taskID = (Long) getArguments().getSerializable(EXTRA_TASK_ID);
        Log.d(LOG_TAG,"The Task ID here is: " +  taskID);
        mTask = MyTaskLab.get(getActivity()).getTask(taskID);
        setHasOptionsMenu(true);
    }

    public static TaskPagerViewOnlyFragment newInstance(Long taskId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TASK_ID, taskId);

        TaskPagerViewOnlyFragment fragment = new TaskPagerViewOnlyFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task_viewmode, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mTaskUserField          =   (TextView)v.findViewById(R.id.task_user);
        mTaskLocationField      =   (TextView)v.findViewById(R.id.task_from_location);
        mTaskDescriptionField   =   (TextView) v.findViewById(R.id.task_description);
        mTaskServiceDateField   =   (TextView) v.findViewById(R.id.task_createDate);
        mTaskCreateDateField    =   (TextView) v.findViewById(R.id.task_serviceDate);

        mTaskUserField.setText(mTask.getUserProfileName());
        mTaskLocationField.setText(mTask.getBeginLocation());
        mTaskDescriptionField.setText(mTask.getTaskDescription());
        mTaskServiceDateField.setText(mTask.getDateReadableFormat(Task.DISPLAY_DATE_TIME,mTask.getServiceDate()));
        mTaskCreateDateField.setText(mTask.getDateReadableFormat());
        if(!mTask.isAccepted()) {//Only the tasks that aren't accepted will show a Accept button.
            mButton = (Button) v.findViewById(R.id.task_accept);
            mButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                /*if (NavUtils.getParentActivityName(getActivity()) != null) {

                    NavUtils.navigateUpFromSameTask(getActivity());
                */
                    Log.d(LOG_TAG, "Accept button clicked. Task descr: " + mTask.getTaskDescription());
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    AcceptTaskDialogFragment dialog = AcceptTaskDialogFragment.newInstance(mTask.getTaskDescription());
                    dialog.setTargetFragment(TaskPagerViewOnlyFragment.this, 0);
                    dialog.show(fm, DIALOG_ACCEPT_TASK);

                }
            });
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d(LOG_TAG, "RESULT is OK");

            Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
            Log.d(LOG_TAG, "User profile id is: " + userprofileid.toString());
            Log.d(LOG_TAG, "TAsk id is: " + mTask.getId());
            new SendToServerAsyncTask().execute(mTask);
            Intent i = new Intent(getActivity(), MyTaskListActivity.class);
            i.putExtra("TAB","2");
            startActivity(i);
        } else Log.d(LOG_TAG, "RESult is CANCEL");
    }


    private class SendToServerAsyncTask extends AsyncTask<Task, Void, String> {


        @Override
        protected String doInBackground(Task... params) {
            try {
                Log.d(LOG_TAG, "Calling updateTaskHelper with TaskId: "+ mTask.getId());
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(LOG_TAG, "Sending user profile id:"+userprofileid);
                GoogleAPIConnector.connect_TaskAPI().updateTaskHelper(mTask.getId(),userprofileid).execute();


            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            return null;
        }

    }
}
