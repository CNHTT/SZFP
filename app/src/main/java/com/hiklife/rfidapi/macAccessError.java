package com.hiklife.rfidapi;

public enum macAccessError {
	Ok, 
	
	HandleMismatch,

    CRCErrorOnTagResponse,

    NoTagReply,

    InvalidPassword,

    ZeroKillPassword,

    TagLost,

    CommandFormatError,

    ReadCountInvalid,

    OutOfRetries,

    OperationFailed
}
