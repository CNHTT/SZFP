package com.szfp.szfp.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szfp.szfp.bean.AgricultureFarmerBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AGRICULTURE_FARMER_BEAN".
*/
public class AgricultureFarmerBeanDao extends AbstractDao<AgricultureFarmerBean, Long> {

    public static final String TABLENAME = "AGRICULTURE_FARMER_BEAN";

    /**
     * Properties of entity AgricultureFarmerBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property FingerPrintId = new Property(2, String.class, "fingerPrintId", false, "FINGER_PRINT_ID");
        public final static Property IDNumber = new Property(3, String.class, "iDNumber", false, "I_DNUMBER");
        public final static Property Gender = new Property(4, boolean.class, "gender", false, "GENDER");
        public final static Property RegistrationNumber = new Property(5, String.class, "registrationNumber", false, "REGISTRATION_NUMBER");
        public final static Property Contact = new Property(6, String.class, "contact", false, "CONTACT");
        public final static Property NumberOfAnimals = new Property(7, int.class, "numberOfAnimals", false, "NUMBER_OF_ANIMALS");
        public final static Property DataOfBirth = new Property(8, String.class, "dataOfBirth", false, "DATA_OF_BIRTH");
        public final static Property HomeTown = new Property(9, String.class, "homeTown", false, "HOME_TOWN");
        public final static Property CollectionRoute = new Property(10, String.class, "collectionRoute", false, "COLLECTION_ROUTE");
        public final static Property Amount = new Property(11, float.class, "amount", false, "AMOUNT");
    }


    public AgricultureFarmerBeanDao(DaoConfig config) {
        super(config);
    }
    
    public AgricultureFarmerBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AGRICULTURE_FARMER_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"FINGER_PRINT_ID\" TEXT," + // 2: fingerPrintId
                "\"I_DNUMBER\" TEXT," + // 3: iDNumber
                "\"GENDER\" INTEGER NOT NULL ," + // 4: gender
                "\"REGISTRATION_NUMBER\" TEXT," + // 5: registrationNumber
                "\"CONTACT\" TEXT," + // 6: contact
                "\"NUMBER_OF_ANIMALS\" INTEGER NOT NULL ," + // 7: numberOfAnimals
                "\"DATA_OF_BIRTH\" TEXT," + // 8: dataOfBirth
                "\"HOME_TOWN\" TEXT," + // 9: homeTown
                "\"COLLECTION_ROUTE\" TEXT," + // 10: collectionRoute
                "\"AMOUNT\" REAL NOT NULL );"); // 11: amount
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AGRICULTURE_FARMER_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AgricultureFarmerBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String fingerPrintId = entity.getFingerPrintId();
        if (fingerPrintId != null) {
            stmt.bindString(3, fingerPrintId);
        }
 
        String iDNumber = entity.getIDNumber();
        if (iDNumber != null) {
            stmt.bindString(4, iDNumber);
        }
        stmt.bindLong(5, entity.getGender() ? 1L: 0L);
 
        String registrationNumber = entity.getRegistrationNumber();
        if (registrationNumber != null) {
            stmt.bindString(6, registrationNumber);
        }
 
        String contact = entity.getContact();
        if (contact != null) {
            stmt.bindString(7, contact);
        }
        stmt.bindLong(8, entity.getNumberOfAnimals());
 
        String dataOfBirth = entity.getDataOfBirth();
        if (dataOfBirth != null) {
            stmt.bindString(9, dataOfBirth);
        }
 
        String homeTown = entity.getHomeTown();
        if (homeTown != null) {
            stmt.bindString(10, homeTown);
        }
 
        String collectionRoute = entity.getCollectionRoute();
        if (collectionRoute != null) {
            stmt.bindString(11, collectionRoute);
        }
        stmt.bindDouble(12, entity.getAmount());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AgricultureFarmerBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String fingerPrintId = entity.getFingerPrintId();
        if (fingerPrintId != null) {
            stmt.bindString(3, fingerPrintId);
        }
 
        String iDNumber = entity.getIDNumber();
        if (iDNumber != null) {
            stmt.bindString(4, iDNumber);
        }
        stmt.bindLong(5, entity.getGender() ? 1L: 0L);
 
        String registrationNumber = entity.getRegistrationNumber();
        if (registrationNumber != null) {
            stmt.bindString(6, registrationNumber);
        }
 
        String contact = entity.getContact();
        if (contact != null) {
            stmt.bindString(7, contact);
        }
        stmt.bindLong(8, entity.getNumberOfAnimals());
 
        String dataOfBirth = entity.getDataOfBirth();
        if (dataOfBirth != null) {
            stmt.bindString(9, dataOfBirth);
        }
 
        String homeTown = entity.getHomeTown();
        if (homeTown != null) {
            stmt.bindString(10, homeTown);
        }
 
        String collectionRoute = entity.getCollectionRoute();
        if (collectionRoute != null) {
            stmt.bindString(11, collectionRoute);
        }
        stmt.bindDouble(12, entity.getAmount());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AgricultureFarmerBean readEntity(Cursor cursor, int offset) {
        AgricultureFarmerBean entity = new AgricultureFarmerBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // fingerPrintId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // iDNumber
            cursor.getShort(offset + 4) != 0, // gender
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // registrationNumber
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // contact
            cursor.getInt(offset + 7), // numberOfAnimals
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // dataOfBirth
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // homeTown
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // collectionRoute
            cursor.getFloat(offset + 11) // amount
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AgricultureFarmerBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFingerPrintId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIDNumber(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setGender(cursor.getShort(offset + 4) != 0);
        entity.setRegistrationNumber(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setContact(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setNumberOfAnimals(cursor.getInt(offset + 7));
        entity.setDataOfBirth(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setHomeTown(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCollectionRoute(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setAmount(cursor.getFloat(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AgricultureFarmerBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AgricultureFarmerBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AgricultureFarmerBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
