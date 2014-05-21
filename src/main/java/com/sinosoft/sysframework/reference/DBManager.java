package com.sinosoft.sysframework.reference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.sinosoft.sysframework.common.datatype.DateTime;
import com.sinosoft.sysframework.common.util.DataUtils;
import com.sinosoft.sysframework.common.util.StringUtils;

public class DBManager
{
    private Statement statement = null;
    private Connection connection = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;
    private DBDataSource dbDataSource = null;
    private HashMap dataSources = new HashMap();
    private boolean useTrim = true;
    private String name;

    public DBManager()
    {
    }

    public DataSource getDataSource(String name)
            throws NamingException
    {

        Object object = this.dataSources.get(name);
        if (object != null) {
            this.name = name;
            return (DataSource)object;
        }

        Context context = new InitialContext();
        DBDataSource dbDataSource = DBFactory.getDB(name);
        DataSource dataSource;
        try
        {
            dataSource = (DataSource)context.lookup(dbDataSource.getJndiName());

        }
        catch (NamingException e)
        {

            dataSource = (DataSource)context.lookup("java:comp/env/" + dbDataSource.getJndiName());
        }
        //包装dataSource用于与Spring整合
        dataSource = new TransactionAwareDataSourceProxy(dataSource);
        this.dataSources.put(name, dataSource);
        this.name = name;
        return dataSource;
    }

    public void open(String name)
            throws Exception
    {

        DataSource dataSource = getDataSource(name);
        open(name, dataSource.getConnection());
    }

    private void open(String name, Connection connection)
            throws Exception
    {

        this.dbDataSource = DBFactory.getDB(name);
        open(this.dbDataSource,  connection);
//         open(name);
    }

    private void open(DBDataSource dbDataSource, Connection connection)
            throws Exception
    {
        this.dbDataSource = dbDataSource;

        this.connection = getDataSource(dbDataSource.getName()).getConnection();
        try
        {
            this.statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }
        catch (SQLException e)
        {
        this.connection.close();
        throw e;
        }
    }

    public void close()
            throws Exception
    {
        Exception sqle = null;
        if (this.resultSet != null) {
            try {
                this.resultSet.close();
                this.resultSet = null;
            } catch (Exception e) {
                sqle = e;
            }
        }
        if (this.statement != null) {
            try {
                this.statement.close();
                this.statement = null;
            } catch (Exception e) {
                sqle = e;
            }
        }
        if (this.preparedStatement != null) {
            try {
                this.preparedStatement.close();
                this.preparedStatement = null;
            } catch (Exception e) {
                sqle = e;
            }
        }
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
        if (sqle != null)
            throw sqle;
    }

    public void beginTransaction()
            throws Exception
    {
        if (notUsedSpringTranscation()&&this.connection.getAutoCommit() == true)
            this.connection.setAutoCommit(false);

    }

    private boolean notUsedSpringTranscation() throws NamingException {
        return !DataSourceUtils.isConnectionTransactional(this.connection,this.getDataSource(this.name)) && !TransactionSynchronizationManager.isSynchronizationActive();
    }

    public void commitTransaction()
            throws Exception
    {

        if (notUsedSpringTranscation()&&this.connection != null) {

            this.connection.commit();
            this.connection.setAutoCommit(true);
        }
    }

    public void rollbackTransaction()
            throws Exception
    {
        if (notUsedSpringTranscation()&&this.connection != null) {
            this.connection.rollback();
            this.connection.setAutoCommit(true);
        }
    }

