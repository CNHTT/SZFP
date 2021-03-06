package com.szfp.szfp.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szfp.szfp.bean.AgricultureEmployeeBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AGRICULTURE_EMPLOYEE_BEAN".
*/
public class AgricultureEmployeeBeanDao extends AbstractDao<AgricultureEmployeeBean, Long> {

    public static final String TABLENAME = "AGRICULTURE_EMPLOYEE_BEAN";

    /**
     * Properties of entity AgricultureEmployeeBean.<br/>
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
        public final static Property JobTitle = new Property(7, String.class, "jobTitle", false, "JOB_TITLE");
        public final static Property Salary = new Property(8, String.class, "salary", false, "SALARY");
        public final static Property DataOfBirth = new Property(9, String.class, "dataOfBirth", false, "DATA_OF_BIRTH");
        public final static Property EmployedDate = new Property(10, String.class, "employedDate", false, "EMPLOYED_DATE");
        public final static Property HomeTown = new Property(11, String.class, "homeTown", false, "HOME_TOWN");
        public final static Property CollectionRoute = new Property(12, String.class, "collectionRoute", false, "COLLECTION_ROUTE");
        public final static Property NatureOfEmployment = new Property(13, String.class, "natureOfEmployment", false, "NATURE_OF_EMPLOYMENT");
    }


    public AgricultureEmployeeBeanDao(DaoConfig config) {
        super(config);
    }
    
    public AgricultureEmployeeBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AGRICULTURE_EMPLOYEE_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"FINGER_PRINT_ID\" TEXT," + // 2: fingerPrintId
                "\"I_DNUMBER\" TEXT," + // 3: iDNumber
                "\"GENDER\" INTEGER NOT NULL ," + // 4: gender
                "\"REGISTRATION_NUMBER\" TEXT," + // 5: registrationNumber
                "\"CONTACT\" TEXT," + // 6: contact
                "\"JOB_TITLE\" TEXT," + // 7: jobTitle
                "\"SALARY\" TEXT," + // 8: salary
                "\"DATA_OF_BIRTH\" TEXT," + // 9: dataOfBirth
                "\"EMPLOYED_DATE\" TEXT," + // 10: employedDate
                "\"HOME_TOWN\" TEXT," + // 11: homeTown
                "\"COLLECTION_ROUTE\" TEXT," + // 12: collectionRoute
                "\"NATURE_OF_EMPLOYMENT\" TEXT);"); // 13: natureOfEmployment
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AGRICULTURE_EMPLOYEE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AgricultureEmployeeBean entity) {
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
 
        String jobTitle = entity.getJobTitle();
        if (jobTitle != null) {
            stmt.bindString(8, jobTitle);
        }
 
        String salary = entity.getSalary();
        if (salary != null) {
            stmt.bindString(9, salary);
        }
 
        String dataOfBirth = entity.getDataOfBirth();
        if (dataOfBirth != null) {
            stmt.bindString(10, dataOfBirth);
        }
 
        String employedDate = entity.getEmployedDate();
        if (employedDate != null) {
            stmt.bindString(11, employedDate);
        }
 
        String homeTown = entity.getHomeTown();
        if (homeTown != null) {
            stmt.bindString(12, homeTown);
        }
 
        String collectionRoute = entity.getCollectionRoute();
        if (collectionRoute != null) {
            stmt.bindString(13, collectionRoute);
        }
 
        String natureOfEmployment = entity.getNatureOfEmployment();
        if (natureOfEmployment != null) {
            stmt.bindString(14, natureOfEmployment);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AgricultureEmployeeBean entity) {
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
 
        String jobTitle = entity.getJobTitle();
        if (jobTitle != null) {
            stmt.bindString(8, jobTitle);
        }
 
        String salary = entity.getSalary();
        if (salary != null) {
            stmt.bindString(9, salary);
        }
 
        String dataOfBirth = entity.getDataOfBirth();
        if (dataOfBirth != null) {
            stmt.bindString(10, dataOfBirth);
        }
 
        String employedDate = entity.getEmployedDate();
        if (employedDate != null) {
            stmt.bindString(11, employedDate);
        }
 
        String homeTown = entity.getHomeTown();
        if (homeTown != null) {
            stmt.bindString(12, homeTown);
        }
 
        String collectionRoute = entity.getCollectionRoute();
        if (collectionRoute != null) {
            stmt.bindString(13, collectionRoute);
        }
 
        String natureOfEmployment = entity.getNatureOfEmployment();
        if (natureOfEmployment != null) {
            stmt.bindString(14, natureOfEmployment);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AgricultureEmployeeBean readEntity(Cursor cursor, int offset) {
        AgricultureEmployeeBean entity = new AgricultureEmployeeBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // fingerPrintId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // iDNumber
            cursor.getShort(offset + 4) != 0, // gender
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // registrationNumber
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // contact
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // jobTitle
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // salary
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // dataOfBirth
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // employedDate
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // homeTown
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // collectionRoute
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // natureOfEmployment
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AgricultureEmployeeBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFingerPrintId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIDNumber(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setGender(cursor.getShort(offset + 4) != 0);
        entity.setRegistrationNumber(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setContact(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setJobTitle(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setSalary(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDataOfBirth(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setEmployedDate(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setHomeTown(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setCollectionRoute(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setNatureOfEmployment(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AgricultureEmployeeBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AgricultureEmployeeBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AgricultureEmployeeBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
