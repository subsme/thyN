package com.thyn.tasklist;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import android.util.Log;
import android.content.Intent;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.thyn.android.backend.myTaskApi.MyTaskApi;
import com.thyn.android.backend.myTaskApi.model.MyTask;


import java.util.List;
import java.util.Iterator;


import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.form.TaskActivity;
import com.thyn.form.TaskFragment;
import com.thyn.collection.MyTaskLab;
import com.thyn.form.view.TaskPagerViewOnlyActivity;
import com.thyn.form.view.TaskPagerViewOnlyFragment;
import com.thyn.R;
import com.thyn.user.LoginActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class TaskListFragment extends ListFragment{
    private static final String TAG = "TaskListFragment";
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
        //((TaskAdapter)getListAdapter()).notifyDataSetChanged();
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
                MyTaskLab.get(getActivity()).addTask(t);

                Intent i = new Intent(getActivity(), TaskActivity.class);
                //Log.d(TAG,"Adding task id: "+t.getId());
                i.putExtra(TaskFragment.EXTRA_TASK_ID, t.getId());
                startActivityForResult(i,0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   @Override
   public void onListItemClick(ListView l, View v, int position, long id){
       Task t = ((TaskAdapter)getListAdapter()).getItem(position);
      // Log.d(TAG, "Clicked task id: "+t.getId());
       Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
       Log.i(TAG, "user profile id is: " + userprofileid);
       Log.i(TAG, "my profile key is: " + t.getUserProfileKey());
       if(userprofileid.equals(t.getUserProfileKey())){
           Log.i(TAG, "The userprofileid and profilekey are the same. This is my task. I created it.");
           Intent i = new Intent(getActivity(), TaskActivity.class);
           i.putExtra(TaskFragment.EXTRA_TASK_ID, t.getId());
           i.putExtra(TaskFragment.OPERATION, "UPDATE");
           startActivity(i);
       }
       else {
           Intent i = new Intent(getActivity(), TaskPagerViewOnlyActivity.class);
           i.putExtra(TaskPagerViewOnlyFragment.EXTRA_TASK_ID, t.getId());
           startActivity(i);
       }
   }


    private class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(ArrayList<Task> tasks){
            super(getActivity(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //if we weren't given a view, inflate one now.
            if(convertView == null){
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_task, null);
            }
            //Configure the view for this task
            Task t = getItem(position);
            Log.i(TAG, "User profile key is: " + t.getUserProfileKey());
            TextView descTextView = (TextView)convertView.findViewById(R.id.task_list_item_titleTextView);
            descTextView.setText(t.getTaskDescription());
            TextView dateTextView = (TextView)convertView.findViewById(R.id.task_list_item_createDateTextView);
            if(t.getCreateDate()!=null) dateTextView.setText(t.getDateReadableFormat());
            TextView timeTextView = (TextView)convertView.findViewById(R.id.task_list_item_serviceDateTextView);
            if(t.getCreateDate()!=null) timeTextView.setText(android.text.format.DateFormat.format("EEE, MMM d h:mm a", t.getServiceDate()));
            TextView userTextView = (TextView)convertView.findViewById(R.id.task_list_item_User);
            userTextView.setText(t.getUserProfileName());
            TextView locationTextView = (TextView)convertView.findViewById(R.id.task_list_item_locationTextView);
            locationTextView.setText(t.getBeginLocation());

            if(t.isAccepted()) {
                TextView isAcceptedTextView = (TextView) convertView.findViewById(R.id.task_list_item_Is_Accepted);
                isAcceptedTextView.setText("Accepted");
            }
            return convertView;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasklist, container, false);
        return v;
    }
    private static MyTaskApi myApiService = null;

    private class RetrieveFromServerAsyncTask extends AsyncTask<Void, Void, List> {


        @Override
        protected List doInBackground(Void... params) {
            List l = null;
            try {
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                //Log.d(TAG, "Sending user profile id:"+userprofileid);
                l = GoogleAPIConnector.connect_TaskAPI().listTasks(false,userprofileid,false).execute().getItems();
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
            MyTaskLab.get(getActivity()).removeAllTasks();
            Log.d(TAG, "The data from the server is");
            while(i.hasNext()) {
                MyTask myTask = (MyTask)i.next();
                Log.d(TAG, "Description: " + myTask.getTaskDescription());
                Log.d(TAG, "Task create date: " + myTask.getCreateDate());
                Log.d(TAG, "Task service date: " + myTask.getServiceDate());
                Log.d(TAG, "Who is going to help this task: " + myTask.getHelperUserProfileKey());
                Log.d(TAG, "profile key" + myTask.getUserProfileKey());
                if(myTask.getId() != null) Log.d(TAG, myTask.getId().toString());
                MyTaskLab.get(getActivity()).convertRemotetoLocalTask(myTask);
            }
            TaskAdapter adapter = new TaskAdapter(MyTaskLab.get(getActivity()).getTasks());
            setListAdapter(adapter);

        }

    }
}
