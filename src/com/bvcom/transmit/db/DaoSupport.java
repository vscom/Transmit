/**
 * Web2.0
 * 
 * DaoSupport.java    2008.4.1
 * 
 * Copyright 2008 BVCOM. All Rights Reserved.
 * http://www.cnbvcom.com
 * 
 */
package com.bvcom.transmit.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.bvcom.transmit.util.DaoException;

/**
 * Create Data Sources
 * 
 * @author ±ß ½­
 * @sinace 2008.4.1
 * 
 */
public class DaoSupport {

    //private static final Log log = LogFactory.getLog(DaoSupport.class);

    /**
     * Create Data Sources Connection
     * 
     * @return Connection
     * @throws DaoException
     */
    public static Connection getJDBCConnection() throws DaoException {
        InitialContext initContext = null;
        try {
            initContext = new InitialContext();
            // get naming context
            Context context = (Context) initContext.lookup("java:comp/env");
            // Look up our data source
            DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
            return dataSource.getConnection();
        } catch (NamingException e) {
            if (initContext != null) {
                try {
                    initContext.close();
                } catch (NamingException ex) {
                    throw new DaoException(ex);
                }
            }
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    /**
     * Close Database Connection pooling
     * 
     * @param connection
     * @throws DaoException
     */
    public static void close(Connection connection) throws DaoException {
        if (connection != null) {
            try {
                // Close Database Connection
                connection.close();
            } catch (SQLException e) {
                throw new DaoException(e.getMessage(), e);
            }
        }
    }

    /**
     * Close PreparedStatement
     * 
     * @param statement
     * @throws DaoException
     */
    public static void close(PreparedStatement statement) throws DaoException {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new DaoException(e.getMessage(), e);
            }
        }
    }

    /**
     * Close Statement
     * 
     * @param statement
     * @throws DaoException
     */
    public static void close(Statement statement) throws DaoException {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new DaoException(e.getMessage(), e);
            }
        }
    }

    /**
     * Close ResuletSet
     * 
     * @param resultSet
     * @throws DaoException
     */
    public static void close(ResultSet resultSet) throws DaoException {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DaoException(e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        Connection oConn = null;
        try {
            oConn = getJDBCConnection();
        } catch (Exception e1) {

        }
    }

}
