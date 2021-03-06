package com.thyn.tasklist;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import android.util.Log;
import android.content.Intent;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.view.MenuItem;

import com.squareup.picasso.Picasso;
import com.thyn.android.backend.myTaskApi.model.MyTask;


import java.util.List;
import java.util.Iterator;


import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.db.thynTaskDBHelper;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.tab.DashboardFragment;
import com.thyn.collection.MyTaskLab;
import com.thyn.R;
import com.thyn.task.view.TaskPagerViewOnlyFragment;
import com.thyn.task.view.edit.TaskPagerEditOnlyFragment;


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

   // private ProgressDialog dialog;

    private MyTaskLab manager;
    private thynTaskDBHelper.TaskCursor mCursor;
    private static Long userprofileid = null;
    private int numRowsToShow;
    private static boolean toRefresh;
    public static final TaskListFragment newInstance(boolean showEntireScreen){
        int numberofItems = (showEntireScreen == true) ? 0:2;
        TaskListFragment tlf = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_NUM_ITEMS, numberofItems);
        tlf.setArguments(bundle);
        return tlf;
    }
    public static final TaskListFragment newInstance(boolean showEntireScreen, boolean refresh){
        toRefresh = refresh;
        int numberofItems = (showEntireScreen == true) ? 0:2;
        TaskListFragment tlf = new TaskListFragment();
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
        //getActivity().setTitle(R.string.task_title);

        if(manager == null) manager = MyTaskLab.get(getActivity());
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume() called...");
        super.onResume();

        if(manager == null) manager = MyTaskLab.get(getActivity());

        loadData();
        /*Subu Sundaram - Oct 07, 2016 - commenting this for now.
        refreshContent();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 000);*/

        //((TaskCursorAdapter)getListAdapter()).notifyDataSetChanged();
    }

   // Subu - Please call loadData() from onResume. If created from onCreate(), it will be fired twice.
    private void loadData(){

        /* Subu 08/19
        The if block will never be called because of change of strategy.
        PollService always fills up the local cache before we call TaskListFragment from SlidingTabsBasicFragment.
        It will always go to the else block.
        Which also means that the inner AsyncThread is never called unless we refresh.
        The refresh logic is yet to be implemented.
         */
        numRowsToShow = getArguments().getInt(EXTRA_NUM_ITEMS);
        /*Subu 10/11 if not in cache or toRefresh flag is true, then refresh. Added toRefresh variable.*/
        if(!MyServerSettings.getLocalTaskCache(getActivity()) || toRefresh) {
            Log.d(TAG, "No cache exists. Getting the latest tasks from server...Will Take some time...");
            //callAsyncThread(getArguments());

            refreshContent(numRowsToShow);
            Log.d(TAG, "LOCAL-CACHE is " + MyServerSettings.getLocalTaskCache(getContext()));
        }
        else {
            Log.d(TAG, "yes. there is cache now. Retrieving from cache...");
            if (numRowsToShow == 2) mCursor = manager.queryTasks(numRowsToShow);
            else mCursor = manager.queryTasks();
            if (mCursor.moveToFirst()) Log.d(TAG, "Cursor count is: " + mCursor.getCount());
            else Log.d(TAG, "Cursor already in first. Its count is: " + mCursor.getCount());
            TaskCursorAdapter adapter = new TaskCursorAdapter(getActivity(), mCursor);
            adapter.notifyDataSetChanged();
            setListAdapter(adapter);
        }

    }

    private void callAsyncThread(Bundle arguments){

        if(arguments != null && arguments.containsKey(EXTRA_NUM_ITEMS)) {
            int numItems = arguments.getInt(EXTRA_NUM_ITEMS, 0);
            System.out.println("onResume: numItems to print is: " + numItems);
            new RetrieveFromServerAsyncTask(numItems, getActivity()).execute();
        }

        else new RetrieveFromServerAsyncTask(getActivity()).execute();


    }

    @Override
    public void onDestroy(){
        if(mCursor != null) mCursor.close();
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

                /*Intent i = new Intent(getActivity(), TaskActivity.class);
                //Log.d(TAG,"Adding task id: "+t.getId());
                i.putExtra(TaskFragment.EXTRA_TASK_ID, t.getId());
                startActivityForResult(i,0);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        // The id argument will be Task ID; CursorAdapter gives this to us for FREE
        Log.d(TAG, "The id given by the cursor adapter is: " + id);

        Task t = MyTaskLab.get(getActivity()).getTask(id);
        Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
        Log.i(TAG, "user profile id is: " + userprofileid);
        if(t != null) Log.i(TAG, "my profile key is: " + t.getUserProfileKey());
        else    Log.e(TAG, "profile key is null");
        Intent i = null;
        if(userprofileid.equals(t.getUserProfileKey())) {
            Log.i(TAG, "the ids are the same");
            /*i = new Intent(getActivity(), TaskPagerEditOnlyActivity.class);
            i.putExtra(TaskPagerEditOnlyFragment.EXTRA_TASK_ID, id);*/
            TaskPagerEditOnlyFragment tpEditOnlyFragment = TaskPagerEditOnlyFragment.newInstance(id);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    tpEditOnlyFragment,
                    tpEditOnlyFragment.getTag()).addToBackStack(null).commit();
        }
        else{
            Log.i(TAG, "the ids are not the same");
           /* i = new Intent(getActivity(), TaskPagerViewOnlyActivity.class);
            i.putExtra(TaskPagerViewOnlyFragment.EXTRA_TASK_ID, id);*/
            TaskPagerViewOnlyFragment tpViewOnlyFragment = TaskPagerViewOnlyFragment.newInstance(id);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    tpViewOnlyFragment,
                    tpViewOnlyFragment.getTag()).addToBackStack(null).commit();

        }
