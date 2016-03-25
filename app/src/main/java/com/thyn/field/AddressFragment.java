package com.thyn.field;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.view.View.OnKeyListener;
import com.thyn.R;
/**
 * Created by shalu on 2/24/16.
 */
public class AddressFragment extends Fragment {
    private static final String TAG = "AddressFragment";
    public static final String EXTRA_ADDRESS_ID =
            "com.android.android.thyn.address";
    public static final String EXTRA_TO_ADDRESS_ID =
            "com.android.android.thyn.to_address";
    private EditText mTaskFromAddress;

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "AddressFragment started");
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public static AddressFragment newInstance(String location){
        Bundle args = new Bundle();
        args.putSerializable("t", location);

        AddressFragment fragment = new AddressFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.address_task, container, false);
        Log.d(TAG, "onCreateView");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        mTaskFromAddress = (EditText)v.findViewById(R.id.task_address);
        mTaskFromAddress.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Log.d(TAG, mTaskFromAddress.getText().toString());
                    Intent i = new Intent();
                    String t = (String)getArguments().getSerializable("t");
                    if(t.equals("from"))
                        i.putExtra(AddressFragment.EXTRA_ADDRESS_ID,mTaskFromAddress.getText().toString());
                    else if(t.equals("to"))
                        i.putExtra(AddressFragment.EXTRA_TO_ADDRESS_ID,mTaskFromAddress.getText().toString());
                    getActivity().setResult(getActivity().RESULT_OK, i);
                    getActivity().finish();
                    return true;
                }
                return false;

            }
        });

        return v;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
