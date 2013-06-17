package com.bvcom.transmit.util;

public class DaoException extends CommonException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public DaoException() {
        super();
    }

    /**
     * @param arg0
     */
    public DaoException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public DaoException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public DaoException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
    
}
