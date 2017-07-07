package com.szfp.szfp.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szfp.szfp.bean.VehicleParkingBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VEHICLE_PARKING_BEAN".
*/
public class VehicleParkingBeanDao extends AbstractDao<VehicleParkingBean, Long> {

    public static final String TABLENAME = "VEHICLE_PARKING_BEAN";

    /**
     * Properties of entity VehicleParkingBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NumberID = new Property(1, String.class, "numberID", false, "NUMBER_ID");
        public final static Property VehicleNumber = new Property(2, String.class, "vehicleNumber", false, "VEHICLE_NUMBER");
        public final static Property FingerPrintID = new Property(3, String.class, "fingerPrintID", false, "FINGER_PRINT_ID");
        public final static Property StartTime = new Property(4, long.class, "startTime", false, "START_TIME");
        public final static Property EndTime = new Property(5, long.class, "endTime", false, "END_TIME");
        public final static Property IsPay = new Property(6, boolean.class, "isPay", false, "IS_PAY");
        public final static Property Time = new Property(7, int.class, "time", false, "TIME");
        public final static Property PayNum = new Property(8, String.class, "payNum", false, "PAY_NUM");
    }


    public VehicleParkingBeanDao(DaoConfig config) {
        super(config);
    }
    
    public VehicleParkingBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VEHICLE_PARKING_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NUMBER_ID\" TEXT," + // 1: numberID
                "\"VEHICLE_NUMBER\" TEXT," + // 2: vehicleNumber
                "\"FINGER_PRINT_ID\" TEXT," + // 3: fingerPrintID
                "\"START_TIME\" INTEGER NOT NULL ," + // 4: startTime
                "\"END_TIME\" INTEGER NOT NULL ," + // 5: endTime
                "\"IS_PAY\" INTEGER NOT NULL ," + // 6: isPay
                "\"TIME\" INTEGER NOT NULL ," + // 7: time
                "\"PAY_NUM\" TEXT);"); // 8: payNum
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VEHICLE_PARKING_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, VehicleParkingBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String numberID = entity.getNumberID();
        if (numberID != null) {
            stmt.bindString(2, numberID);
        }
 
        String vehicleNumber = entity.getVehicleNumber();
        if (vehicleNumber != null) {
            stmt.bindString(3, vehicleNumber);
        }
 
        String fingerPrintID = entity.getFingerPrintID();
        if (fingerPrintID != null) {
            stmt.bindString(4, fingerPrintID);
        }
        stmt.bindLong(5, entity.getStartTime());
        stmt.bindLong(6, entity.getEndTime());
        stmt.bindLong(7, entity.getIsPay() ? 1L: 0L);
        stmt.bindLong(8, entity.getTime());
 
        String payNum = entity.getPayNum();
        if (payNum != null) {
            stmt.bindString(9, payNum);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, VehicleParkingBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String numberID = entity.getNumberID();
        if (numberID != null) {
            stmt.bindString(2, numberID);
        }
 
        String vehicleNumber = entity.getVehicleNumber();
        if (vehicleNumber != null) {
            stmt.bindString(3, vehicleNumber);
        }
 
        String fingerPrintID = entity.getFingerPrintID();
        if (fingerPrintID != null) {
            stmt.bindString(4, fingerPrintID);
        }
        stmt.bindLong(5, entity.getStartTime());
        stmt.bindLong(6, entity.getEndTime());
        stmt.bindLong(7, entity.getIsPay() ? 1L: 0L);
        stmt.bindLong(8, entity.getTime());
 
        String payNum = entity.getPayNum();
        if (payNum != null) {
            stmt.bindString(9, payNum);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public VehicleParkingBean readEntity(Cursor cursor, int offset) {
        VehicleParkingBean entity = new VehicleParkingBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // numberID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // vehicleNumber
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // fingerPrintID
            cursor.getLong(offset + 4), // startTime
            cursor.getLong(offset + 5), // endTime
            cursor.getShort(offset + 6) != 0, // isPay
            cursor.getInt(offset + 7), // time
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // payNum
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, VehicleParkingBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNumberID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setVehicleNumber(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFingerPrintID(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStartTime(cursor.getLong(offset + 4));
        entity.setEndTime(cursor.getLong(offset + 5));
        entity.setIsPay(cursor.getShort(offset + 6) != 0);
        entity.setTime(cursor.getInt(offset + 7));
        entity.setPayNum(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(VehicleParkingBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(VehicleParkingBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(VehicleParkingBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}