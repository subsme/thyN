package photogallery.android.bignerdranch.com.thyn.tasklist;

import android.support.v4.app.Fragment;

import photogallery.android.bignerdranch.com.thyn.SingleFragmentActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class TaskListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() { return new TaskListFragment();}

}
