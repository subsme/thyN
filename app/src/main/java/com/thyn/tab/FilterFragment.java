package com.thyn.tab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thyn.R;
import com.thyn.collection.Filter;
import com.thyn.common.MyServerSettings;
import com.thyn.navigate.NavigationActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {
    private static final String TAG = "FilterFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static int previousFilterSelected = 1;
    private static int previousFilterRadiusSelected = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
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
        View v = inflater.inflate(R.layout.fragment_filter, container, false);
        final Filter filter = new Filter(false, false, false, 10);
        RadioButton mostRecent = (RadioButton) v.findViewById(R.id.check_MostRecent);

        filter.distanceRadius = previousFilterRadiusSelected;
        if(previousFilterSelected==3)
            mostRecent.setChecked(true);

        mostRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    // perform logic
                    Log.d(TAG, "mostrecent radio button clicked");
                    filter.mostRecent = true;
                    filter.expiringSoon = false;
                    filter.closestToMyHome = false;

                }

            }
        });

        RadioButton expiringSoon = ( RadioButton ) v.findViewById(R.id.check_ExpiringSoon);
        if(previousFilterSelected==2)
            expiringSoon.setChecked(true);
        expiringSoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    // perform logic

                    filter.expiringSoon = true;
                    filter.closestToMyHome = false;
                    filter.mostRecent = false;

                }

            }
        });
        RadioButton check_ClosestHome = ( RadioButton ) v.findViewById(R.id.check_ClosestHome);

        if(previousFilterSelected==1)
            check_ClosestHome.setChecked(true);

        check_ClosestHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    // perform logic

                    filter.closestToMyHome = true;
                    filter.mostRecent = false;
                    filter.expiringSoon = false;

                }

            }
        });
        SeekBar sbc = (SeekBar) v.findViewById(R.id.seekBar);

        final TextView dist_txt = (TextView) v.findViewById(R.id.distance_text);

        if(previousFilterRadiusSelected != 0) {
            sbc.setProgress(previousFilterRadiusSelected);
            dist_txt.setText(String.valueOf(previousFilterRadiusSelected) + " miles");
        }

        sbc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Log.d(TAG, String.valueOf(progress));
                dist_txt.setText(String.valueOf(progress) + " miles");
                filter.distanceRadius=progress;
            }
        });



        Button applyFilters = (Button)v.findViewById(R.id.apply_filters);
        applyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Filter Button clicked");
                if(!filter.closestToMyHome && !filter.expiringSoon && !filter.mostRecent){
                    if(previousFilterSelected==2)
                        filter.expiringSoon = true;
                    else if(previousFilterSelected==1)
                        filter.closestToMyHome = true;
                    else filter.mostRecent = true;

                }
                onButtonPressed(filter);
                //getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        Button  cancel = (Button)v.findViewById(R.id.cancel_action);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel Button clicked");
                if(!filter.closestToMyHome && !filter.expiringSoon && !filter.mostRecent){
                    if(previousFilterSelected==2)
                        filter.expiringSoon = true;
                    else if(previousFilterSelected==1)
                        filter.closestToMyHome = true;
                    else filter.mostRecent = true;

                }
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
         /* Subu Hiding the floating action button
        * that was created in NavigationActivity.
         */
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).hideFloatingActionButton();
        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Filter filter) {
        if (mListener != null) {
            mListener.onFragmentInteraction(filter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        //Subu code Nov 30, 2016
        previousFilterSelected = MyServerSettings.getFilterSetting(context);
        previousFilterRadiusSelected = MyServerSettings.getFilterRadius(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Filter filter);
    }
}
