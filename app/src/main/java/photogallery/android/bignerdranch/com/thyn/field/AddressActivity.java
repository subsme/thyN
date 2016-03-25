package photogallery.android.bignerdranch.com.thyn.field;

import android.support.v4.app.Fragment;

import photogallery.android.bignerdranch.com.thyn.SingleFragmentActivity;

/**
 * Created by shalu on 2/24/16.
 */
public class AddressActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        String t = (String)getIntent()
                .getSerializableExtra("t");
        return AddressFragment.newInstance(t);
    }
}
