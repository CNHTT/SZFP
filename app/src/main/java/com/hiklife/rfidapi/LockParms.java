package com.hiklife.rfidapi;

public class LockParms {
	public PasswordPermission accessPasswordPermission;
    public PasswordPermission killPasswordPermission;
    public MemoryPermission TIDMemoryBankPermissions;
    public MemoryPermission EPCMemoryBankPermissions;
    public MemoryPermission USERMemoryBankPermissions;

    public LockParms()
    {
        accessPasswordPermission = PasswordPermission.Unknown;
        killPasswordPermission = PasswordPermission.Unknown;
        TIDMemoryBankPermissions = MemoryPermission.Unknown;
        EPCMemoryBankPermissions = MemoryPermission.Unknown;
        USERMemoryBankPermissions = MemoryPermission.Unknown;
    }
}
