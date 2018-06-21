package com.thyn.user.userGroup;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thyn.R;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JoinUserGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JoinUserGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JoinUserGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView name_groupTitle;
    private EditText val_groupTitle;
    private Button button_JoinGroup;
    private Button button_StartGroup;
    private Context context;
    private static final String TAG = "JoinUserFragment";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public JoinUserGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JoinUserGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JoinUserGroupFragment newInstance(String param1, String param2) {
        JoinUserGroupFragment fragment = new JoinUserGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_join_user_group, container, false);
        name_groupTitle = (TextView) v.findViewById(R.id.group_text_header);
        val_groupTitle = (EditText) v.findViewById(R.id.join_group_text);
        val_groupTitle.setHint("Please enter the Group Name");
        button_JoinGroup = (Button) v.findViewById(R.id.join_group_btn);
        Log.d(TAG, "In OnCreateView...");
        button_JoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Join group button clicked");
                String groupVal = null;
                if(val_groupTitle.getText() != null){
                    groupVal = val_groupTitle.getText().toString();
                    new JoinGroupAsync().execute(groupVal, "false");
                }
            }
        });

        button_StartGroup = (Button) v.findViewById(R.id.start_group_btn);
        button_StartGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "Start group button clicked");
                String groupVal = null;
                if(val_groupTitle.getText() != null){
                    groupVal = val_groupTitle.getText().toString();
                    new JoinGroupAsync().execute(groupVal, "true");
                }
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private class JoinGroupAsync extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                String userGroup = params[0];
                String is_StartGroupAction = params[1];
                context = getActivity();
                String socialID = MyServerSettings.getUserSocialId(context);
                int socialType = MyServerSettings.getUserSocialType(context);


                APIGeneralResult result = null;
                if (is_StartGroupAction.equals("true")){
                    result = GoogleAPIConnector.connect_UserAPI().startGroup(userGroup, socialID, socialType).execute();
                    Log.d(TAG, "Cicked on start group");
                }
                 else{
                    result = GoogleAPIConnector.connect_UserAPI().joinGroup(userGroup, socialID, socialType).execute();
                    Log.d(TAG, "Clicked on join group");
                }
           } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getActivity(), "Thanks! You have joined the Group.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
