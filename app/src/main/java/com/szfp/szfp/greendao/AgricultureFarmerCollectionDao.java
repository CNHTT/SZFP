package com.szfp.szfp.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szfp.szfp.bean.AgricultureFarmerCollection;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AGRICULTURE_FARMER_COLLECTION".
*/
public class AgricultureFarmerCollectionDao extends AbstractDao<AgricultureFarmerCollection, Long> {

    public static final String TABLENAME = "AGRICULTURE_FARMER_COLLECTION";

    /**
     * Properties of entity AgricultureFarmerCollection.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property RegistrationNumber = new Property(1, String.class, "registrationNumber", false, "REGISTRATION_NUMBER");
        public final static Property IsPay = new Property(2, boolean.class, "isPay", false, "IS_PAY");
        public final static Property IdNumber = new Property(3, String.class, "idNumber", false, "ID_NUMBER");
        public final static Property Time = new Property(4, long.class, "time", false, "TIME");
        public final static Property AmountCollected = new Property(5, int.class, "amountCollected", false, "AMOUNT_COLLECTED");
        public final static Property Amount = new Property(6, float.class, "amount", false, "AMOUNT");
    }


    public AgricultureFarmerCollectionDao(DaoConfig config) {
        super(config);
    }
    
    public AgricultureFarmerCollectionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AGRICULTURE_FARMER_COLLECTION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"REGISTRATION_NUMBER\" TEXT," + // 1: registrationNumber
                "\"IS_PAY\" INTEGER NOT NULL ," + // 2: isPay
                "\"ID_NUMBER\" TEXT," + // 3: idNumber
                "\"TIME\" INTEGER NOT NULL ," + // 4: time
                "\"AMOUNT_COLLECTED\" INTEGER NOT NULL ," + // 5: amountCollected
                "\"AMOUNT\" REAL NOT NULL );"); // 6: amount
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AGRICULTURE_FARMER_COLLECTION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AgricultureFarmerCollection entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String registrationNumber = entity.getRegistrationNumber();
        if (registrationNumber != null) {
            stmt.bindString(2, registrationNumber);
        }
        stmt.bindLong(3, entity.getIsPay() ? 1L: 0L);
 
        String idNumber = entity.getIdNumber();
        if (idNumber != null) {
            stmt.bindString(4, idNumber);
        }
        stmt.bindLong(5, entity.getTime());
        stmt.bindLong(6, entity.getAmountCollected());
        stmt.bindDouble(7, entity.getAmount());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AgricultureFarmerCollection entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String registrationNumber = entity.getRegistrationNumber();
        if (registrationNumber != null) {
            stmt.bindString(2, registrationNumber);
        }
        stmt.bindLong(3, entity.getIsPay() ? 1L: 0L);
 
        String idNumber = entity.getIdNumber();
        if (idNumber != null) {
            stmt.bindString(4, idNumber);
        }
        stmt.bindLong(5, entity.getTime());
        stmt.bindLong(6, entity.getAmountCollected());
        stmt.bindDouble(7, entity.getAmount());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AgricultureFarmerCollection readEntity(Cursor cursor, int offset) {
        AgricultureFarmerCollection entity = new AgricultureFarmerCollection( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // registrationNumber
            cursor.getShort(offset + 2) != 0, // isPay
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // idNumber
            cursor.getLong(offset + 4), // time
            cursor.getInt(offset + 5), // amountCollected
            cursor.getFloat(offset + 6) // amount
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AgricultureFarmerCollection entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRegistrationNumber(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setIsPay(cursor.getShort(offset + 2) != 0);
        entity.setIdNumber(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTime(cursor.getLong(offset + 4));
        entity.setAmountCollected(cursor.getInt(offset + 5));
        entity.setAmount(cursor.getFloat(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AgricultureFarmerCollection entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AgricultureFarmerCollection entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AgricultureFarmerCollection entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
