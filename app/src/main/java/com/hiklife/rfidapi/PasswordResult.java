package com.hiklife.rfidapi;

public class PasswordResult {
	private String flagid = "";
	public short[] epc;
    public int accessPasswordValue;
    public backscatterError backscatterErrorCode;
    public macAccessError macAccessErrorCode;
    public tagMemoryOpResult result;
    
    public String getFlagID()
    {
    	if (flagid != "")
        {
            return flagid;
        }
        else
        {
            if (epc != null)
            {
                for (int i = 0; i < epc.length; i++)
                {
                    flagid += Integer.toHexString(((epc[i] >> 8) & 0x000000FF) | 0xFFFFFF00).substring(6) + Integer.toHexString((epc[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
                }

                return flagid;
            }
            else
            {
                return "";
            }
        }
    }
}
