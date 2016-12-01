package com.thyn.tab;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thyn.R;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.AppStatus;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.graphics.SquareImageGridAdapter;
import com.thyn.navigate.NavigationActivity;
import com.thyn.tab.view.SlidingTabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomePageFragment extends Fragment {

    private static String TAG="WelcomePageFragment";
    private ViewPager mViewPager;
    private SlidingTabsBasicFragment.SamplePagerAdapter mSamplePagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;

    public WelcomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome_page, container, false);
        Context c = getContext();
        TextView welcomeText = (TextView) view.findViewById(R.id.title_text);
        welcomeText.setText("Welcome, " + MyServerSettings.getUserFirstName(c) + "!");
        MLRoundedImageView user_profile_image = (MLRoundedImageView) view.findViewById(R.id.user_profile_image);
        Log.d(TAG, MyServerSettings.getUserProfileImageURL(c));
        Picasso.with(c)
                // .load("https://scontent.xx.fbcdn.net/v/t1.0-1/p50x50/11156187_10205188530126207_4481467444246362898_n.jpg?oh=2dee76ec7e202649b84c7a71b4c86721&oe=58ADEBE1")
                .load(MyServerSettings.getUserProfileImageURL(c))
                .into(user_profile_image);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        SquareImageGridAdapter sg = new SquareImageGridAdapter(getActivity());
        sg.setGrid1(MyServerSettings.getNumNeighbrsIHelped(c) + "", "Neighbrs I have helped");
        sg.setGrid2(MyServerSettings.getPoints(c) + "", "thyNeighbr points");

        gridView.setAdapter(sg);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
        mSamplePagerAdapter = fragment.new SamplePagerAdapter(getChildFragmentManager());
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        mViewPager.setAdapter(mSamplePagerAdapter);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.tab_highlighter);
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);

        /*Subu I am hiding the floating button that is available in the NavigationActivity layout (see the include file - app_bar_navigation.xml

         */
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).hideFloatingActionButton();
        }
        if (!AppStatus.getInstance(getActivity()).isOnline()) {
            Toast.makeText(getActivity().getApplicationContext(), "Sorry! There is no Internet connection", Toast.LENGTH_LONG).show();
            Log.v(TAG, "############Not online!!!!#########");

        }

        return view;
    }

}
