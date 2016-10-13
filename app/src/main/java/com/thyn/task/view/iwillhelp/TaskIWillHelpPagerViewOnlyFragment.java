package com.thyn.task.view.iwillhelp;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.Date;


import com.squareup.picasso.Picasso;
import com.thyn.collection.Task;
import com.thyn.collection.MyTaskLab;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;


import com.thyn.R;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.tab.DashboardActivity;
import com.thyn.tab.WelcomePageActivity;
import com.thyn.task.ThumbsUpActivity;
import com.thyn.task.ThumbsUpFragment;
import com.thyn.task.view.AcceptTaskDialogFragment;

import android.widget.LinearLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskIWillHelpPagerViewOnlyFragment extends Fragment {
    private static final String LOG_TAG = "TaskIWillHelpPagerViewOnlyFragment";
    public static final String EXTRA_TASK_ID =
            "com.android.android.thyn.form.view.task_id";
    private static final String DIALOG_CANCEL_TASK = "cancelTaskDialog";

    private Task mTask;
    private TextView mTaskDescriptionField;
    private TextView mTaskUserField;
    private TextView mTaskLocationField;
    private TextView mTaskServiceDateField;
    private TextView mTaskCreateDateField;
    private TextView mTaskListingDetails;

    private TextView mTaskDetailedLocation;
    private TextView mTaskWhenTimeField;
    private TextView mTaskWhenStartDateField;
    private TextView mTaskWhenEndDateField;

    private Button mCancelButton;
    private Button mDoneButton;
    private Button mTaskBackToDashboard;
    private MLRoundedImageView mTask_user_profile_image;

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    private static final int CURRENT = 0;
    private static final int PAST = -1;
    private static final int FUTURE = 1;

    private static int CANCEL_OR_COMPLETE;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long taskID = (Long) getArguments().getSerializable(EXTRA_TASK_ID);
        Log.d(LOG_TAG,"The Task ID here is: " +  taskID);
        mTask = MyTaskLab.get(getActivity()).getTask(taskID);
        setHasOptionsMenu(true);
    }

    public static com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment newInstance(Long taskId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TASK_ID, taskId);

        com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment fragment = new com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment();
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
        View v = inflater.inflate(R.layout.fragment_task_i_will_help_viewmode, container, false);

        /* Subu - I am not going to go back from the action bar. There is a different button to click to go back to dashboar.d
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }*/
        Context c = getActivity().getApplicationContext();
        mTask_user_profile_image     =   (MLRoundedImageView)v.findViewById(R.id.task_list_user_image);


        mTaskUserField          =   (TextView)v.findViewById(R.id.task_user);
        mTaskLocationField      =   (TextView)v.findViewById(R.id.task_from_location);
        mTaskDescriptionField   =   (TextView) v.findViewById(R.id.task_description);
        mTaskServiceDateField   =   (TextView) v.findViewById(R.id.task_createDate);
        mTaskCreateDateField    =   (TextView) v.findViewById(R.id.task_serviceDate);
        mTaskListingDetails     =   (TextView) v.findViewById(R.id.task_details_value);

        mTaskDetailedLocation   =   (TextView) v.findViewById(R.id.task_where_value);

        mTaskWhenTimeField      =   (TextView) v.findViewById(R.id.task_when_value);

        mTaskWhenStartDateField =   (TextView) v.findViewById(R.id.task_startdate);

        mTaskBackToDashboard    =   (Button) v.findViewById(R.id.task_BackToDashboard);

        mTaskDetailedLocation.setText(mTask.getBeginLocation());
        mTaskWhenTimeField.setText(mTask.getTaskTimeRange());

        if(mTask.getTaskFromDate() != null)
            mTaskWhenStartDateField.setText(new SimpleDateFormat("EEE, MMM d").format(mTask.getTaskFromDate()));


        Log.d(LOG_TAG, mTask.getImageURL());
        Picasso.with(c)
                // .load("https://scontent.xx.fbcdn.net/v/t1.0-1/p50x50/11156187_10205188530126207_4481467444246362898_n.jpg?oh=2dee76ec7e202649b84c7a71b4c86721&oe=58ADEBE1")
                .load(mTask.getImageURL())
                .into(mTask_user_profile_image);

        mTaskUserField.setText(mTask.getUserProfileName());
        mTaskLocationField.setText(mTask.getCity());
        mTaskDescriptionField.setText(mTask.getTaskTitle());
        mTaskListingDetails.setText(mTask.getTaskDescription());
