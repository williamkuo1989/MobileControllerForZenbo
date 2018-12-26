package com.asus.zenboControl.Server;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asus.zenboControl.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Samanna_Huang on 2016/7/18.
 */

public class BroadCaseEventAdapter extends BaseAdapter {
    private LayoutInflater myInflater;

    private ServerService mService;
    private List<String> mInfo;
    private List<String> mShowString;
    private Context mCtx;
    private int currentSelection = -1;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    public BroadCaseEventAdapter(Context context, List<String> infos, List<String> showString, ServerService s) {
        mCtx = context;
        myInflater = LayoutInflater.from(context);
        mInfo = infos;
        mShowString = showString;
        mService = s;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.simple_list_item, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.item),
                    (ImageView) convertView.findViewById(R.id.play_icon)
            );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(mShowString.size() > position){
            holder.txtInfo.setText(mShowString.get(position));
        } else {
            holder.txtInfo.setText(mInfo.get(position));
        }

        holder.play.setVisibility(isPositionSelection(position) ? View.VISIBLE
                : View.INVISIBLE);

        holder.play.setActivated(false);
        final View finalConvertView = convertView;
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setActivated(true);
                itemRunEvent();
                finalConvertView.setBackgroundColor(Color.parseColor("#9999FF"));
                mService.broadcastZbaEvent(mInfo.get(position));
                view.setVisibility(View.INVISIBLE);
            }
        });

        if (isPositionChecked(position)) {

            if (isPositionSelection(position)) {
                convertView.setBackgroundColor(Color.parseColor("#96CDCD"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#96CDCD"));
            }

        } else if (isPositionSelection(position)) {
            convertView.setBackgroundColor(Color.parseColor("#FF4D9DFF"));
        } else {
            if( (position % 2) > 0){
                convertView.setBackgroundColor(Color.parseColor("#FFDD55"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtInfo;
        ImageView play;

        public ViewHolder(TextView txtInfo, ImageView iv) {
            this.txtInfo = txtInfo;
            play = iv;
        }
    }

    public void itemRunEvent(){

    }
}
