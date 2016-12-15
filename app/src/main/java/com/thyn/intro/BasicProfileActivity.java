package com.thyn.intro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.thyn.R;
import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.collection.Task;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.field.AddressActivity;
import com.thyn.field.AddressFragment;
import com.thyn.navigate.NavigationActivity;
import com.thyn.tab.WelcomePageActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BasicProfileActivity extends AppCompatActivity {
    private static final String TAG = "BasicProfileActivity";
    private static final int ADDRESS_ACTIVITY_REQUEST_CODE = 1;
    private static double mLAT;
    private static double mLONG;
    private static String mCity;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE=1;
    private EditText addressField;
    private EditText aptNoField;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_profile);
        final EditText phoneField = (EditText) findViewById(R.id.phone);
        Log.d(TAG, "onCreate");
        phoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            //String lastChar = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               /* int digits = phoneField.getText().toString().length();
                if (digits > 1)
                    lastChar = phoneField.getText().toString().substring(digits - 1);
                    */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //int digits = phoneField.getText().toString().length();
                //Log.d("LENGTH", "" + digits);
                /*if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        phoneField.append("-");
                    }
                }*/
            }
        });
        addressField = (EditText) findViewById(R.id.street_address);
        addressField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "address was clicked");
                callPlaceAutocompleteActivityIntent();
            }
        });

        aptNoField = (EditText) findViewById(R.id.aptNo);
        aptNoField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "apt No. was clicked");

            }
        });

        button = (Button) findViewById(R.id.done);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place:" + place.toString());
                addressField.setText(place.getAddress());

                LatLng LatLong = place.getLatLng();
                mLAT = LatLong.latitude;
                mLONG = LatLong.longitude;
                Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
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
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (requestCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
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
                String socialID = MyServerSettings.getUserSocialId(getApplicationContext());
                int socialType = MyServerSettings.getUserSocialType(getApplicationContext());
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
                        MyServerSettings.initializeUserAddress(getApplicationContext(), l_address, l_aptno, l_city, l_lat, l_long);
                    /*
                     After I set the address, I start polling.
                     */
                        MyServerSettings.startPolling(getApplicationContext());
                }
            } catch (IOException e) {
                e.getMessage();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Intent intent = new Intent(getApplicationContext(), LoginSplash.class);
            startActivity(intent);
        }
    }
}
