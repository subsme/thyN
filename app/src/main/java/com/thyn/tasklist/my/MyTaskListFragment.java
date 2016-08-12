package com.thyn.tasklist.my;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
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
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.db.thynTaskDBHelper;
import com.thyn.form.TaskActivity;
import com.thyn.form.view.my.MyTaskViewOnlyFragment;
import com.thyn.form.TaskFragment;
import com.thyn.form.view.my.MyTaskPagerViewOnlyActivity;
import com.thyn.R;
import com.thyn.user.LoginActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class MyTaskListFragment extends ListFragment{
    private static final String TAG = "MyTaskListFragment";
    private ArrayList<Task> mTasks;
    public static final String SHOW_ENTIRE_SCREEN =
            "com.thyn.tasklist.my.MyTaskListFragment.NumItems";

    boolean showEntireScreen;

    public static final MyTaskListFragment newInstance(boolean showEntireScreen){
        MyTaskListFragment tlf = new MyTaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_ENTIRE_SCREEN, showEntireScreen);
        tlf.setArguments(bundle);
        return tlf;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.task_title);
        Log.d(TAG, " in onCreate");

        showEntireScreen = false;
        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(SHOW_ENTIRE_SCREEN)) {
            showEntireScreen = arguments.getBoolean(SHOW_ENTIRE_SCREEN);
        }
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


  /*  private class TaskAdapter extends ArrayAdapter<Task> {
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
//            TextView timeTextView = (TextView)convertView.findViewById(R.id.task_list_item_serviceDateTextView);
//            if(t.getCreateDate()!=null) timeTextView.setText(android.text.format.DateFormat.format("EEE, MMM d h:mm a", t.getServiceDate()));
            TextView userTextView = (TextView)convertView.findViewById(R.id.task_list_item_User);
            userTextView.setText(t.getUserProfileName());
            TextView locationTextView = (TextView)convertView.findViewById(R.id.task_list_item_locationTextView);
            locationTextView.setText(t.getBeginLocation());
            return convertView;
        }
    }*/
       /*
    See page 552 Big Nerd Ranch
     */
  private static class TaskCursorAdapter extends CursorAdapter {
      private thynTaskDBHelper.TaskCursor mTaskCursor;

      public TaskCursorAdapter(Context context, thynTaskDBHelper.TaskCursor cursor){
          super(context, cursor, 0);
          mTaskCursor = cursor;
      }

      @Override
      public View newView(Context context, Cursor cursor, ViewGroup parent) {
          // Use a layout inflater to get a row view
          LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          return inflater.inflate(R.layout.list_item_task, parent, false);
      }

      @Override
      public void bindView(View view, Context context, Cursor cursor) {
          // Get the Task for the current row
          Task task = mTaskCursor.getTask();

          // Set up the text view values
          TextView descTextView = (TextView)view.findViewById(R.id.task_list_item_titleTextView);
          descTextView.setText(task.getTaskDescription());

          TextView dateTextView = (TextView)view.findViewById(R.id.task_list_item_createDateTextView);
          if(task.getCreateDate()!=null) dateTextView.setText(task.getDateReadableFormat());
          TextView userTextView = (TextView)view.findViewById(R.id.task_list_item_User);
          userTextView.setText(task.getUserProfileName());
          TextView locationTextView = (TextView)view.findViewById(R.id.task_list_item_locationTextView);
          locationTextView.setText(task.getBeginLocation());

      }

  }
    // End inner class TaskCursorAdapter


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= null;
        if(!showEntireScreen) {
            v = inflater.inflate(R.layout.fragment_tasklist1, container, false);
            //TextView t = (TextView)v.findViewById(R.id.empty_text);
            //t.setText(getResources().getString(R.string.no_tasks_get_help));
        }
        else v = inflater.inflate(R.layout.fragment_tasklist_nobutton, container, false);

        return v;
    }



    private static MyTaskApi myApiService = null;

    private class RetrieveFromServerAsyncTask extends AsyncTask<Void, Void, List> {


        @Override
        protected List doInBackground(Void... params) {
            List l = null;

            try {
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(TAG, "Sending user profile id:"+userprofileid);
                l = GoogleAPIConnector.connect_TaskAPI().listTasks(true, userprofileid, false).execute().getItems();
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
