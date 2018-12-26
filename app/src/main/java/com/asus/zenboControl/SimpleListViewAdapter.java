package com.asus.zenboControl;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Samanna_Huang on 2016/7/18.
 */

public class SimpleListViewAdapter extends BaseAdapter {
    private LayoutInflater myInflater;

    private List<String> mEventIndex;
    private List<String> mInfo;
    private Context mCtx;
    private int currentSelection = -1;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    public SimpleListViewAdapter(Context context, List<String> eventIndex, List<String> infos) {
        mCtx = context;
        myInflater = LayoutInflater.from(context);
        mEventIndex = eventIndex;
        mInfo = infos;
    }

    public void setNewChecked(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public void setSelection(int position, boolean value) {

        if (value) {
            currentSelection = position;
        } else {
            if (currentSelection == position) {
                currentSelection = -1;
            }
        }

        notifyDataSetChanged();
    }

    public boolean isPositionSelection(int position) {
        return currentSelection == position;
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    @Override
    public int getCount() {
        return mInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return mInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mInfo.indexOf(getItem(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.listview_item, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.tv_num),
                    (TextView) convertView.findViewById(R.id.tv_info)
            );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNumber.setText(mEventIndex.get(position));
        holder.txtInfo.setText(mInfo.get(position));

        if (isPositionChecked(position)) {

            if (isPositionSelection(position)) {
                convertView.setBackgroundColor(Color.parseColor("#FF68db"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#FF6868"));
            }

        } else if (isPositionSelection(position)) {
            convertView.setBackgroundColor(Color.parseColor("#FF4D9DFF"));
        } else {
            convertView.setBackgroundColor(Color.argb(0,0,0,0));
        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtNumber;
        TextView txtInfo;

        public ViewHolder(TextView txtNum, TextView txtInfo) {
            this.txtNumber = txtNum;
            this.txtInfo = txtInfo;
        }
    }
}
