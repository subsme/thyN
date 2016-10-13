package com.thyn.task;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;
import com.thyn.task.view.TaskPagerViewOnlyFragment;

/**
 * Created by shalu on 10/5/16.
 */
public class ThumbsUpActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        String title = (String)getIntent()
                .getSerializableExtra(ThumbsUpFragment.EXTRA_THUMBS_TITLE);
        String desc = (String)getIntent()
                .getSerializableExtra(ThumbsUpFragment.EXTRA_THUMBS_DESCRIPTION);
        return ThumbsUpFragment.newInstance(title, desc);
    }
}
