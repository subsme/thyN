package com.thyn.tasklist.iwillhelp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thyn.R;
import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.collection.MyTaskLab;
import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.db.thynTaskDBHelper;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyActivity;
import com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by subu sundaram on 10/11/16.
 */
public class IWillHelpTaskListFragment extends ListFragment{
        private static final String TAG = "IWillHelpTaskListFragment";

        private SwipeRefreshLayout mSwipeRefreshLayout;

        private MyTaskLab manager;
        private thynTaskDBHelper.TaskCursor mCursor;
        private static Long userprofileid = null;
        private static boolean toRefresh;

        public static final IWillHelpTaskListFragment newInstance(boolean refresh){
            toRefresh = refresh;
            IWillHelpTaskListFragment tlf = new IWillHelpTaskListFragment();
            return tlf;
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            userprofileid = MyServerSettings.getUserProfileId(getActivity());
            getActivity().setTitle(R.string.task_title);

            if(manager == null) manager = MyTaskLab.get(getActivity());
        }

        @Override
        public void onResume(){
            Log.d(TAG, "onResume() called...");
            super.onResume();
            if(manager == null) manager = MyTaskLab.get(getActivity());

            loadData();
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
            if(!MyServerSettings.getLocalTaskCache(getActivity()) || toRefresh) {
                Log.d(TAG, "No cache exists. Getting the latest tasks from server...Will Take some time...");
                //callAsyncThread(getArguments());
                refreshContent();
                Log.d(TAG, "LOCAL-CACHE is " + MyServerSettings.getLocalTaskCache(getContext()));
            }
            else {
                Log.d(TAG, "yes. there is cache now. Retrieving from cache...");

                mCursor = manager.queryTasksIWillHelp(userprofileid);
                if (mCursor.moveToFirst()) Log.d(TAG, "Cursor count is: " + mCursor.getCount());
                else Log.d(TAG, "Cursor already in first. Its count is: " + mCursor.getCount());
                TaskCursorAdapter adapter = new TaskCursorAdapter(getActivity(), mCursor);
                adapter.notifyDataSetChanged();
                setListAdapter(adapter);
            }

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

                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id){
            // The id argument will be Task ID; CursorAdapter gives this to us for FREE
            Log.d(TAG, "The id given by the cursor adapter is: " + id);

            Intent i = new Intent(getActivity(), TaskIWillHelpPagerViewOnlyActivity.class);
            i.putExtra(TaskIWillHelpPagerViewOnlyFragment.EXTRA_TASK_ID, id);
            startActivity(i);
        }

        private void refreshContent(){
            //remove the local database and then refresh content.
        /*Subu: Aug 19/2016
        TODO - need to correct this logic because we included a PollService.
         */
            manager.purgeTasks();
            Log.d(TAG, "In refreshContent()");
            new RetrieveFromServerAsyncTask(getActivity()).execute();

        }

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

            }

        }
        // End inner class TaskCursorAdapter


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v= null;
            v = inflater.inflate(R.layout.fragment_tasklist_nobutton, container, false);
            ListView listView = (ListView)v.findViewById(android.R.id.list);
            listView.setEmptyView(v.findViewById(android.R.id.empty));
            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "Refreshing content");
                    refreshContent();
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
                    Log.d(TAG, "Sending user profile id:"+userprofileid);
                    //l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, userprofileid, false).execute().getItems();
                    l = GoogleAPIConnector.connect_TaskAPI().listTasks(MyServerSettings.getFilterRadius(c), false, MyServerSettings.getUserSocialId(c), MyServerSettings.getUserSocialType(c), false).execute().getItems();

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
                mCursor = manager.queryTasksIWillHelp(userprofileid);
                TaskCursorAdapter adapter = new TaskCursorAdapter(getActivity(), mCursor);
                adapter.notifyDataSetChanged();
                setListAdapter(adapter);

            }


        }
    }
