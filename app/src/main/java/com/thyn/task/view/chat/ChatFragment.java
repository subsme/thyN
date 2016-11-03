package com.thyn.task.view.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import com.thyn.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by shalu on 10/24/16.
 */
public class ChatFragment extends DialogFragment {
        public static final String CHAT_MESSAGE =
                "com.thyn.task.view.chat.message";
        private String message = null;

        public static ChatFragment newInstance(){

            ChatFragment fragment = new ChatFragment();
            return fragment;
        }

        private void sendResult(int resultCode){
            if(getTargetFragment() == null){
                return;
            }
            Intent i = new Intent();
            i.putExtra(CHAT_MESSAGE, this.message);
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            View v = getActivity().getLayoutInflater()
                    .inflate(R.layout.chatmessage, null);


            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle("Message")
                            // .setPositiveButton(android.R.string.ok, null) big nerd ranch page 222
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendResult(Activity.RESULT_OK);
                                    message = "ok this is the messsage";
                                }
                            }

                    )
                    .create();
        }
    }
