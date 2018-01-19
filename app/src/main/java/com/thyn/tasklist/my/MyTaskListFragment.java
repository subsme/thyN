package com.thyn.tasklist.my;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thyn.android.backend.myTaskApi.MyTaskApi;
import com.thyn.android.backend.myTaskApi.model.MyTask;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.thyn.collection.MyPersonalTaskLab;
import com.thyn.collection.MyTaskLab;
import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.db.thynTaskDBHelper;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.task.view.TaskPagerViewOnlyFragment;
import com.thyn.task.view.edit.TaskPagerEditOnlyFragment;
import com.thyn.R;

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
    private ProgressDialog dialog;
    private MyPersonalTaskLab pmanager;
    private thynTaskDBHelper.TaskCursor mCursor;
    private static Long userprofileid = null;
    private int numRowsToShow;

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
        //getActivity().setTitle(R.string.task_title);

        //if(pmanager == null) pmanager = MyPersonalTaskLab.get(getActivity());

        Log.d(TAG, " in onCreate ");
        Log.d(TAG, "LOCAL-CACHE is " + MyServerSettings.getLocalTaskCache(getContext()));
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
        numRowsToShow = getArguments().getInt(EXTRA_NUM_ITEMS);
        if(!MyServerSettings.getLocalTaskCache(getActivity())){
            Log.d(TAG, "no cache exists...");

            refreshContent();
            Log.d(TAG, "LOCAL-CACHE is " + MyServerSettings.getLocalTaskCache(getContext()));
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


   @Override
   public void onListItemClick(ListView l, View v, int position, long id){
       //Task t = ((TaskAdapter)getListAdapter()).getItem(position);
       Log.d(TAG, id + " was clicked"); //page 191 big nerdranch
       Log.d(TAG, "The id given by the cursor adapter is: " + id);

       Task t = MyTaskLab.get(getActivity()).getTask(id);
       Long userprofileid = MyServerSettings.getUserProfileId(getActivity());
       Log.i(TAG, "user profile id is: " + userprofileid);
       Log.i(TAG, "my profile key is: " + t.getUserProfileKey());
       Intent i = null;
       if(userprofileid.equals(t.getUserProfileKey())) {
           Log.i(TAG, "the ids are the same");
           TaskPagerEditOnlyFragment tpEditOnlyFragment = TaskPagerEditOnlyFragment.newInstance(id);
           FragmentManager manager = getActivity().getSupportFragmentManager();
           manager.beginTransaction().replace(R.id.navigation_fragment_container,
                   tpEditOnlyFragment,
                   tpEditOnlyFragment.getTag()).commit();
       }
       else{
           Log.i(TAG, "the ids are not the same");
           TaskPagerViewOnlyFragment tpViewOnlyFragment = TaskPagerViewOnlyFragment.newInstance(id);
           FragmentManager manager = getActivity().getSupportFragmentManager();
           manager.beginTransaction().replace(R.id.navigation_fragment_container,
                   tpViewOnlyFragment,
                   tpViewOnlyFragment.getTag()).commit();
       }

   }
    private void refreshContent(){
        //remove the local database and then refresh content.
        /*Subu: Aug 19/2016
        TODO - need to correct this logic because we included a PollService.
         */
        pmanager.purgeTasks();
        Log.d(TAG, "In refreshContent()");
        new RetrieveFromServerAsyncTask(getContext()).execute();
    }
    public void refreshContent(MyPersonalTaskLab pmanagerLocal, Context c){
        pmanagerLocal.purgeTasks();
        Log.d(TAG, "Calling refreshContent(MyPersonalTaskLab");
        new RetrieveFromServerAsyncTask(c).execute();
    }

    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
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
          MLRoundedImageView imageView = (com.thyn.graphics.MLRoundedImageView) view.findViewById(R.id.task_list_user_image);
          if(task.getImageURL() != null) {
              Picasso.with(context)
                      //.load("https://scontent.xx.fbcdn.net/v/t1.0-1/p50x50/11156187_10205188530126207_4481467444246362898_n.jpg?oh=2dee76ec7e202649b84c7a71b4c86721&oe=58ADEBE1")
                      .load(task.getImageURL())
                      .into(imageView);
          }

          // Set up the text view values
          TextView descTextView = (TextView)view.findViewById(R.id.task_list_item_titleTextView);
          descTextView.setText(task.getTaskTitle());

          TextView dateTextView = (TextView)view.findViewById(R.id.task_list_item_createDateTextView);
          if(task.getCreateDate()!=null) dateTextView.setText(task.getDateReadableFormat());
          TextView userTextView = (TextView)view.findViewById(R.id.task_list_item_User);
          userTextView.setText(task.getUserProfileName());
          TextView locationTextView = (TextView)view.findViewById(R.id.task_list_item_locationTextView);
          locationTextView.setText(task.getCity());

          double dist = task.getDistance();
          Log.d(TAG, "The task list distance is " + task.getDistance());

          TextView distanceTextView = (TextView)view.findViewById(R.id.task_distance);
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
        int numItems = getArguments().getInt(EXTRA_NUM_ITEMS, 0);
        Log.d(TAG, "In onCreateView, numitems is " + numItems);

        if(numItems == 2) {
            v = inflater.inflate(R.layout.fragment_tasklist1, container, false);
            ListView listView = (ListView)v.findViewById(android.R.id.list);
            listView.setEmptyView(v.findViewById(android.R.id.empty));
            ImageView img_view = (ImageView) v.findViewById(R.id.expand);
            img_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent intent = new Intent(getActivity().getApplicationContext(), DashboardActivity.class);
                    intent.putExtra(DashboardActivity.SELECT_SECOND_TAB, true);
                    startActivity(intent);*/
                    MyTaskListFragment myTaskListFragment = MyTaskListFragment.newInstance(true);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.navigation_fragment_container,
                            myTaskListFragment,
                            myTaskListFragment.getTag()).commit();
                }
            });
        }
        else{
            v = inflater.inflate(R.layout.fragment_tasklist_nobutton, container, false);
            // BUG: Subu i see "no tasks available when there are no tasks.
             ListView listView = (ListView)v.findViewById(android.R.id.list);
            listView.setEmptyView(v.findViewById(android.R.id.empty));
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

        private Context c = null;
        public RetrieveFromServerAsyncTask(Context c) {
            super();
            this.c = c;
        }
        @Override
        protected List doInBackground(Void... params) {
            List l = null;

            try {
                userprofileid = MyServerSettings.getUserProfileId(getContext());
                Log.d(TAG, "Sending user profile id:"+userprofileid);
                Log.d(TAG, "Sending distance filter: " +  MyServerSettings.getFilterRadius(c));
                //l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, userprofileid, false).execute().getItems();
                l = GoogleAPIConnector.connect_TaskAPI().listTasks(MyServerSettings.getFilterRadius(c), false, MyServerSettings.getUserSocialId(c), MyServerSettings.getUserSocialType(c), true).execute().getItems();

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
            if(numRowsToShow == 2)
                mCursor = pmanager.queryMyTasks(userprofileid,numRowsToShow);
            else
                mCursor = pmanager.queryMyTasks(userprofileid);
            MyTaskCursorAdapter adapter = new MyTaskCursorAdapter(getActivity(), mCursor);
            adapter.notifyDataSetChanged();
            setListAdapter(adapter);
            dismissProgressDialog();

        }
        protected void dismissProgressDialog() {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

}
