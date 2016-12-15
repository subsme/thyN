package com.thyn.intro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.thyn.R;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class BasicProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "BasicProfileFragment";
    private static final int ADDRESS_ACTIVITY_REQUEST_CODE = 1;
    private static double mLAT = 0;
    private static double mLONG = 0;
    private static String mCity;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE=1;
    private EditText addressField;
    private EditText aptNoField;
    private Button button;
    private Context c;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BasicProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BasicProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasicProfileFragment newInstance(String param1, String param2) {
        BasicProfileFragment fragment = new BasicProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_basic_profile, container, false);

        final EditText phoneField = (EditText) v.findViewById(R.id.phone);
        phoneField.setText(MyServerSettings.getUserPhone(c));
        phoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            //String lastChar = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               /* int digits = phoneField.getText().toString().length();
                if (digits > 1)
                    lastChar = phoneField.getText().toString().substring(digits - 1);*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // int digits = phoneField.getText().toString().length();
               // Log.d("LENGTH", "" + digits);
               /* if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        phoneField.append("-");
                    }
                }*/
            }
        });
        addressField = (EditText) v.findViewById(R.id.street_address);
        addressField.setText(MyServerSettings.getUserAddress(c));
        addressField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "address was clicked");
                callPlaceAutocompleteActivityIntent();
            }
        });

        aptNoField = (EditText) v.findViewById(R.id.aptNo);
        aptNoField.setText(MyServerSettings.getUserAptNumber(c));
        aptNoField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "apt No. was clicked");
            }
        });

        button = (Button) v.findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, aptNoField.getText().toString());
                String aptNo = null;
                if(aptNoField.getText() != null){
                    aptNo = aptNoField.getText().toString();
                }
                new SendMyProfileAsync().execute(phoneField.getText().toString(),
                        addressField.getText().toString(),
                        aptNo,
                        Double.toString(mLAT),
                        Double.toString(mLONG),
                        mCity);

            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(c, data);
                Log.i(TAG, "Place:" + place.toString());
                addressField.setText(place.getAddress());

                LatLng LatLong = place.getLatLng();
                mLAT = LatLong.latitude;
                mLONG = LatLong.longitude;
                Geocoder mGeocoder = new Geocoder(c, Locale.getDefault());
                try {
                    List<Address> addresses = mGeocoder.getFromLocation(mLAT, mLONG, 1);
                    if (addresses != null && addresses.size() > 0) {
                        this.mCity = addresses.get(0).getLocality();
                        Log.i(TAG, "The city is: " + this.mCity);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(c, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (requestCode == Activity.RESULT_CANCELED) {

            }
        }
    }


    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, 1);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }



    private class SendMyProfileAsync extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                String l_phone = params[0];
                String l_address = params[1];
                String l_aptno = params[2];
                String l_lat = params[3];
                String l_long = params[4];
                String l_city = params[5];

//                Long userprofileid = MyServerSettings.getUserProfileId(getParent());
                String socialID = MyServerSettings.getUserSocialId(c);
                int socialType = MyServerSettings.getUserSocialType(c);

                if(l_phone      == null) l_phone        = MyServerSettings.getUserPhone(c);
                if(l_address    == null) l_address      = MyServerSettings.getUserAddress(c);
               /* l_aptNo can be null. A user can go to a new home (not apartments) and set the apt no. to null. */
                if(l_lat        == null) l_lat          = MyServerSettings.getUserLAT(c);
                if(l_long       == null) l_long         = MyServerSettings.getUserLNG(c);
                if(l_lat.equalsIgnoreCase("0.0") && l_long.equalsIgnoreCase("0.0")){ //This is somewhere in the ocean near Ghana. Not to worry about it.
                    l_lat          = MyServerSettings.getUserLAT(c);
                    l_long         = MyServerSettings.getUserLNG(c);
                }
                if(l_city       == null) l_city         = MyServerSettings.getUserCity(c);

                Log.d(TAG, "CAlling the SErver...Sending user profile id: " + socialID
                                + ", phone: " + l_phone
                                + ", address: " + l_address
                                + ", aptNo: " + l_aptno
                                + ", LAT: " + l_lat
                                + ", LONG: " + l_long
                );
                //APIGeneralResult result = GoogleAPIConnector.connect_UserAPI().updateMyProfile(l_address,l_aptno, l_city, l_lat, l_long, l_phone, socialID, socialType).execute();
                APIGeneralResult result = GoogleAPIConnector.connect_UserAPI().updateMyProfile(l_address, l_city, l_lat, l_long, l_phone, socialID, socialType).set("aptNo", l_aptno).execute();

                if(result.getStatusCode().equalsIgnoreCase("OK")){
                    MyServerSettings.initializeUserAddress(getContext(), l_address, l_aptno, l_city, l_lat, l_long);
                    /*
                     After I set the address, I start polling.
                     */
                    MyServerSettings.startPolling(getContext());
                }
            } catch (IOException e) {
                e.getMessage();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getActivity(), "Thanks! Your information is now saved.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
