package com.asus.zenboControl.Provider;

import com.asus.zenboControl.R;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

/**
 * Created by Ryan_Chou on 2017/12/7.
 */

public class StatusProvider extends AbstractProvider {

    @Override
    protected String getAuthority() {
        return getContext().getString(R.string.statusProviderURL);
    }

    @Table
    public class Status {
        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String ID = "_id";

        @Column(Column.FieldType.INTEGER)
        public static final String MOTION_AVOIDANCE_STATUS = "MotionAvoidanceStatus";
    }
}