//        mTaskServiceDateField.setText(mTask.getDateReadableFormat(Task.DISPLAY_DATE_TIME, mTask.getServiceDate()));
        mTaskCreateDateField.setText(mTask.getDateReadableFormat());

        Log.d(LOG_TAG, "task description is: " + mTask.getTaskDescription());
        Log.d(LOG_TAG, "isAccepted is " +mTask.isAccepted());
        if(!mTask.isAccepted()) {//Only the tasks that aren't accepted will show a Accept button.

            LinearLayout lm = (LinearLayout) v.findViewById(R.id.linear_layout_task_view);
            if(lm == null) Log.d(LOG_TAG,"Linearlayout is null");
                    // create the layout params that will be used to define how your
            // button will be displayed
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mCancelButton = (Button) v.findViewById(R.id.cancel_this_request);
            mDoneButton = (Button) v.findViewById(R.id.mark_complete);
            if(taskDateTimeHorizon(mTask.getTaskFromDate()) == PAST){
                mCancelButton.setVisibility(View.GONE);
            }
            else if(taskDateTimeHorizon(mTask.getTaskFromDate()) == FUTURE){
                mDoneButton.setVisibility(View.GONE);
            }
            else if(taskDateTimeHorizon(mTask.getTaskFromDate()) == CURRENT){
                //Do nothing
            }
            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, "Cancel button clicked. Task descr: " + mTask.getTaskDescription());
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    CANCEL_OR_COMPLETE = CancelOrCompleteTaskDialogFragment.CANCEL;
                    CancelOrCompleteTaskDialogFragment dialog = CancelOrCompleteTaskDialogFragment.newInstance(mTask.getUserProfileName(),CancelOrCompleteTaskDialogFragment.CANCEL);
                    dialog.setTargetFragment(com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment.this, 0);
                    dialog.show(fm, DIALOG_CANCEL_TASK);
                }
            });

            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, "Done button clicked. Task descr: " + mTask.getTaskDescription());
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    CANCEL_OR_COMPLETE = CancelOrCompleteTaskDialogFragment.DONE;
                    CancelOrCompleteTaskDialogFragment dialog = CancelOrCompleteTaskDialogFragment.newInstance(mTask.getUserProfileName(),CancelOrCompleteTaskDialogFragment.DONE);
                    dialog.setTargetFragment(com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment.this, 0);
                    dialog.show(fm, DIALOG_CANCEL_TASK);
                }
            });

            mTaskBackToDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
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
            /*Intent i = new Intent(getActivity(), WelcomePageActivity.class);
            i.putExtra("TAB","2");
            startActivity(i);*/
            if(CancelOrCompleteTaskDialogFragment.DONE == CANCEL_OR_COMPLETE) {
                Intent i = new Intent(getActivity(), ThumbsUpActivity.class);
                i.putExtra(ThumbsUpFragment.EXTRA_THUMBS_TITLE, "THANK YOU!");
                i.putExtra(ThumbsUpFragment.EXTRA_THUMBS_DESCRIPTION, "Awesome! Thanks for helping your neighbr " + mTask.getUserProfileName() + ".");
                startActivity(i);
            }
            else {
                Intent i = new Intent(getActivity(), DashboardActivity.class);
                startActivity(i);
            }
        } else Log.d(LOG_TAG, "RESult is CANCEL");
    }

    private int taskDateTimeHorizon(Date taskDate){
        int rtn = PAST;
        if (new Date().before(taskDate)) {
            rtn = FUTURE;
        }
        long delta = System.currentTimeMillis() - taskDate.getTime();
        if(Math.abs(delta) < 24*60*60*1000){
            rtn = CURRENT;
        }
        return rtn;
    }


    private class SendToServerAsyncTask extends AsyncTask<Task, Void, String> {


        @Override
        protected String doInBackground(Task... params) {
            try {
                Log.d(LOG_TAG, "Calling updateTaskHelper with TaskId: "+ mTask.getId());
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(LOG_TAG, "Sending user profile id:" + userprofileid);
                if(CANCEL_OR_COMPLETE==CancelOrCompleteTaskDialogFragment.CANCEL) {
                    GoogleAPIConnector.connect_TaskAPI().cancelTaskHelper(userprofileid, mTask.getId()).execute();
                }
                else {
                    GoogleAPIConnector.connect_TaskAPI().markComplete(mTask.getId(), userprofileid).execute();
                }
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            return null;
        }

    }
}
