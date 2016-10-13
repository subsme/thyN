package com.thyn.task.view.iwillhelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.thyn.R;

/**
 * Created by shalu on 3/14/16.
 */
public class CancelOrCompleteTaskDialogFragment extends DialogFragment{
    public static final String CANCEL_OR_DONE =
            "com.thyn.task.view.iwillhelp.CancelOrDone";
    public static final String PROFILE_NAME =
            "com.thyn.task.view.iwillhelp.profilename";
    private String mUserName = null;
    public static final int CANCEL = 0;
    public static final int DONE = 1;


    public static CancelOrCompleteTaskDialogFragment newInstance(String str, int type){
        Bundle args = new Bundle();
        args.putSerializable(PROFILE_NAME, str);
        args.putSerializable(CANCEL_OR_DONE, type);
        CancelOrCompleteTaskDialogFragment fragment = new CancelOrCompleteTaskDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        mUserName = (String)getArguments().getSerializable(PROFILE_NAME);
        int i = (int)getArguments().getSerializable(CANCEL_OR_DONE);
        String title = " ";
        String desc = "Thanks for helping, " +  mUserName.substring(0, mUserName.indexOf(" "))
                + ". You have earned 10 points for your kindness.";
        if(i==CANCEL){
            title = "Thanks for trying, " + mUserName.substring(0, mUserName.indexOf(" "));
            desc =  "Thanks for trying, " + mUserName.substring(0, mUserName.indexOf(" "))
                    + ". Hopefully you will get a chance to help you neighbr another time.";
        }
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_accept_task, null);
        TextView textView = (TextView)v.findViewById(R.id.dialog_accept_task);
        textView.setText(title);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(desc)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode){
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode, null);
    }
}
