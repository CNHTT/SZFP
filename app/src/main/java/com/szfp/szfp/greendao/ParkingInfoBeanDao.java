package com.szfp.szfp.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szfp.szfp.bean.ParkingInfoBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PARKING_INFO_BEAN".
*/
public class ParkingInfoBeanDao extends AbstractDao<ParkingInfoBean, Long> {

    public static final String TABLENAME = "PARKING_INFO_BEAN";

    /**
     * Properties of entity ParkingInfoBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property IdNumber = new Property(2, String.class, "idNumber", false, "ID_NUMBER");
        public final static Property FingerID = new Property(3, String.class, "fingerID", false, "FINGER_ID");
        public final static Property VehicleRegNumber = new Property(4, String.class, "vehicleRegNumber", false, "VEHICLE_REG_NUMBER");
        public final static Property Balance = new Property(5, float.class, "balance", false, "BALANCE");
        public final static Property Parameters_type = new Property(6, int.class, "parameters_type", false, "PARAMETERS_TYPE");
        public final static Property StartTime = new Property(7, long.class, "startTime", false, "START_TIME");
        public final static Property EndTime = new Property(8, long.class, "endTime", false, "END_TIME");
    }


    public ParkingInfoBeanDao(DaoConfig config) {
        super(config);
    }
    
    public ParkingInfoBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PARKING_INFO_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"ID_NUMBER\" TEXT," + // 2: idNumber
                "\"FINGER_ID\" TEXT," + // 3: fingerID
                "\"VEHICLE_REG_NUMBER\" TEXT," + // 4: vehicleRegNumber
                "\"BALANCE\" REAL NOT NULL ," + // 5: balance
                "\"PARAMETERS_TYPE\" INTEGER NOT NULL ," + // 6: parameters_type
                "\"START_TIME\" INTEGER NOT NULL ," + // 7: startTime
                "\"END_TIME\" INTEGER NOT NULL );"); // 8: endTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PARKING_INFO_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ParkingInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String idNumber = entity.getIdNumber();
        if (idNumber != null) {
            stmt.bindString(3, idNumber);
        }
 
        String fingerID = entity.getFingerID();
        if (fingerID != null) {
            stmt.bindString(4, fingerID);
        }
 
        String vehicleRegNumber = entity.getVehicleRegNumber();
        if (vehicleRegNumber != null) {
            stmt.bindString(5, vehicleRegNumber);
        }
        stmt.bindDouble(6, entity.getBalance());
        stmt.bindLong(7, entity.getParameters_type());
        stmt.bindLong(8, entity.getStartTime());
        stmt.bindLong(9, entity.getEndTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ParkingInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String idNumber = entity.getIdNumber();
        if (idNumber != null) {
            stmt.bindString(3, idNumber);
        }
 
        String fingerID = entity.getFingerID();
        if (fingerID != null) {
            stmt.bindString(4, fingerID);
        }
 
        String vehicleRegNumber = entity.getVehicleRegNumber();
        if (vehicleRegNumber != null) {
            stmt.bindString(5, vehicleRegNumber);
        }
        stmt.bindDouble(6, entity.getBalance());
        stmt.bindLong(7, entity.getParameters_type());
        stmt.bindLong(8, entity.getStartTime());
        stmt.bindLong(9, entity.getEndTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ParkingInfoBean readEntity(Cursor cursor, int offset) {
        ParkingInfoBean entity = new ParkingInfoBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // idNumber
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // fingerID
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // vehicleRegNumber
            cursor.getFloat(offset + 5), // balance
            cursor.getInt(offset + 6), // parameters_type
            cursor.getLong(offset + 7), // startTime
            cursor.getLong(offset + 8) // endTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ParkingInfoBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setIdNumber(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFingerID(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setVehicleRegNumber(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBalance(cursor.getFloat(offset + 5));
        entity.setParameters_type(cursor.getInt(offset + 6));
        entity.setStartTime(cursor.getLong(offset + 7));
        entity.setEndTime(cursor.getLong(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ParkingInfoBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ParkingInfoBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ParkingInfoBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
