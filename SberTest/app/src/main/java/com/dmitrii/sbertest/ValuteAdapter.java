package com.dmitrii.sbertest;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitrii on 13.04.2017.
 */

public class ValuteAdapter extends ArrayAdapter<String> {

//    public ValuteAdapter(@NonNull Context context, @LayoutRes int resource/*, @NonNull List<CbrXmlParser.Valute> valutes*/) {
//        super(context, resource, objects);
//    }


    public ValuteAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    private CbrXmlParser.ValuteList mList;
    public ValuteAdapter setValuteList(@NonNull CbrXmlParser.ValuteList list) {
        mList = list;
        ArrayList<String> stringList = new ArrayList<>();
        for (CbrXmlParser.Valute item : list.valuteList) {
            stringList.add(item.name);
        }
        addAll(stringList);
        notifyDataSetChanged();
        return this;
    }

    public float getValueByPosition(int position) {
        return mList.valuteList.get(position).getValue();
    }

    //    private final List<CbrXmlParser.Valute> mList;
//    private LayoutInflater mInflater;
//    public ValuteAdapter(@NonNull Context context, @NonNull CbrXmlParser.ValuteList list) {
//        mInflater = LayoutInflater.from(context);
//        mList = list.valuteList;
//    }
//
//    @Override
//    public int getCount() {
//        return mList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        if (position <= 0 || position >= mList.size()) {
//            return null;
//        }
//        return mList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return mList.get(position).hashCode();
//    }
//
//    @Override
//    public View getView(int position, View view, ViewGroup parent) {
//        if (view == null) {
//            view = mInflater.inflate(android.R.layout.simple_spinner_item, parent, false);
//        }
//        ((TextView)view).setText(((CbrXmlParser.Valute)getItem(position)).name);
//        return view;
//    }
//
//
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        return null;
//    }


}
