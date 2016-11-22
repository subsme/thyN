package com.thyn.task.view.edit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thyn.R;
import com.thyn.android.backend.myTaskApi.model.APIGeneralResult;
import com.thyn.collection.MyTaskLab;
import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.navigate.NavigationActivity;
import com.thyn.task.TaskActivity;
import com.thyn.task.TaskFragment;
import com.thyn.task.ThumbsUpActivity;
import com.thyn.task.ThumbsUpFragment;
import com.thyn.task.view.AcceptTaskDialogFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by shalu on 10/25/16.
 */
public class TaskPagerEditOnlyFragment extends Fragment {
    private static final String LOG_TAG = "TaskPagerEditOnlyFragment";
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

    private Button mEditButton;
    private Button mDeleteButton;
    private Button mTaskBackToDashboard;
    private MLRoundedImageView mTask_user_profile_image;

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private Long taskID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskID = (Long) getArguments().getSerializable(EXTRA_TASK_ID);
        Log.d(LOG_TAG, "The Task ID here is: " + taskID);
        mTask = MyTaskLab.get(getActivity()).getTask(taskID);
        setHasOptionsMenu(true);
    }

    public static TaskPagerEditOnlyFragment newInstance(Long taskId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TASK_ID, taskId);

        TaskPagerEditOnlyFragment fragment = new TaskPagerEditOnlyFragment();
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
        View v = inflater.inflate(R.layout.fragment_task_editmode, container, false);

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

            LinearLayout lm = (LinearLayout) v.findViewById(R.id.linear_layout_task_view);
            if(lm == null) Log.d(LOG_TAG,"Linearlayout is null");
            // create the layout params that will be used to define how your
            // button will be displayed
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mEditButton = (Button) v.findViewById(R.id.edit);
            mEditButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    /*Intent i = new Intent(getActivity(), TaskActivity.class);
                    i.putExtra(TaskFragment.EXTRA_TASK_ID, taskID);
                    startActivity(i);*/
                    TaskFragment taskFragment = TaskFragment.newInstance(taskID);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.navigation_fragment_container,
                            taskFragment,
                            taskFragment.getTag()).commit();
                }
            });
            mDeleteButton = (Button) v.findViewById(R.id.delete_this_request);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, "Delete button clicked. Task descr: " + mTask.getTaskDescription());
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    CancelTaskDialogFragment dialog = CancelTaskDialogFragment.newInstance(mTask.getUserProfileName());
                    dialog.setTargetFragment(TaskPagerEditOnlyFragment.this, 0);
                    dialog.show(fm, DIALOG_CANCEL_TASK);
                }
            });


            mTaskBackToDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getActivity().finish();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).hideFloatingActionButton();
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
            new DeleteAsyncTask().execute(mTask);

            Intent i = new Intent(getActivity(), ThumbsUpActivity.class);
            i.putExtra(ThumbsUpFragment.EXTRA_THUMBS_TITLE, "THANK YOU!");
            i.putExtra(ThumbsUpFragment.EXTRA_THUMBS_DESCRIPTION, "Your task is deleted now. Please create another one if you need some other help.");
            startActivity(i);
        } else Log.d(LOG_TAG, "RESult is CANCEL");
    }


    private class DeleteAsyncTask extends AsyncTask<Task, Void, String> {


        @Override
        protected String doInBackground(Task... params) {
            try {
                Log.d(LOG_TAG, "Calling deleteMyTask with TaskId: "+ mTask.getId());
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(LOG_TAG, "Sending user profile id:" + userprofileid);
                APIGeneralResult result = GoogleAPIConnector.connect_TaskAPI().deleteMyTask(mTask.getId(),userprofileid).execute();
                if(result.getStatusCode().equalsIgnoreCase("OK")){
                    Log.d(LOG_TAG, "Task :" + mTask.getId() + " deleted successfully");
                }
                else{

                    Log.d(LOG_TAG, "Task :" + mTask.getId() + " didnt get deleted. The message is: " + result.getMessage());
                }
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            return null;
        }

    }
}
