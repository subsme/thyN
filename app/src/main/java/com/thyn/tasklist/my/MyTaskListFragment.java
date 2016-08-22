package com.thyn.tasklist.my;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
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
import com.thyn.collection.MyTaskLab;
import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.db.thynTaskDBHelper;
import com.thyn.form.TaskActivity;
import com.thyn.form.view.my.MyTaskViewOnlyFragment;
import com.thyn.form.TaskFragment;
import com.thyn.form.view.my.MyTaskPagerViewOnlyActivity;
import com.thyn.R;
import com.thyn.tab.DashboardActivity;
import com.thyn.user.LoginActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class MyTaskListFragment extends ListFragment{
    private static final String TAG = "MyTaskListFragment";
    private ArrayList<Task> mTasks;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static final String EXTRA_NUM_ITEMS =
            "com.thyn.tasklist.my.MyTaskListFragment.NumItems";

    boolean showEntireScreen;
    private MyPersonalTaskLab pmanager;
    private thynTaskDBHelper.TaskCursor mCursor;
    private static Long userprofileid = null;

    public static final MyTaskListFragment newInstance(boolean showEntireScreen){
        int numberofItems = (showEntireScreen == true) ? 0:2;
        MyTaskListFragment tlf = new MyTaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_NUM_ITEMS, numberofItems);
        tlf.setArguments(bundle);
        return tlf;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userprofileid = MyServerSettings.getUserProfileId(getActivity());
        getActivity().setTitle(R.string.task_title);
        Log.d(TAG, " in onCreate");
    }

    @Override
    public void onResume(){
        super.onResume();
        if(pmanager == null) pmanager = MyPersonalTaskLab.get(getActivity());

        Bundle arguments = getArguments();
        loadData();
    }
    // Subu - Please call loadData() from onResume. If created from onCreate(), it will be fired twice.
    private void loadData(){
        //manager.removeAllTasks();
        if(!MyServerSettings.getLocalTaskCache(getActivity())){
            Log.d(TAG, "no cache exists...");
            //callAsyncThread(arguments);
        } else {
            Log.d(TAG, "yes. there is cache...");
            int numRows = getArguments().getInt(EXTRA_NUM_ITEMS);
            if(numRows == 2) mCursor = pmanager.queryMyTasks(userprofileid,numRows);
            else mCursor = pmanager.queryMyTasks(userprofileid);
            if(mCursor.moveToFirst()) Log.d(TAG, "Cursor count is: " + mCursor.getCount());
            MyTaskCursorAdapter adapter = new MyTaskCursorAdapter(getActivity(), mCursor);
            adapter.notifyDataSetChanged();
            setListAdapter(adapter);
        }
    }
    /*private void callAsyncThread(Bundle arguments){
        new RetrieveFromServerAsyncTask().execute();
    }*/
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
       //Task t = ((TaskAdapter)getListAdapter()).getItem(position);
       Log.d(TAG, id+ " was clicked"); //page 191 big nerdranch
       //Start TaskPagerActivity
       Intent i = new Intent(getActivity(), MyTaskPagerViewOnlyActivity.class);
       i.putExtra(MyTaskViewOnlyFragment.EXTRA_TASK_ID, id);
       startActivity(i);
   }
    private void refreshContent(){
        //remove the local database and then refresh content.
        /*Subu: Aug 19/2016
        TODO - need to correct this logic because we included a PollService.
         */
        pmanager.purgeTasks();
        Log.d(TAG, "In refreshContent()");
        new RetrieveFromServerAsyncTask().execute();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 000);
    }
/*
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
  private static class MyTaskCursorAdapter extends CursorAdapter {
      private thynTaskDBHelper.TaskCursor mTaskCursor;

      public MyTaskCursorAdapter(Context context, thynTaskDBHelper.TaskCursor cursor){
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
        int numItems = getArguments().getInt(EXTRA_NUM_ITEMS, 0);
        Log.d(TAG, "In onCreateView, numitems is " + numItems);

        if(numItems == 2) {
            v = inflater.inflate(R.layout.fragment_tasklist1, container, false);
            ImageView img_view = (ImageView) v.findViewById(R.id.expand);
            img_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), DashboardActivity.class);
                    intent.putExtra(DashboardActivity.SELECT_SECOND_TAB, true);
                    startActivity(intent);
                }
            });
        }
        else{
            v = inflater.inflate(R.layout.fragment_tasklist_nobutton, container, false);
            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "Refreshing content");
                    refreshContent();
                }
            });
        }

        return v;
    }



    private static MyTaskApi myApiService = null;

    private class RetrieveFromServerAsyncTask extends AsyncTask<Void, Void, List> {


        @Override
        protected List doInBackground(Void... params) {
            List l = null;

            try {
                userprofileid = MyServerSettings.getUserProfileId(getActivity());
                Log.d(TAG, "Sending user profile id:"+userprofileid);
                l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, userprofileid, false).execute().getItems();
            } catch (IOException e) {
                 e.getMessage();
            }
            return l;
        }

        @Override
        protected void onPostExecute(List result) {
            if(result == null) return;

            Iterator i = result.iterator();

            //MyPersonalTaskLab.get(getActivity()).removeAllTasks();
            Log.d(TAG, "The data count sent from the server is: " + result.size());
            if(!pmanager.doesLocalCacheExist()) {
                while (i.hasNext()) {
                    MyTask myTask = (MyTask) i.next();
                    //if (myTask.getId() != null) Log.d(TAG, myTask.getId().toString());
                    Log.d(TAG, " Inserting MyTask. Description: " + myTask.getTaskDescription());
                    pmanager.convertRemotetoLocalTask(myTask);
                }
                pmanager.initializeLocalCache();
            }
            //TaskAdapter adapter = new TaskAdapter(MyPersonalTaskLab.get(getActivity()).getTasks());
            //setListAdapter(adapter);
            mCursor = pmanager.queryMyTasks(userprofileid);
            MyTaskCursorAdapter adapter = new MyTaskCursorAdapter(getActivity(), mCursor);
            adapter.notifyDataSetChanged();
            setListAdapter(adapter);

        }
    }

}
