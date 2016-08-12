package com.thyn.tasklist;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.CursorAdapter;
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
import com.thyn.db.thynTaskDBHelper;
import com.thyn.form.TaskActivity;
import com.thyn.form.TaskFragment;
import com.thyn.collection.MyTaskLab;
import com.thyn.form.view.TaskPagerViewOnlyActivity;
import com.thyn.form.view.TaskPagerViewOnlyFragment;
import com.thyn.R;
import com.thyn.tab.DashboardActivity;


import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

/**
 * Created by shalu on 2/22/16.
 */
public class TaskListFragment extends ListFragment{
    private static final String TAG = "TaskListFragment";
    private ArrayList<Task> mTasks;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static final String EXTRA_NUM_ITEMS =
            "com.thyn.tasklist.my.TaskListFragment.NumItems";
    public static final String RETRIEVE_FROM_CACHE =
            "com.thyn.tasklist.my.TaskListFragment.RetrieveFromCache";
    private  int numItems;
    private MyTaskLab manager;
    private thynTaskDBHelper.TaskCursor mCursor;
    public static final TaskListFragment newInstance(boolean showEntireScreen){
        int numberofItems = (showEntireScreen == true) ? 0:2;
        TaskListFragment tlf = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_NUM_ITEMS, numberofItems);
        if(numberofItems != 2) bundle.putBoolean(RETRIEVE_FROM_CACHE, true);
        tlf.setArguments(bundle);
        return tlf;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.task_title);

        manager = MyTaskLab.get(getActivity());

        if(!getArguments().containsKey(RETRIEVE_FROM_CACHE)) {
            manager.purgeTasks();
        }
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume() called...");
        super.onResume();

        manager = MyTaskLab.get(getActivity());

        Bundle arguments = getArguments();
        loadData(arguments);
        //((TaskCursorAdapter)getListAdapter()).notifyDataSetChanged();
    }

   // Subu - Please call loadData() from onResume. If created from onCreate(), it will be fired twice.
    private void loadData(Bundle arguments){
        //manager.removeAllTasks();
        if(!manager.isInCache()) {
            Log.d(TAG, "no cache exists...");
            callAsyncThread(arguments);
        } else {
            Log.d(TAG, "yes. there is cache...");

            mCursor = manager.queryTasks();
            if(mCursor.moveToFirst()) Log.d(TAG, "Cursor count is: " + mCursor.getCount());
            TaskCursorAdapter adapter = new TaskCursorAdapter(getActivity(), mCursor);
            adapter.notifyDataSetChanged();
            setListAdapter(adapter);

        }
    }
    private void callAsyncThread(Bundle arguments){

        if(arguments != null && arguments.containsKey(EXTRA_NUM_ITEMS)) {
            numItems = arguments.getInt(EXTRA_NUM_ITEMS, 0);
            System.out.println("onResume: numItems to print is: " + numItems);
            new RetrieveFromServerAsyncTask(numItems).execute();
        }

        else new RetrieveFromServerAsyncTask().execute();


    }

    @Override
    public void onDestroy(){
        mCursor.close();
        super.onDestroy();
    }
 /*   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_list, menu);
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_new_task:
                Task t = new Task();

                //MyTaskLab.get(getActivity()).addTask(t);
                manager.addTask(t);

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
        // The id argument will be Task ID; CursorAdapter gives this to us for FREE
        Intent i = new Intent(getActivity(), TaskActivity.class);
        i.putExtra(TaskFragment.EXTRA_TASK_ID, id);
        startActivity(i);
    }

 /* Commenting this because it was used for TaskListAdapter when we got the information
  directly from the internet instead of caching the results.
 @Override
   public void onListItemClick(ListView l, View v, int position, long id){
       Task t = ((TaskListAdapter)getListAdapter()).getItem(position);

       Log.d(TAG, "Clicked task id: "+t.getId());
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
   }*/

    private void refreshContent(){
        new RetrieveFromServerAsyncTask().execute();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        },000);
    }

  /*  private class TaskListAdapter extends ArrayAdapter<Task> {
        public TaskListAdapter(ArrayList<Task> tasks){
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
//            TextView timeTextView = (TextView)convertView.findViewById(R.id.task_list_item_serviceDateTextView);
//            if(t.getCreateDate()!=null) timeTextView.setText(android.text.format.DateFormat.format("EEE, MMM d h:mm a", t.getServiceDate()));
            TextView userTextView = (TextView)convertView.findViewById(R.id.task_list_item_User);
            userTextView.setText(t.getUserProfileName());
            TextView locationTextView = (TextView)convertView.findViewById(R.id.task_list_item_locationTextView);
            locationTextView.setText(t.getBeginLocation());
//            TextView isAcceptedTextView = (TextView) convertView.findViewById(R.id.task_list_item_Is_Accepted);
//            if(t.isAccepted()) {
//                isAcceptedTextView.setText("Accepted");
//                TextView helperProfileNameTextView = (TextView) convertView.findViewById(R.id.task_list_item_Helper);
//                helperProfileNameTextView.setText("Helper: " + t.getHelperProfileName());
//            }

            return convertView;
        }
    }//End Inner Class -1 TaskListAdapter
*/
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
        numItems = getArguments().getInt(EXTRA_NUM_ITEMS, 0);
        Log.d(TAG, "In onCreateView, numitems is " + numItems);
        if(numItems == 2) {
            v = inflater.inflate(R.layout.fragment_tasklist, container, false);
            ImageView img_view = (ImageView) v.findViewById(R.id.expand);
            img_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                }
            });
            return v;
        }
        v = inflater.inflate(R.layout.fragment_tasklist_nobutton, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refreshing content");
                refreshContent();
            }
        });
        return v;
    }
    private static MyTaskApi myApiService = null;

    private class RetrieveFromServerAsyncTask extends AsyncTask<Void, Void, List> {
        int numItems;

        public RetrieveFromServerAsyncTask(int numItems) {
            super();
            this.numItems = numItems;
        }

        public RetrieveFromServerAsyncTask() {
            super();
        }

        @Override
        protected List doInBackground(Void... params) {
            List l = null;

            try {
                Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
                //Log.d(TAG, "Sending user profile id:"+userprofileid);
                l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, userprofileid, false).execute().getItems();
            } catch (IOException e) {
                e.getMessage();
            }

            return l;
        }

        @Override
        protected void onPostExecute(List result) {
            if (result == null) return;

            Iterator i = result.iterator();
            /* initialize the array list to 0 items. remove that existed before */
            //MyTaskLab.get(getActivity()).removeAllTasks();
            //manager.removeAllTasks();
            Log.d(TAG, "The data count sent from the server is: " + result.size());

            if(!manager.isInCache()){
                while (i.hasNext()) {
                    MyTask myTask = (MyTask) i.next();
                    if (myTask.getId() != null) Log.d(TAG, myTask.getId().toString());
                    Log.d(TAG, " Inserting Task. Description: " + myTask.getTaskDescription());
                    manager.convertRemotetoLocalTask(myTask);

                }
            }
         /*   if (numItems == 2) {
                while (i.hasNext() && numItems > 0) {
                    MyTask myTask = (MyTask) i.next();
                    if (myTask.getId() != null) Log.d(TAG, myTask.getId().toString());
                    Log.d(TAG, " Inserting Task. Description: " + myTask.getTaskDescription());
                    manager.convertRemotetoLocalTask(myTask);
                    numItems--;
                }
            } else {
                while (i.hasNext()) {
                    MyTask myTask = (MyTask) i.next();
                    if (myTask.getId() != null) Log.d(TAG, myTask.getId().toString());
                    //Log.d(TAG, "profile key" + myTask.getUserProfileKey());
                    // Log.d(TAG, "Who is going to help this task: " + myTask.getHelperUserProfileKey());
                    // Log.d(TAG, "Helper profile name" + myTask.getHelperProfileName());
                    Log.d(TAG, " Inserting Task. Description: " + myTask.getTaskDescription());
                    // Log.d(TAG, "Task create date: " + myTask.getCreateDate());
                    // Log.d(TAG, "Task service date: " + myTask.getServiceDate());
                    //MyTaskLab.get(getActivity()).convertRemotetoLocalTask(myTask);
                    manager.convertRemotetoLocalTask(myTask);
                }
            }*/

             // TaskListAdapter adapter = new TaskListAdapter(manager.getTasks());
             // adapter.notifyDataSetChanged();
             // setListAdapter(adapter);
           if(numItems == 2) mCursor = manager.queryTasks(numItems);
            else mCursor = manager.queryTasks();
            TaskCursorAdapter adapter = new TaskCursorAdapter(getActivity(), mCursor);
            adapter.notifyDataSetChanged();
            setListAdapter(adapter);
        }
    }
}
