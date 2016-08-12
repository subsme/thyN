package com.thyn.tasklist.my.completed;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.thyn.android.backend.myTaskApi.MyTaskApi;
import com.thyn.android.backend.myTaskApi.model.MyTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.form.TaskActivity;
import com.thyn.form.TaskFragment;
import com.thyn.collection.MyCompletedTaskLab;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v4.app.Fragment;
import com.thyn.R;
import com.thyn.user.LoginActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class MyCompletedTaskListFragment extends Fragment{
    private static final String TAG = "MyCompletedTaskListFrag";
    private  RecyclerView mRecyclerView ;
    private ArrayList<Task> mTasks;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.task_title);

        new RetrieveFromServerAsyncTask().execute();

    }

    @Override
    public void onResume(){
        super.onResume();
        new RetrieveFromServerAsyncTask().execute();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_list, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_new_task:
                Task t = new Task();
                MyCompletedTaskLab.get(getActivity()).addTask(t);
                Intent i = new Intent(getActivity(), TaskActivity.class);
                i.putExtra(TaskFragment.EXTRA_TASK_ID, t.getId());
                startActivityForResult(i,0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CheckBox mSolvedCheckBox;
        private TextView descTextView;
        private TextView dateTextView;
        private TextView timeTextView;
        private TextView locationTextView;
        private TextView userTextView;
        private Task mTask;

        public TaskHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            descTextView = (TextView)itemView.findViewById(R.id.task_list_item_titleTextView);
            dateTextView = (TextView)itemView.findViewById(R.id.task_list_item_createDateTextView);
            timeTextView = (TextView)itemView.findViewById(R.id.task_list_item_serviceDateTextView);
            locationTextView = (TextView)itemView.findViewById(R.id.task_list_item_locationTextView);
            userTextView = (TextView)itemView.findViewById(R.id.task_list_item_User);
        }
        public void bindTask(Task task) {
            mTask = task;
            //mSolvedCheckBox.setChecked(task.isSolved());
            descTextView.setText(task.getTaskDescription());
            if(task.getCreateDate()!=null) dateTextView.setText(task.getDateReadableFormat());
//            if(task.getCreateDate()!=null) timeTextView.setText(android.text.format.DateFormat.format("EEE, MMM d h:mm a", task.getServiceDate()));
            locationTextView.setText(task.getBeginLocation());
            userTextView.setText(task.getUserProfileName());
        }
        @Override
        public void onClick(View v) {
            if (mTask != null) {
                /*Intent i = MyCompletedTaskListActivity.getIntent(v.getContext(), mTask);
                startActivity(i);*/
            }
        }
    }
    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
            return new TaskHolder(view);
        }
        @Override
        public void onBindViewHolder(TaskHolder holder, int pos) {
            Task task = mTasks.get(pos);
            holder.bindTask(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_completed_activity, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }
    private static MyTaskApi myApiService = null;

    private class RetrieveFromServerAsyncTask extends AsyncTask<Void, Void, List> {


        @Override
        protected List doInBackground(Void... params) {
            List l = null;

            try {
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(TAG, "Sending user profile id:" + userprofileid);
                l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, userprofileid, true).execute().getItems();
            } catch (IOException e) {
                 e.getMessage();
            }
            return l;
        }

        @Override
        protected void onPostExecute(List result) {
            if(result == null) return;

            Iterator i = result.iterator();
            /* initialize the array list to 0 items. remove that existed before */
            MyCompletedTaskLab.get(getActivity()).removeAllTasks();
            while(i.hasNext()) {
                MyTask myTask = (MyTask)i.next();
                MyCompletedTaskLab.get(getActivity()).convertRemotetoLocalTask(myTask);
            }
            mTasks = MyCompletedTaskLab.get(getActivity()).getTasks();
            mRecyclerView.setAdapter(new TaskAdapter());
        }
    }
}
