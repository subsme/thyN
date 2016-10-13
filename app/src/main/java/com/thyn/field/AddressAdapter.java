package com.thyn.field;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * Created by shalu on 9/30/16.
 */
public class AddressAdapter extends ArrayAdapter<String> implements Filterable {
    private static final int MAX_RESULTS = 10;
    private Context mContext;
    ArrayList<String> addresses;


    public AddressAdapter(Context context, int resource) {
        super(context, resource);
        addresses = new ArrayList<String>();
    }

}