//        startActivity(i);
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


    private void refreshContent(int numRows){
        //remove the local database and then refresh content.
        /*Subu: Aug 19/2016
        TODO - need to correct this logic because we included a PollService.
         */
        manager.purgeTasks();
        Log.d(TAG, "In refreshContent()");

        new RetrieveFromServerAsyncTask(numRows, getActivity()).execute();

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
        public void bindView(View view, Context c, Cursor cursor) {
            // Get the Task for the current row
            Task task = mTaskCursor.getTask();

            // Set up the text view values
            TextView titleTextView = (TextView)view.findViewById(R.id.task_list_item_titleTextView);
            titleTextView.setText(task.getTaskTitle());


            MLRoundedImageView imageView = (com.thyn.graphics.MLRoundedImageView) view.findViewById(R.id.task_list_user_image);
            if(task.getImageURL() != null) {
                Picasso.with(c)
                        //.load("https://scontent.xx.fbcdn.net/v/t1.0-1/p50x50/11156187_10205188530126207_4481467444246362898_n.jpg?oh=2dee76ec7e202649b84c7a71b4c86721&oe=58ADEBE1")
                        .load(task.getImageURL())
                        .into(imageView);
            }
            TextView dateTextView = (TextView)view.findViewById(R.id.task_list_item_createDateTextView);
            if(task.getCreateDate()!=null) dateTextView.setText(task.getDateReadableFormat());
            TextView userTextView = (TextView)view.findViewById(R.id.task_list_item_User);
            userTextView.setText(task.getUserProfileName());
            TextView locationTextView = (TextView)view.findViewById(R.id.task_list_item_locationTextView);
            //locationTextView.setText(task.getBeginLocation());
            locationTextView.setText(task.getCity());
            TextView distanceTextView = (TextView)view.findViewById(R.id.task_distance);

            double dist = task.getDistance();
            Log.d(TAG, "The task list distance is " + task.getDistance());
            distanceTextView.setText(Double.toString(dist));
            if(dist >=0) {
               // dist = (double) Math.round(dist * 100.0) / 100.0;
                DecimalFormat f = new DecimalFormat("##0.0 mi");
                distanceTextView.setText(f.format(dist));
            }
        }

    }
    // End inner class TaskCursorAdapter


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= null;
        final int numItems = getArguments().getInt(EXTRA_NUM_ITEMS, 0);
        Log.d(TAG, "In onCreateView, numitems is " + numItems);
        if(numItems == 2) {
            v = inflater.inflate(R.layout.fragment_tasklist, container, false);
            ListView listView = (ListView)v.findViewById(android.R.id.list);
            listView.setEmptyView(v.findViewById(android.R.id.empty));
            ImageView img_view = (ImageView) v.findViewById(R.id.expand);
            img_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Intent intent = new Intent(getActivity().getApplicationContext(), DashboardActivity.class);
                   // startActivity(intent);

                    Log.d(TAG, "Debug - before crash?");
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.navigation_fragment_container,
                            dashboardFragment,
                            dashboardFragment.getTag()).addToBackStack(null).commit();

                }
            });
            return v;
        }
        v = inflater.inflate(R.layout.fragment_tasklist_nobutton, container, false);
        ListView listView = (ListView)v.findViewById(android.R.id.list);
        listView.setEmptyView(v.findViewById(android.R.id.empty));
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refreshing content");
                refreshContent(numItems);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 000);
            }
        });

        return v;
    }
    @Override
    /*Subu Oct 07. Copied from RandomTaskFragment.
     Had to dismiss the dialog i create in AsyncTask here because the activity could get destroyed before the asynctask
    see this issue here - http://stackoverflow.com/questions/2224676/android-view-not-attached-to-window-manager
     */
    public void onPause() {
        super.onPause();
/*
commenting dialog creation for now. Not good for user interface. The progress dialog shows for two reasons when
app fires off.
 */
       /* if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;*/
    }

    private class RetrieveFromServerAsyncTask extends AsyncTask<Void, Void, List> {
        int numItems;
        int filterradius = 0;
        private Context c = null;
        public RetrieveFromServerAsyncTask(int numItems, Context c) {
            super();
            this.numItems = numItems;
            this.c = c;
        }
        public RetrieveFromServerAsyncTask(int numItems, int radius, Context c) {
            super();
            this.numItems = numItems;
            this.filterradius = radius;
            this.c = c;
        }

        public RetrieveFromServerAsyncTask(Context c) {
            super();
            this.c = c;
        }

        @Override
        protected List doInBackground(Void... params) {
            List l = null;

            try {
                Log.d(TAG, "Sending user profile id:"+userprofileid);
                Log.d(TAG, "Sending distance filter: " +  MyServerSettings.getFilterRadius(c));
                //l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, userprofileid, false).execute().getItems();
                l = GoogleAPIConnector.connect_TaskAPI().listTasks(MyServerSettings.getFilterRadius(c), false, MyServerSettings.getUserSocialId(c), MyServerSettings.getUserSocialType(c), false).execute().getItems();

            } catch (IOException e) {
                e.getMessage();
            }

            return l;
        }
        /** progress dialog to show user that the backup is processing. */
        /** application context. */
      /*  Oct 18, 2016 Removing progress dialog because there are two progress dialogs initially. Need to eliminate the second one.
      Also looked at how facebook did things. Facebook doesnt have a progress dialog. Instead it has a spinner inside the list view.

      @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Please wait");
            dialog.show();
        }*/

        @Override
        protected void onPostExecute(List result) {
            if (result == null) return;

            Iterator i = result.iterator();
            /* initialize the array list to 0 items. remove that existed before */
            //MyTaskLab.get(getActivity()).removeAllTasks();
            //manager.removeAllTasks();
            Log.d(TAG, "The data count sent from the server is: " + result.size());
            Log.d(TAG, "LOCAL-CACHE is " + MyServerSettings.getLocalTaskCache(getContext()));
            if(!manager.doesLocalCacheExist()){
                Log.d(TAG, "LOCAL-CACHE variable not set");
                while (i.hasNext()) {
                    MyTask myTask = (MyTask) i.next();
                    //if (myTask.getId() != null) Log.d(TAG, myTask.getId().toString());
                    Log.d(TAG, " Inserting Task. Description: " + myTask.getTaskDescription());
                    manager.convertRemotetoLocalTask(myTask);
                }
                Log.d(TAG, "setting the LOCAL-CACHE variable...");
                manager.initializeLocalCache();
                Log.d(TAG, "LOCAL-CACHE is " + MyServerSettings.getLocalTaskCache(getContext()));
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
            Log.i(TAG, "BOOMnum items is: " + numItems);
           if(numItems == 2) mCursor = manager.queryTasks(numItems);
            else mCursor = manager.queryTasks();
            TaskCursorAdapter adapter = new TaskCursorAdapter(getActivity(), mCursor);
            adapter.notifyDataSetChanged();
            setListAdapter(adapter);

            //dismissProgressDialog();
        }

     /*   protected void dismissProgressDialog() {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }*/
    }
}
