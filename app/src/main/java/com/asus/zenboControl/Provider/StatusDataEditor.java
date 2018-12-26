package com.asus.zenboControl.Provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.asus.zenboControl.R;

import static com.asus.zenboControl.Provider.StatusProvider.Status.MOTION_AVOIDANCE_STATUS;

/**
 * Created by Ryan_Chou on 2017/12/7.
 */

public class StatusDataEditor {

    public static final int MOTION_AVOIDANCE_STATUS_OPEN = 1;
    public static final int MOTION_AVOIDANCE_STATUS_CLOSE = 0;

    public static boolean getMotionAvoidanceStatus(Context context){
        Cursor statusCursor = getMotionAvoidanceStatusCursor(context);
        int statusNum = MOTION_AVOIDANCE_STATUS_OPEN;

        if(statusCursor.getCount() > 0){
            statusCursor.moveToFirst();
            statusNum = statusCursor.getInt(statusCursor.getColumnIndex(MOTION_AVOIDANCE_STATUS));
        } else {
            initMotionAvoidanceStatus(context);
        }

        if(statusNum == MOTION_AVOIDANCE_STATUS_CLOSE)
            return false;
        else
            return true;
    }

    private static Cursor getMotionAvoidanceStatusCursor(Context context){
        Uri uri = Uri.parse("content://" + context.getString(R.string.statusProviderURL) + "/Status");
        return context.getContentResolver().query(uri,null,null,null,null);
    }

    private static void initMotionAvoidanceStatus(Context context){
        Uri uri = Uri.parse("content://" + context.getString(R.string.statusProviderURL) + "/Status");
        ContentValues mNewValues = new ContentValues();
        mNewValues.put(MOTION_AVOIDANCE_STATUS, MOTION_AVOIDANCE_STATUS_OPEN);
        context.getContentResolver().insert(uri,mNewValues);
    }

    public static int UpdateMotionAvoidanceStatus(Context context, boolean setOpen){
        Uri uri = Uri.parse("content://" + context.getString(R.string.statusProviderURL) + "/Status");
        Uri selecturi = ContentUris.withAppendedId(uri,1);

        ContentValues values = new ContentValues();

        if(setOpen) {
            values.put(MOTION_AVOIDANCE_STATUS, MOTION_AVOIDANCE_STATUS_OPEN);
        } else {
            values.put(MOTION_AVOIDANCE_STATUS, MOTION_AVOIDANCE_STATUS_CLOSE);
        }
        return context.getContentResolver().update(selecturi, values, null, null);
    }
}
