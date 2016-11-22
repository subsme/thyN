package com.thyn.task.view.edit;

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
 * Created by shalu on 10/26/16.
 */
public class CancelTaskDialogFragment extends DialogFragment {
        public static final String EXTRA_TEXT =
                "com.android.android.thyn.form.view.taskdescription";
        private String mTaskTitle = null;

        public static CancelTaskDialogFragment newInstance(String str){
            Bundle args = new Bundle();
            args.putSerializable(EXTRA_TEXT, str);

            CancelTaskDialogFragment fragment = new CancelTaskDialogFragment();
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            mTaskTitle = (String)getArguments().getSerializable(EXTRA_TEXT);
            View v = getActivity().getLayoutInflater()
                    .inflate(R.layout.dialog_cancel_task, null);
            TextView textView = (TextView)v.findViewById(R.id.dialog_accept_task);
            textView.setText(R.string.cancel_task_dialog_title);
            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle("Cancel " + mTaskTitle)
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

