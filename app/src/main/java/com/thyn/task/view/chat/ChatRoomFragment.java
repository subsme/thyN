package com.thyn.task.view.chat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thyn.R;
import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.collection.MessageLocal;
import com.thyn.android.backend.myTaskApi.model.Message;
import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.AppStatus;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.navigate.NavigationActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "MESSAGE_FROM_CREATOR_TO_HELPER";
    private static String TAG = "ChatRoomFragment";
    // TODO: Rename and change types of parameters
    private Long mTaskID;
    private boolean mMessageFromCreatorToHelper;

    private EditText inputMessage;
    private Button btnSend;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<MessageLocal> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public Context _context;



    public ChatRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param taskID Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatRoomFragment newInstance(Long taskID, boolean param2) {
        ChatRoomFragment fragment = new ChatRoomFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, taskID);
        args.putBoolean(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate().....");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTaskID = getArguments().getLong(ARG_PARAM1);
            mMessageFromCreatorToHelper = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "in onCreateView().....");
        String broadcast = (String)getActivity().getIntent().getStringExtra("broadcast");
        if(broadcast != null) Log.d(TAG, "A BROADCAST OPENED THIS CHATROOM");
        else Log.d(TAG, "CHATROOM didnt Open because of BROADCAST");
        View v = inflater.inflate(R.layout.fragment_chat_room, container, false);
        if (!AppStatus.getInstance(getActivity()).isOnline()) {
            Toast.makeText(getActivity().getApplicationContext(), "Sorry! There is no Internet connection", Toast.LENGTH_LONG).show();
            Log.v(TAG, "############Not online!!!!#########");
            return v;
        }
        // Inflate the layout for this fragment
        inputMessage = (EditText) v.findViewById(R.id.message);
        btnSend = (Button) v.findViewById(R.id.btn_send);
        messageArrayList = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        /* Subu 05/11/17 to check if this is going to work after implementing */
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* Subu 05/11/17 setting the adapter here. */
        // self user id is to identify the message owner
        Long selfUserId = MyServerSettings.getUserProfileId(getActivity().getApplicationContext());
        mAdapter = new ChatRoomThreadAdapter(getActivity(), messageArrayList, selfUserId);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    handlePushNotification(intent);
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                layoutManager.scrollToPosition(messageArrayList.size() - 1);
            }
        });
 /*Subu I am hiding the floating button that is available in the NavigationActivity layout (see the include file - app_bar_navigation.xml

         */
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).hideFloatingActionButton();
        }
        fetchChatThread();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            String sender = intent.getStringExtra("sender");
            String profileID = intent.getStringExtra("profileID");
            //do other stuff here
            Log.d(  TAG,
                    "The message got from Broadcast receiver is: " + message +
                    ", Sender is: " + sender +
                    ", Profile ID  is: " + profileID);
            MessageLocal msglocal = new MessageLocal();
            msglocal.setMessage(message);
            msglocal.setUser(sender.indexOf(" ")>0?sender.trim().substring(0,sender.indexOf(" ")):sender.trim());
            msglocal.setUserKey(profileID==null?0:Long.parseLong(profileID));
            SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date now = new Date();
            msglocal.setCreatedAt(sdfdate.format(now));
            messageArrayList.add(msglocal);
            mAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    };

    @Override
    public void onResume(){
        Log.d(TAG, "in onResume().....");
        super.onResume();
        //fetchChatThread();
        /* Subu 09/20/17 Registering the receiver here. See https://stackoverflow.com/questions/22252065/refreshing-activity-on-receiving-gcm-push-notification*/
        if(_context == null) _context = getContext();
        _context.registerReceiver(mMessageReceiver, new IntentFilter("unique_chat_name"));
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Subu 09/20/17 Unregistering the receiver here. See https://stackoverflow.com/questions/22252065/refreshing-activity-on-receiving-gcm-push-notification */
        _context.unregisterReceiver(mMessageReceiver);
    }
    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     * */
    private void sendMessage() {
        final String message = this.inputMessage.getText().toString().trim();
        Log.d(TAG, "in sendmessage and message about to be sent to asynch task is: " + message);

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity().getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        new SendChatMessageAsyncTask(message, MyServerSettings.getUserProfileId(getActivity()), this.mTaskID).execute();
        this.inputMessage.setText("");

    }
    /**
     * Fetching all the messages of a single chat room
     * */
    private void fetchChatThread() {
        new ReceiveChatRoomMessagesAsyncTask(MyServerSettings.getUserProfileId(getActivity()), this.mTaskID).execute();
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        MessageLocal message = (MessageLocal) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    private class SendChatMessageAsyncTask extends AsyncTask<Void, Void, MessageLocal> {

        private String message;
        private Long profileID;
        private Long taskID;

        public SendChatMessageAsyncTask(String message, Long profileID, Long taskID) {
            super();
            this.message = message;
            this.profileID = profileID;
            this.taskID = taskID;
        }
        @Override
        protected MessageLocal doInBackground(Void... params) {
            MessageLocal msglocal = null;
            if(_context == null) _context = getContext();
            try {
                msglocal = new MessageLocal();
                msglocal.setMessage(this.message);
                msglocal.setUser(MyServerSettings.getUserFirstName(_context));
                msglocal.setUserKey(profileID);
                SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date now = new Date();
                msglocal.setCreatedAt(sdfdate.format(now));
                Log.d(TAG, "calling sendChatMessage and message is: " + this.message);
                if(mMessageFromCreatorToHelper) GoogleAPIConnector.connect_TaskAPI().sendChatMessageToTaskHelper(message,profileID,taskID).execute();
                else GoogleAPIConnector.connect_TaskAPI().sendChatMessageToTaskCreator(message,profileID,taskID).execute();
            } catch (IOException e) {
                e.getMessage();
            }
            return msglocal;
        }

        @Override
        protected void onPostExecute(MessageLocal msglocal) {
            messageArrayList.add(msglocal);
            mAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }
    private class ReceiveChatRoomMessagesAsyncTask extends AsyncTask<Void, Void, List> {

        private Long profileID;
        private Long taskID;
        public ReceiveChatRoomMessagesAsyncTask(Long profileID, Long taskID) {
            super();
            this.profileID = profileID;
            this.taskID = taskID;
        }
        @Override
        protected List doInBackground(Void... params) {
            List l = null;
            try {
                Log.d(TAG, "calling ReceiveChatRoomMessagesAsyncTask to get all the chat messages for this chat room " + this.taskID);
                //GoogleAPIConnector.connect_TaskAPI().sendChatMessage(message,profileID,taskID).execute();
                l = GoogleAPIConnector.connect_TaskAPI().getChatRoomMessages(profileID,taskID).execute().getItems();
            } catch (Exception e) {
                e.getMessage();
            }

            return l;
        }

        @Override
        protected void onPostExecute(List result) {
            if (result == null) return;

            Iterator i = result.iterator();
            Log.d(TAG, "The data count sent from the server is: " + result.size());
            while (i.hasNext()) {
                Message message = (Message) i.next();
                Log.i(TAG, message.getContent());
                MessageLocal msglocal = new MessageLocal();
                if(message.getContent() != null) {
                    msglocal.setId(message.getId());
                    msglocal.setMessage(message.getContent());
                    msglocal.setCreatedAtInMillis(message.getMessageTime().getValue());
                    msglocal.setCreatedAt(message.getMessageTime().toString());
                    msglocal.setUser(message.getUserFromName());
                    msglocal.setUserKey(message.getUserKey());
                    messageArrayList.add(msglocal);
                    Collections.sort(messageArrayList,new MessageLocalComparator());
                    /*Log.d(TAG, "Sorted list entries: ");
                    for(MessageLocal ml: messageArrayList){
                        Log.d(TAG, ml.getMessage() + "-%-" + ml.getCreatedAtInMillis() + "-%-" + ml.getCreatedAt());
                    }*/
                }
            }

           recyclerView.setAdapter(mAdapter);// Subu 05/11 - Does this fix the issue.
           mAdapter.notifyDataSetChanged();
        }

    }

    class MessageLocalComparator implements Comparator<MessageLocal>{
        @Override
        public int compare(MessageLocal m1, MessageLocal m2) {
            if(m1.getCreatedAtInMillis() < m2.getCreatedAtInMillis()){
                return -1;
            } else {
                return 1;
            }
        }
    }
}
