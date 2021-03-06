package com.szfp.szfp.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szfp.szfp.bean.VehicleInfoBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VEHICLE_INFO_BEAN".
*/
public class VehicleInfoBeanDao extends AbstractDao<VehicleInfoBean, Long> {

    public static final String TABLENAME = "VEHICLE_INFO_BEAN";

    /**
     * Properties of entity VehicleInfoBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CommuterAccount = new Property(1, String.class, "commuterAccount", false, "COMMUTER_ACCOUNT");
        public final static Property NameOfOwner = new Property(2, String.class, "nameOfOwner", false, "NAME_OF_OWNER");
        public final static Property OwnerIDNo = new Property(3, String.class, "ownerIDNo", false, "OWNER_IDNO");
        public final static Property OwnerContacts = new Property(4, String.class, "ownerContacts", false, "OWNER_CONTACTS");
        public final static Property OwnerAddress = new Property(5, String.class, "ownerAddress", false, "OWNER_ADDRESS");
        public final static Property OwnerFingerPrintFileUrl = new Property(6, String.class, "ownerFingerPrintFileUrl", false, "OWNER_FINGER_PRINT_FILE_URL");
        public final static Property OwnerPhotoFileUrl = new Property(7, String.class, "ownerPhotoFileUrl", false, "OWNER_PHOTO_FILE_URL");
        public final static Property DriverName = new Property(8, String.class, "driverName", false, "DRIVER_NAME");
        public final static Property DriverNo = new Property(9, String.class, "driverNo", false, "DRIVER_NO");
        public final static Property DriverAddress = new Property(10, String.class, "driverAddress", false, "DRIVER_ADDRESS");
        public final static Property DriverContacts = new Property(11, String.class, "driverContacts", false, "DRIVER_CONTACTS");
        public final static Property DriverFingerPrintFileUrl = new Property(12, String.class, "driverFingerPrintFileUrl", false, "DRIVER_FINGER_PRINT_FILE_URL");
        public final static Property DriverPhotoFileUrl = new Property(13, String.class, "driverPhotoFileUrl", false, "DRIVER_PHOTO_FILE_URL");
        public final static Property MakeOfVehicle = new Property(14, String.class, "makeOfVehicle", false, "MAKE_OF_VEHICLE");
        public final static Property RegNo = new Property(15, String.class, "regNo", false, "REG_NO");
        public final static Property Model = new Property(16, String.class, "model", false, "MODEL");
        public final static Property YearOfVehicle = new Property(17, String.class, "yearOfVehicle", false, "YEAR_OF_VEHICLE");
        public final static Property Color = new Property(18, String.class, "color", false, "COLOR");
        public final static Property PsvNo = new Property(19, String.class, "psvNo", false, "PSV_NO");
        public final static Property DesiginnatedRoute = new Property(20, String.class, "desiginnatedRoute", false, "DESIGINNATED_ROUTE");
        public final static Property PassengerNo = new Property(21, String.class, "passengerNo", false, "PASSENGER_NO");
        public final static Property Conductor = new Property(22, String.class, "conductor", false, "CONDUCTOR");
        public final static Property TimeStr = new Property(23, String.class, "timeStr", false, "TIME_STR");
        public final static Property TimeMills = new Property(24, Long.class, "timeMills", false, "TIME_MILLS");
    }


    public VehicleInfoBeanDao(DaoConfig config) {
        super(config);
    }
    
    public VehicleInfoBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VEHICLE_INFO_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"COMMUTER_ACCOUNT\" TEXT," + // 1: commuterAccount
                "\"NAME_OF_OWNER\" TEXT," + // 2: nameOfOwner
                "\"OWNER_IDNO\" TEXT," + // 3: ownerIDNo
                "\"OWNER_CONTACTS\" TEXT," + // 4: ownerContacts
                "\"OWNER_ADDRESS\" TEXT," + // 5: ownerAddress
                "\"OWNER_FINGER_PRINT_FILE_URL\" TEXT," + // 6: ownerFingerPrintFileUrl
                "\"OWNER_PHOTO_FILE_URL\" TEXT," + // 7: ownerPhotoFileUrl
                "\"DRIVER_NAME\" TEXT," + // 8: driverName
                "\"DRIVER_NO\" TEXT," + // 9: driverNo
                "\"DRIVER_ADDRESS\" TEXT," + // 10: driverAddress
                "\"DRIVER_CONTACTS\" TEXT," + // 11: driverContacts
                "\"DRIVER_FINGER_PRINT_FILE_URL\" TEXT," + // 12: driverFingerPrintFileUrl
                "\"DRIVER_PHOTO_FILE_URL\" TEXT," + // 13: driverPhotoFileUrl
                "\"MAKE_OF_VEHICLE\" TEXT," + // 14: makeOfVehicle
                "\"REG_NO\" TEXT," + // 15: regNo
                "\"MODEL\" TEXT," + // 16: model
                "\"YEAR_OF_VEHICLE\" TEXT," + // 17: yearOfVehicle
                "\"COLOR\" TEXT," + // 18: color
                "\"PSV_NO\" TEXT," + // 19: psvNo
                "\"DESIGINNATED_ROUTE\" TEXT," + // 20: desiginnatedRoute
                "\"PASSENGER_NO\" TEXT," + // 21: passengerNo
                "\"CONDUCTOR\" TEXT," + // 22: conductor
                "\"TIME_STR\" TEXT," + // 23: timeStr
                "\"TIME_MILLS\" INTEGER);"); // 24: timeMills
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VEHICLE_INFO_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, VehicleInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String commuterAccount = entity.getCommuterAccount();
        if (commuterAccount != null) {
            stmt.bindString(2, commuterAccount);
        }
 
        String nameOfOwner = entity.getNameOfOwner();
        if (nameOfOwner != null) {
            stmt.bindString(3, nameOfOwner);
        }
 
        String ownerIDNo = entity.getOwnerIDNo();
        if (ownerIDNo != null) {
            stmt.bindString(4, ownerIDNo);
        }
 
        String ownerContacts = entity.getOwnerContacts();
        if (ownerContacts != null) {
            stmt.bindString(5, ownerContacts);
        }
 
        String ownerAddress = entity.getOwnerAddress();
        if (ownerAddress != null) {
            stmt.bindString(6, ownerAddress);
        }
 
        String ownerFingerPrintFileUrl = entity.getOwnerFingerPrintFileUrl();
        if (ownerFingerPrintFileUrl != null) {
            stmt.bindString(7, ownerFingerPrintFileUrl);
        }
 
        String ownerPhotoFileUrl = entity.getOwnerPhotoFileUrl();
        if (ownerPhotoFileUrl != null) {
            stmt.bindString(8, ownerPhotoFileUrl);
        }
 
        String driverName = entity.getDriverName();
        if (driverName != null) {
            stmt.bindString(9, driverName);
        }
 
        String driverNo = entity.getDriverNo();
        if (driverNo != null) {
            stmt.bindString(10, driverNo);
        }
 
        String driverAddress = entity.getDriverAddress();
        if (driverAddress != null) {
            stmt.bindString(11, driverAddress);
        }
 
        String driverContacts = entity.getDriverContacts();
        if (driverContacts != null) {
            stmt.bindString(12, driverContacts);
        }
 
        String driverFingerPrintFileUrl = entity.getDriverFingerPrintFileUrl();
        if (driverFingerPrintFileUrl != null) {
            stmt.bindString(13, driverFingerPrintFileUrl);
        }
 
        String driverPhotoFileUrl = entity.getDriverPhotoFileUrl();
        if (driverPhotoFileUrl != null) {
            stmt.bindString(14, driverPhotoFileUrl);
        }
 
        String makeOfVehicle = entity.getMakeOfVehicle();
        if (makeOfVehicle != null) {
            stmt.bindString(15, makeOfVehicle);
        }
 
        String regNo = entity.getRegNo();
        if (regNo != null) {
            stmt.bindString(16, regNo);
        }
 
        String model = entity.getModel();
        if (model != null) {
            stmt.bindString(17, model);
        }
 
        String yearOfVehicle = entity.getYearOfVehicle();
        if (yearOfVehicle != null) {
            stmt.bindString(18, yearOfVehicle);
        }
 
        String color = entity.getColor();
        if (color != null) {
            stmt.bindString(19, color);
        }
 
        String psvNo = entity.getPsvNo();
        if (psvNo != null) {
            stmt.bindString(20, psvNo);
        }
 
        String desiginnatedRoute = entity.getDesiginnatedRoute();
        if (desiginnatedRoute != null) {
            stmt.bindString(21, desiginnatedRoute);
        }
 
        String passengerNo = entity.getPassengerNo();
        if (passengerNo != null) {
            stmt.bindString(22, passengerNo);
        }
 
        String conductor = entity.getConductor();
        if (conductor != null) {
            stmt.bindString(23, conductor);
        }
 
        String timeStr = entity.getTimeStr();
        if (timeStr != null) {
            stmt.bindString(24, timeStr);
        }
 
        Long timeMills = entity.getTimeMills();
        if (timeMills != null) {
            stmt.bindLong(25, timeMills);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, VehicleInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String commuterAccount = entity.getCommuterAccount();
        if (commuterAccount != null) {
            stmt.bindString(2, commuterAccount);
        }
 
        String nameOfOwner = entity.getNameOfOwner();
        if (nameOfOwner != null) {
            stmt.bindString(3, nameOfOwner);
        }
 
        String ownerIDNo = entity.getOwnerIDNo();
        if (ownerIDNo != null) {
            stmt.bindString(4, ownerIDNo);
        }
 
        String ownerContacts = entity.getOwnerContacts();
        if (ownerContacts != null) {
            stmt.bindString(5, ownerContacts);
        }
 
        String ownerAddress = entity.getOwnerAddress();
        if (ownerAddress != null) {
            stmt.bindString(6, ownerAddress);
        }
 
        String ownerFingerPrintFileUrl = entity.getOwnerFingerPrintFileUrl();
        if (ownerFingerPrintFileUrl != null) {
            stmt.bindString(7, ownerFingerPrintFileUrl);
        }
 
        String ownerPhotoFileUrl = entity.getOwnerPhotoFileUrl();
        if (ownerPhotoFileUrl != null) {
            stmt.bindString(8, ownerPhotoFileUrl);
        }
 
        String driverName = entity.getDriverName();
        if (driverName != null) {
            stmt.bindString(9, driverName);
        }
 
        String driverNo = entity.getDriverNo();
        if (driverNo != null) {
            stmt.bindString(10, driverNo);
        }
 
        String driverAddress = entity.getDriverAddress();
        if (driverAddress != null) {
            stmt.bindString(11, driverAddress);
        }
 
        String driverContacts = entity.getDriverContacts();
        if (driverContacts != null) {
            stmt.bindString(12, driverContacts);
        }
 
        String driverFingerPrintFileUrl = entity.getDriverFingerPrintFileUrl();
        if (driverFingerPrintFileUrl != null) {
            stmt.bindString(13, driverFingerPrintFileUrl);
        }
 
        String driverPhotoFileUrl = entity.getDriverPhotoFileUrl();
        if (driverPhotoFileUrl != null) {
            stmt.bindString(14, driverPhotoFileUrl);
        }
 
        String makeOfVehicle = entity.getMakeOfVehicle();
        if (makeOfVehicle != null) {
            stmt.bindString(15, makeOfVehicle);
        }
 
        String regNo = entity.getRegNo();
        if (regNo != null) {
            stmt.bindString(16, regNo);
        }
 
        String model = entity.getModel();
        if (model != null) {
            stmt.bindString(17, model);
        }
 
        String yearOfVehicle = entity.getYearOfVehicle();
        if (yearOfVehicle != null) {
            stmt.bindString(18, yearOfVehicle);
        }
 
        String color = entity.getColor();
        if (color != null) {
            stmt.bindString(19, color);
        }
 
        String psvNo = entity.getPsvNo();
        if (psvNo != null) {
            stmt.bindString(20, psvNo);
        }
 
        String desiginnatedRoute = entity.getDesiginnatedRoute();
        if (desiginnatedRoute != null) {
            stmt.bindString(21, desiginnatedRoute);
        }
 
        String passengerNo = entity.getPassengerNo();
        if (passengerNo != null) {
            stmt.bindString(22, passengerNo);
        }
 
        String conductor = entity.getConductor();
        if (conductor != null) {
            stmt.bindString(23, conductor);
        }
 
        String timeStr = entity.getTimeStr();
        if (timeStr != null) {
            stmt.bindString(24, timeStr);
        }
 
        Long timeMills = entity.getTimeMills();
        if (timeMills != null) {
            stmt.bindLong(25, timeMills);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public VehicleInfoBean readEntity(Cursor cursor, int offset) {
        VehicleInfoBean entity = new VehicleInfoBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // commuterAccount
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // nameOfOwner
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ownerIDNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ownerContacts
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // ownerAddress
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ownerFingerPrintFileUrl
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // ownerPhotoFileUrl
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // driverName
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // driverNo
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // driverAddress
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // driverContacts
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // driverFingerPrintFileUrl
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // driverPhotoFileUrl
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // makeOfVehicle
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // regNo
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // model
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // yearOfVehicle
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // color
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // psvNo
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // desiginnatedRoute
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // passengerNo
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // conductor
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // timeStr
            cursor.isNull(offset + 24) ? null : cursor.getLong(offset + 24) // timeMills
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, VehicleInfoBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCommuterAccount(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNameOfOwner(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setOwnerIDNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setOwnerContacts(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setOwnerAddress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOwnerFingerPrintFileUrl(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOwnerPhotoFileUrl(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDriverName(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDriverNo(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setDriverAddress(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDriverContacts(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setDriverFingerPrintFileUrl(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setDriverPhotoFileUrl(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setMakeOfVehicle(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setRegNo(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setModel(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setYearOfVehicle(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setColor(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setPsvNo(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setDesiginnatedRoute(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setPassengerNo(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setConductor(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setTimeStr(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setTimeMills(cursor.isNull(offset + 24) ? null : cursor.getLong(offset + 24));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(VehicleInfoBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(VehicleInfoBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(VehicleInfoBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
