package com.hiklife.rfidapi;

public class radioFailException extends Exception {
	private static final long serialVersionUID = 101L;
	
	/**
     * Constructs a new exception with <code>null</code> as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public radioFailException() {
       super();
    }

    /**
     * @param message
     *        the detail message. The detail message is saved for later
     *        retrieval by the {@link #getMessage()} method.
     */
    public radioFailException(String message) {
       super(message);
    }

    /**
     * @param message
     *        the detail message (which is saved for later retrieval by the
     *        {@link #getMessage()} method).
     * @param cause
     *        the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method). (A <tt>null</tt> value is
     *        permitted, and indicates that the cause is nonexistent or
     *        unknown.)
     * @since 1.4
     */
    public radioFailException(String message, Throwable cause) {
       super(message, cause);
    }

    /**
     * @param cause
     *        the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method). (A <tt>null</tt> value is
     *        permitted, and indicates that the cause is nonexistent or
     *        unknown.)
     * @since 1.4
     */
    public radioFailException(Throwable cause) {
       super(cause);
    }
}