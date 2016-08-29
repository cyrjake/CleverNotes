package com.ser210.cyr.clevernotes2.CursorAdapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class AlternatingColorCursorAdapter extends SimpleCursorAdapter {

    public AlternatingColorCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    private int[] colors = new int[] { Color.parseColor("#50737373"), Color.parseColor("#50000000") };


   //Alternate the color of each item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        return view;
    }
}
