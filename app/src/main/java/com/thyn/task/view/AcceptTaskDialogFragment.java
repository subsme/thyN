package com.thyn.task.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.thyn.R;

/**
 * Created by shalu on 3/14/16.
 */
public class AcceptTaskDialogFragment extends DialogFragment{
    public static final String EXTRA_TEXT =
            "com.android.android.thyn.form.view.taskdescription";
    private String mTaskTitle = null;

    public static AcceptTaskDialogFragment newInstance(String str){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TEXT, str);

        AcceptTaskDialogFragment fragment = new AcceptTaskDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        mTaskTitle = (String)getArguments().getSerializable(EXTRA_TEXT);
       // View v = getActivity().getLayoutInflater()
       //         .inflate(R.layout.dialog_accept_task, null);
       // TextView textView = (TextView)v.findViewById(R.id.dialog_accept_task);
        //textView.setText(R.string.accept_task_dialog_title);
       // textView.setText("Thanks for helping " + mTaskTitle + ". Please Confirm.");
        //TextView textViewMsg = (TextView)v.findViewById(R.id.txtDiagMsg);
        //textViewMsg.setText("Thanks for helping " + mTaskTitle + ".");

        /*return new AlertDialog.Builder(getActivity())
                .setView(v)
                //.setTitle("Thanks for helping " + mTaskTitle)
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
                .create();*/
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_accept_task);
        TextView textView = (TextView)dialog.findViewById(R.id.dialog_accept_task);
        //textView.setText(R.string.accept_task_dialog_title);
        textView.setText("Thanks for helping " + mTaskTitle + ". Please Confirm.");
        Button dialogOKButton = (Button) dialog.findViewById(R.id.btnOk);
        // if button is clicked, close the custom dialog
        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK);
                dialog.dismiss();

            }
        });
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the custom dialog
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_CANCELED);
                dialog.dismiss();

            }
        });
        return dialog;

    }

    private void sendResult(int resultCode){
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode, null);
    }
}
