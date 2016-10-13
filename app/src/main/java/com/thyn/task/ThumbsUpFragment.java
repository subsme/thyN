package com.thyn.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thyn.R;
import com.thyn.connection.PollService;
import com.thyn.tab.DashboardActivity;

import org.w3c.dom.Text;

public class ThumbsUpFragment extends Fragment {

    private Button mGoToDashboard;
    private TextView mTitle;
    private TextView mDescription;
    public static final String EXTRA_THUMBS_TITLE =
            "com.thyn.tasklist.my.ThumbsUpFragment.Title";
    public static final String EXTRA_THUMBS_DESCRIPTION =
            "com.thyn.tasklist.my.ThumbsUpFragment.Description";
    public ThumbsUpFragment() {
        // Required empty public constructor
    }
    public static ThumbsUpFragment newInstance(String title, String desc) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_THUMBS_TITLE, title);
        args.putSerializable(EXTRA_THUMBS_DESCRIPTION, desc);

        ThumbsUpFragment fragment = new ThumbsUpFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String desc = (String) getArguments().getSerializable(EXTRA_THUMBS_DESCRIPTION);
        String title = (String) getArguments().getSerializable(EXTRA_THUMBS_TITLE);

        View view = inflater.inflate(R.layout.fragment_thumbs_up, container, false);
        mGoToDashboard = (Button)view.findViewById(R.id.back_to_dashboard);
        mGoToDashboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent =
                        new Intent(getActivity(), DashboardActivity.class);
                intent.putExtra(DashboardActivity.REFRESH_NEEDED, true);
                startActivity(intent);

            }
        });
        mDescription = (TextView)view.findViewById(R.id.desc);
        mTitle = (TextView)view.findViewById(R.id.title);
        /*
        desc and title are not set when a new task is created.
        These are set only when someone accepts a task.
         */
        if(desc != null) mDescription.setText(desc);
        if(title != null) mTitle.setText(title);

        return view;
    }



}