    public void prepareStatement(String statement)
            throws Exception
    {
        if (this.preparedStatement != null) {
            this.preparedStatement.close();
        }
        this.preparedStatement = this.connection.prepareStatement(statement, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    public void addBatch()
            throws Exception
    {
        this.preparedStatement.addBatch();
    }

    public int[] executePreparedUpdateBatch()
            throws Exception
    {
        return this.preparedStatement.executeBatch();
    }

    public void setString(int index, String value)
            throws Exception
    {
        value = DataUtils.nullToEmpty(value);
        if ((this.dbDataSource.isNeedConvertFromServerToDb()) && (
                (!this.dbDataSource.getServerToDbDecodeCharset().equals("")) || (!this.dbDataSource.getServerToDbEncodeCharset().equals(""))))
        {
            if (this.dbDataSource.getServerToDbDecodeCharset().equals("")) {
                value = new String(value.getBytes(), this.dbDataSource.getServerToDbEncodeCharset());
            }
            else if (this.dbDataSource.getServerToDbEncodeCharset().equals("")) {
                value = new String(value.getBytes(this.dbDataSource.getServerToDbDecodeCharset()));
            }
            else {
                value = new String(value.getBytes(this.dbDataSource.getServerToDbDecodeCharset()), this.dbDataSource.getServerToDbEncodeCharset());
            }

        }

        if (this.useTrim) {
            value = StringUtils.rightTrim(value);
        }
        this.preparedStatement.setString(index, value);
    }

    public void setInt(int index, int value)
            throws Exception
    {
        this.preparedStatement.setInt(index, value);
    }

    public void setLong(int index, long value)
            throws Exception
    {
        this.preparedStatement.setLong(index, value);
    }

    public void setDouble(int index, double value)
            throws Exception
    {
        this.preparedStatement.setDouble(index, value);
    }

    public void setDateTime(int index, DateTime value)
            throws Exception
    {
        if (value != null)
            if (value.isEmpty()) {
                this.preparedStatement.setNull(index, 93);
            } else {
                Timestamp time = new Timestamp(value.getTime());
                this.preparedStatement.setTimestamp(index, time);
            }
    }

    public void setBytes(int index, byte[] value)
            throws Exception
    {
        this.preparedStatement.setBytes(index, value);
    }

    public String getString(ResultSet resultSet, int index)
            throws Exception
    {
        String value = resultSet.getString(index);
        value = DataUtils.dbNullToEmpty(value);
        if ((this.dbDataSource.isNeedConvertFromDbToServer()) && (
                (!this.dbDataSource.getDbToServerDecodeCharset().equals("")) || (!this.dbDataSource.getDbToServerEncodeCharset().equals(""))))
        {
            if (this.dbDataSource.getDbToServerDecodeCharset().equals("")) {
                value = new String(value.getBytes(), this.dbDataSource.getDbToServerEncodeCharset());
            }
            else if (this.dbDataSource.getDbToServerEncodeCharset().equals("")) {
                value = new String(value.getBytes(this.dbDataSource.getDbToServerDecodeCharset()));
            }
            else {
                value = new String(value.getBytes(this.dbDataSource.getDbToServerDecodeCharset()), this.dbDataSource.getDbToServerEncodeCharset());
            }

        }

        if (this.useTrim) {
            value = StringUtils.rightTrim(value);
        }
        return value;
    }

    public int getInt(ResultSet resultSet, int index)
            throws Exception
    {
        return resultSet.getInt(index);
    }

    public long getLong(ResultSet resultSet, int index)
            throws Exception
    {
        return resultSet.getLong(index);
    }

    public double getDouble(ResultSet resultSet, int index)
            throws Exception
    {
        return resultSet.getDouble(index);
    }

    public DateTime getDateTime(ResultSet resultSet, int type, int index)
            throws Exception
    {
        Timestamp time = resultSet.getTimestamp(index);
        DateTime d = null;
        if (time == null)
            d = new DateTime();
        else {
            d = new DateTime(time, type);
        }
        return d;
    }

    /** @deprecated */
    public DateTime getDateTime(ResultSet resultSet, int index)
            throws Exception
    {
        Timestamp time = resultSet.getTimestamp(index);
        DateTime d = null;
        if (time == null)
            d = new DateTime();
        else {
            d = new DateTime(time, 13);
        }
        return d;
    }

    /** @deprecated */
    public DateTime getDateTime(ResultSet resultSet, String name)
            throws Exception
    {
        Timestamp time = resultSet.getTimestamp(name);
        DateTime d = null;
        if (time == null)
            d = new DateTime();
        else {
            d = new DateTime(time, 13);
        }
        return d;
    }

    public byte[] getBytes(ResultSet resultSet, int index)
            throws Exception
    {
        return resultSet.getBytes(index);
    }

    public String getString(ResultSet resultSet, String name)
            throws Exception
    {
        String value = resultSet.getString(name);
        value = DataUtils.dbNullToEmpty(value);
        if ((this.dbDataSource.isNeedConvertFromDbToServer()) && (
                (!this.dbDataSource.getDbToServerDecodeCharset().equals("")) || (!this.dbDataSource.getDbToServerEncodeCharset().equals(""))))
        {
            if (this.dbDataSource.getDbToServerDecodeCharset().equals("")) {
                value = new String(value.getBytes(), this.dbDataSource.getDbToServerEncodeCharset());
            }
            else if (this.dbDataSource.getDbToServerEncodeCharset().equals("")) {
                value = new String(value.getBytes(this.dbDataSource.getDbToServerDecodeCharset()));
            }
            else {
                value = new String(value.getBytes(this.dbDataSource.getDbToServerDecodeCharset()), this.dbDataSource.getDbToServerEncodeCharset());
            }

        }

        if (this.useTrim) {
            value = StringUtils.rightTrim(value);
        }
        return value;
    }

    public int getInt(ResultSet resultSet, String name)
            throws Exception
    {
        return resultSet.getInt(name);
    }

    public long getLong(ResultSet resultSet, String name)
            throws Exception
    {
        return resultSet.getLong(name);
    }

    public double getDouble(ResultSet resultSet, String name)
            throws Exception
    {
        return resultSet.getDouble(name);
    }

    public DateTime getDateTime(ResultSet resultSet, int type, String name)
            throws Exception
    {
        Timestamp time = resultSet.getTimestamp(name);
        DateTime d = null;
        if (time == null)
            d = new DateTime();
        else {
            d = new DateTime(time, type);
        }
        return d;
    }

    public byte[] getBytes(ResultSet resultSet, String name)
            throws Exception
    {
        return resultSet.getBytes(name);
    }

    public ResultSet executeQuery(String sqlString)
            throws Exception
    {
        if ((this.dbDataSource.isNeedConvertFromServerToDb()) && (
                (!this.dbDataSource.getServerToDbDecodeCharset().equals("")) || (!this.dbDataSource.getServerToDbEncodeCharset().equals(""))))
        {
            if (this.dbDataSource.getServerToDbDecodeCharset().equals("")) {
                sqlString = new String(sqlString.getBytes(), this.dbDataSource.getServerToDbEncodeCharset());
            }
            else if (this.dbDataSource.getServerToDbEncodeCharset().equals("")) {
                sqlString = new String(sqlString.getBytes(this.dbDataSource.getServerToDbDecodeCharset()));
            }
            else {
                sqlString = new String(sqlString.getBytes(this.dbDataSource.getServerToDbDecodeCharset()), this.dbDataSource.getServerToDbEncodeCharset());
            }

        }

        this.resultSet = this.statement.executeQuery(sqlString);
        return this.resultSet;
    }

    public void locate(ResultSet resultSet, int position)
            throws Exception
    {
        try
        {
            resultSet.absolute(position);
        } catch (Exception e) {
            for (int i = 0; i < position; i++)
                resultSet.next();
        }
    }

    public int executeUpdate(String sqlString)
            throws Exception
    {
        if ((this.dbDataSource.isNeedConvertFromServerToDb()) && (
                (!this.dbDataSource.getServerToDbDecodeCharset().equals("")) || (!this.dbDataSource.getServerToDbEncodeCharset().equals(""))))
        {
            if (this.dbDataSource.getServerToDbDecodeCharset().equals("")) {
                sqlString = new String(sqlString.getBytes(), this.dbDataSource.getServerToDbEncodeCharset());
            }
            else if (this.dbDataSource.getServerToDbEncodeCharset().equals("")) {
                sqlString = new String(sqlString.getBytes(this.dbDataSource.getServerToDbDecodeCharset()));
            }
            else {
                sqlString = new String(sqlString.getBytes(this.dbDataSource.getServerToDbDecodeCharset()), this.dbDataSource.getServerToDbEncodeCharset());
            }

        }

        int result = this.statement.executeUpdate(sqlString);
        return result;
    }

    public int executePreparedUpdate()
            throws Exception
    {
        int result = this.preparedStatement.executeUpdate();
        return result;
    }

    public ResultSet executePreparedQuery()
            throws Exception
    {
        this.resultSet = this.preparedStatement.executeQuery();
        return this.resultSet;
    }

    public boolean hasColumn(String tableName, String columnName)
            throws SQLException, Exception
    {
        boolean blnFoundFlag = false;
        String sqlString = " SELECT " + columnName + " FROM " + tableName + " WHERE 1=0";
        try
        {
            this.statement.executeQuery(sqlString);
            blnFoundFlag = true;
        } catch (SQLException e) {
            blnFoundFlag = false;
        }
        return blnFoundFlag;
    }

    public void setUseTrim(boolean useTrim)
    {
        this.useTrim = useTrim;
    }

    public int getCount(String statement)
            throws Exception
    {
        int count = -1;
        ResultSet resultSet = executeQuery(statement);
        resultSet.next();
        count = getInt(resultSet, 1);
        resultSet.close();
        return count;
    }

    public String[] getTableColumnNames(String tableName)
            throws Exception
    {
        this.resultSet = this.statement.executeQuery("SELECT * FROM " + tableName + " WHERE 1=0");

        ResultSetMetaData rmeta = this.resultSet.getMetaData();

        int numColumns = rmeta.getColumnCount();
        String[] columns = new String[numColumns];

        for (int i = 1; i <= numColumns; i++) {
            columns[(i - 1)] = rmeta.getColumnName(i);
        }
        return columns;
    }

    public Connection getConnection()
    {
        return this.connection;
    }

    public Statement getStatement()
    {
        return this.statement;
    }

    public String getDSName()
    {
        return this.dbDataSource.getName();
    }
}                                                                                                                                                                                        
                                                                                                                                                                                         