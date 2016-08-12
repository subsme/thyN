package com.thyn.graphics;

import android.widget.BaseAdapter;
import java.util.List;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.content.Context;
import com.thyn.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shalu on 7/15/16.
 */
public class SquareImageGridAdapter extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;

    public SquareImageGridAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        //mItems.add(new Item("Yellow",    R.drawable.yellow));
        //mItems.add(new Item("Peach",   R.drawable.peach));
        //mItems.add(new Item("Light Blue", R.drawable.lightblue));
        //mItems.add(new Item("Pink",      R.drawable.pink));
    }
    public void setGrid1(String text, String description){
        mItems.add(new Item(text, description, R.drawable.yellow));
    }
    public void setGrid2(String text, String description){
        mItems.add(new Item(text, description, R.drawable.peach));
    }
    public void setGrid3(String text, String description){
        mItems.add(new Item(text, description, R.drawable.lightblue));
    }
    public void setGrid4(String text, String description){
        mItems.add(new Item(text, description, R.drawable.pink));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).drawableId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;
        TextView desc;

        if (v == null) {
            v = mInflater.inflate(R.layout.mytask_slidingtab_griditem, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
            v.setTag(R.id.desc, v.findViewById(R.id.desc));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);
        desc = (TextView) v.getTag(R.id.desc);

        Item item = getItem(i);

        picture.setImageResource(item.drawableId);
        name.setText(item.name);
        desc.setText(item.desc);

        return v;
    }

    private static class Item {
        public final String name;
        public final String desc;
        public final int drawableId;

        Item(String name, String desc, int drawableId) {
            this.name = name;
            this.desc = desc;
            this.drawableId = drawableId;
        }
    }
}