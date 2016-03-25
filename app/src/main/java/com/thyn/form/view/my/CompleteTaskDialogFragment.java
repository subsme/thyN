package com.thyn.form.view.my;

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
public class CompleteTaskDialogFragment extends DialogFragment{
    public static final String EXTRA_TEXT =
            "com.android.android.thyn.form.view.my.taskdescription";
    private String mTaskDescription = null;

    public static CompleteTaskDialogFragment newInstance(String str){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TEXT, str);

        CompleteTaskDialogFragment fragment = new CompleteTaskDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        mTaskDescription = (String)getArguments().getSerializable(EXTRA_TEXT);
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_accept_task, null);
        TextView textView = (TextView)v.findViewById(R.id.dialog_accept_task);
        textView.setText(mTaskDescription);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.accept_task_dialog_title)
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
