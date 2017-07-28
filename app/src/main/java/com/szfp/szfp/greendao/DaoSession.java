package com.szfp.szfp.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.szfp.szfp.bean.AccountReportBean;
import com.szfp.szfp.bean.AgricultureEmployeeBean;
import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfp.bean.AgricultureFarmerCollection;
import com.szfp.szfp.bean.BankCustomerBean;
import com.szfp.szfp.bean.BankDepositBean;
import com.szfp.szfp.bean.BankRegistrationBean;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.bean.ParkingInfoBean;
import com.szfp.szfp.bean.ParkingRechargeBean;
import com.szfp.szfp.bean.StudentBean;
import com.szfp.szfp.bean.StudentStaffBean;
import com.szfp.szfp.bean.VehicleInfoBean;
import com.szfp.szfp.bean.VehicleParkingBean;

import com.szfp.szfp.greendao.AccountReportBeanDao;
import com.szfp.szfp.greendao.AgricultureEmployeeBeanDao;
import com.szfp.szfp.greendao.AgricultureFarmerBeanDao;
import com.szfp.szfp.greendao.AgricultureFarmerCollectionDao;
import com.szfp.szfp.greendao.BankCustomerBeanDao;
import com.szfp.szfp.greendao.BankDepositBeanDao;
import com.szfp.szfp.greendao.BankRegistrationBeanDao;
import com.szfp.szfp.greendao.CommuterAccountInfoBeanDao;
import com.szfp.szfp.greendao.ParkingInfoBeanDao;
import com.szfp.szfp.greendao.ParkingRechargeBeanDao;
import com.szfp.szfp.greendao.StudentBeanDao;
import com.szfp.szfp.greendao.StudentStaffBeanDao;
import com.szfp.szfp.greendao.VehicleInfoBeanDao;
import com.szfp.szfp.greendao.VehicleParkingBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig accountReportBeanDaoConfig;
    private final DaoConfig agricultureEmployeeBeanDaoConfig;
    private final DaoConfig agricultureFarmerBeanDaoConfig;
    private final DaoConfig agricultureFarmerCollectionDaoConfig;
    private final DaoConfig bankCustomerBeanDaoConfig;
    private final DaoConfig bankDepositBeanDaoConfig;
    private final DaoConfig bankRegistrationBeanDaoConfig;
    private final DaoConfig commuterAccountInfoBeanDaoConfig;
    private final DaoConfig parkingInfoBeanDaoConfig;
    private final DaoConfig parkingRechargeBeanDaoConfig;
    private final DaoConfig studentBeanDaoConfig;
    private final DaoConfig studentStaffBeanDaoConfig;
    private final DaoConfig vehicleInfoBeanDaoConfig;
    private final DaoConfig vehicleParkingBeanDaoConfig;

    private final AccountReportBeanDao accountReportBeanDao;
    private final AgricultureEmployeeBeanDao agricultureEmployeeBeanDao;
    private final AgricultureFarmerBeanDao agricultureFarmerBeanDao;
    private final AgricultureFarmerCollectionDao agricultureFarmerCollectionDao;
    private final BankCustomerBeanDao bankCustomerBeanDao;
    private final BankDepositBeanDao bankDepositBeanDao;
    private final BankRegistrationBeanDao bankRegistrationBeanDao;
    private final CommuterAccountInfoBeanDao commuterAccountInfoBeanDao;
    private final ParkingInfoBeanDao parkingInfoBeanDao;
    private final ParkingRechargeBeanDao parkingRechargeBeanDao;
    private final StudentBeanDao studentBeanDao;
    private final StudentStaffBeanDao studentStaffBeanDao;
    private final VehicleInfoBeanDao vehicleInfoBeanDao;
    private final VehicleParkingBeanDao vehicleParkingBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        accountReportBeanDaoConfig = daoConfigMap.get(AccountReportBeanDao.class).clone();
        accountReportBeanDaoConfig.initIdentityScope(type);

        agricultureEmployeeBeanDaoConfig = daoConfigMap.get(AgricultureEmployeeBeanDao.class).clone();
        agricultureEmployeeBeanDaoConfig.initIdentityScope(type);

        agricultureFarmerBeanDaoConfig = daoConfigMap.get(AgricultureFarmerBeanDao.class).clone();
        agricultureFarmerBeanDaoConfig.initIdentityScope(type);

        agricultureFarmerCollectionDaoConfig = daoConfigMap.get(AgricultureFarmerCollectionDao.class).clone();
        agricultureFarmerCollectionDaoConfig.initIdentityScope(type);

        bankCustomerBeanDaoConfig = daoConfigMap.get(BankCustomerBeanDao.class).clone();
        bankCustomerBeanDaoConfig.initIdentityScope(type);

        bankDepositBeanDaoConfig = daoConfigMap.get(BankDepositBeanDao.class).clone();
        bankDepositBeanDaoConfig.initIdentityScope(type);

        bankRegistrationBeanDaoConfig = daoConfigMap.get(BankRegistrationBeanDao.class).clone();
        bankRegistrationBeanDaoConfig.initIdentityScope(type);

        commuterAccountInfoBeanDaoConfig = daoConfigMap.get(CommuterAccountInfoBeanDao.class).clone();
        commuterAccountInfoBeanDaoConfig.initIdentityScope(type);

        parkingInfoBeanDaoConfig = daoConfigMap.get(ParkingInfoBeanDao.class).clone();
        parkingInfoBeanDaoConfig.initIdentityScope(type);

        parkingRechargeBeanDaoConfig = daoConfigMap.get(ParkingRechargeBeanDao.class).clone();
        parkingRechargeBeanDaoConfig.initIdentityScope(type);

        studentBeanDaoConfig = daoConfigMap.get(StudentBeanDao.class).clone();
        studentBeanDaoConfig.initIdentityScope(type);

        studentStaffBeanDaoConfig = daoConfigMap.get(StudentStaffBeanDao.class).clone();
        studentStaffBeanDaoConfig.initIdentityScope(type);

        vehicleInfoBeanDaoConfig = daoConfigMap.get(VehicleInfoBeanDao.class).clone();
        vehicleInfoBeanDaoConfig.initIdentityScope(type);

        vehicleParkingBeanDaoConfig = daoConfigMap.get(VehicleParkingBeanDao.class).clone();
        vehicleParkingBeanDaoConfig.initIdentityScope(type);

        accountReportBeanDao = new AccountReportBeanDao(accountReportBeanDaoConfig, this);
        agricultureEmployeeBeanDao = new AgricultureEmployeeBeanDao(agricultureEmployeeBeanDaoConfig, this);
        agricultureFarmerBeanDao = new AgricultureFarmerBeanDao(agricultureFarmerBeanDaoConfig, this);
        agricultureFarmerCollectionDao = new AgricultureFarmerCollectionDao(agricultureFarmerCollectionDaoConfig, this);
        bankCustomerBeanDao = new BankCustomerBeanDao(bankCustomerBeanDaoConfig, this);
        bankDepositBeanDao = new BankDepositBeanDao(bankDepositBeanDaoConfig, this);
        bankRegistrationBeanDao = new BankRegistrationBeanDao(bankRegistrationBeanDaoConfig, this);
        commuterAccountInfoBeanDao = new CommuterAccountInfoBeanDao(commuterAccountInfoBeanDaoConfig, this);
        parkingInfoBeanDao = new ParkingInfoBeanDao(parkingInfoBeanDaoConfig, this);
        parkingRechargeBeanDao = new ParkingRechargeBeanDao(parkingRechargeBeanDaoConfig, this);
        studentBeanDao = new StudentBeanDao(studentBeanDaoConfig, this);
        studentStaffBeanDao = new StudentStaffBeanDao(studentStaffBeanDaoConfig, this);
        vehicleInfoBeanDao = new VehicleInfoBeanDao(vehicleInfoBeanDaoConfig, this);
        vehicleParkingBeanDao = new VehicleParkingBeanDao(vehicleParkingBeanDaoConfig, this);

        registerDao(AccountReportBean.class, accountReportBeanDao);
        registerDao(AgricultureEmployeeBean.class, agricultureEmployeeBeanDao);
        registerDao(AgricultureFarmerBean.class, agricultureFarmerBeanDao);
        registerDao(AgricultureFarmerCollection.class, agricultureFarmerCollectionDao);
        registerDao(BankCustomerBean.class, bankCustomerBeanDao);
        registerDao(BankDepositBean.class, bankDepositBeanDao);
        registerDao(BankRegistrationBean.class, bankRegistrationBeanDao);
        registerDao(CommuterAccountInfoBean.class, commuterAccountInfoBeanDao);
        registerDao(ParkingInfoBean.class, parkingInfoBeanDao);
        registerDao(ParkingRechargeBean.class, parkingRechargeBeanDao);
        registerDao(StudentBean.class, studentBeanDao);
        registerDao(StudentStaffBean.class, studentStaffBeanDao);
        registerDao(VehicleInfoBean.class, vehicleInfoBeanDao);
        registerDao(VehicleParkingBean.class, vehicleParkingBeanDao);
    }
    
    public void clear() {
        accountReportBeanDaoConfig.clearIdentityScope();
        agricultureEmployeeBeanDaoConfig.clearIdentityScope();
        agricultureFarmerBeanDaoConfig.clearIdentityScope();
        agricultureFarmerCollectionDaoConfig.clearIdentityScope();
        bankCustomerBeanDaoConfig.clearIdentityScope();
        bankDepositBeanDaoConfig.clearIdentityScope();
        bankRegistrationBeanDaoConfig.clearIdentityScope();
        commuterAccountInfoBeanDaoConfig.clearIdentityScope();
        parkingInfoBeanDaoConfig.clearIdentityScope();
        parkingRechargeBeanDaoConfig.clearIdentityScope();
        studentBeanDaoConfig.clearIdentityScope();
        studentStaffBeanDaoConfig.clearIdentityScope();
        vehicleInfoBeanDaoConfig.clearIdentityScope();
        vehicleParkingBeanDaoConfig.clearIdentityScope();
    }

    public AccountReportBeanDao getAccountReportBeanDao() {
        return accountReportBeanDao;
    }

    public AgricultureEmployeeBeanDao getAgricultureEmployeeBeanDao() {
        return agricultureEmployeeBeanDao;
    }

    public AgricultureFarmerBeanDao getAgricultureFarmerBeanDao() {
        return agricultureFarmerBeanDao;
    }

    public AgricultureFarmerCollectionDao getAgricultureFarmerCollectionDao() {
        return agricultureFarmerCollectionDao;
    }

    public BankCustomerBeanDao getBankCustomerBeanDao() {
        return bankCustomerBeanDao;
    }

    public BankDepositBeanDao getBankDepositBeanDao() {
        return bankDepositBeanDao;
    }

    public BankRegistrationBeanDao getBankRegistrationBeanDao() {
        return bankRegistrationBeanDao;
    }

    public CommuterAccountInfoBeanDao getCommuterAccountInfoBeanDao() {
        return commuterAccountInfoBeanDao;
    }

    public ParkingInfoBeanDao getParkingInfoBeanDao() {
        return parkingInfoBeanDao;
    }

    public ParkingRechargeBeanDao getParkingRechargeBeanDao() {
        return parkingRechargeBeanDao;
    }

    public StudentBeanDao getStudentBeanDao() {
        return studentBeanDao;
    }

    public StudentStaffBeanDao getStudentStaffBeanDao() {
        return studentStaffBeanDao;
    }

    public VehicleInfoBeanDao getVehicleInfoBeanDao() {
        return vehicleInfoBeanDao;
    }

    public VehicleParkingBeanDao getVehicleParkingBeanDao() {
        return vehicleParkingBeanDao;
    }

}
