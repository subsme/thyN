package photogallery.android.bignerdranch.com.thyn.form;

import android.support.v4.app.Fragment;

import photogallery.android.bignerdranch.com.thyn.SingleFragmentActivity;

public class TaskActivity extends SingleFragmentActivity {

   @Override
    protected Fragment createFragment() {
       Long taskID = (Long)getIntent()
               .getSerializableExtra(TaskFragment.EXTRA_TASK_ID);
       return TaskFragment.newInstance(taskID);

   }
}
