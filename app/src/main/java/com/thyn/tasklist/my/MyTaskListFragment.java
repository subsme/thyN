package com.thyn.tasklist.my;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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


import com.thyn.collection.MyPersonalTaskLab;
import com.thyn.collection.Task;
import com.thyn.form.TaskActivity;
import com.thyn.form.view.my.MyTaskViewOnlyFragment;
import com.thyn.form.TaskFragment;
import com.thyn.form.view.my.MyTaskPagerViewOnlyActivity;
import com.thyn.R;
/**
 * Created by shalu on 2/22/16.
 */
public class MyTaskListFragment extends ListFragment{
    private static final String TAG = "MyTaskListFragment";
    private ArrayList<Task> mTasks;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.task_title);
        Log.d(TAG," in onCreate");
        new RetrieveFromServerAsyncTask().execute();
        //mTasks = TaskLab.get(getActivity()).getTasks();

        /*ArrayAdapter<Task> adapter =
                new ArrayAdapter<Task>(getActivity(), android.R.layout.simple_list_item_1, mTasks);*///page 188 big nerd rahcnh
        //TaskAdapter adapter = new TaskAdapter(mTasks);
        //setListAdapter(adapter);
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
                MyPersonalTaskLab.get(getActivity()).addTask(t);
                Intent i = new Intent(getActivity(), TaskActivity.class);
                i.putExtra(TaskFragment.EXTRA_TASK_ID, t.getId());
                startActivityForResult(i,0);
                //Intent i = new Intent(getActivity(), EndpointsAsyncTaskActivity.class);
                //startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   @Override
   public void onListItemClick(ListView l, View v, int position, long id){
       Task t = ((TaskAdapter)getListAdapter()).getItem(position);
       Log.d(TAG, t.getTaskDescription()+ " was clicked"); //page 191 big nerdranch
       //Start TaskPagerActivity
       Intent i = new Intent(getActivity(), MyTaskPagerViewOnlyActivity.class);
       i.putExtra(MyTaskViewOnlyFragment.EXTRA_TASK_ID, t.getId());
       startActivity(i);
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
                l = myApiService.listTasks(true,false).execute().getItems();
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
            MyPersonalTaskLab.get(getActivity()).removeAllTasks();
            while(i.hasNext()) {
                MyTask myTask = (MyTask)i.next();
                if(myTask.getId() != null) Log.d(TAG, myTask.getId().toString());
                MyPersonalTaskLab.get(getActivity()).convertRemotetoLocalTask(myTask);
            }
            TaskAdapter adapter = new TaskAdapter(MyPersonalTaskLab.get(getActivity()).getTasks());
            setListAdapter(adapter);

        }
    }
}
