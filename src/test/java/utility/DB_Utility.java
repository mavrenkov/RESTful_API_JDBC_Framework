package utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_Utility {
    /**
     *  Connection (session) with a specific
     *  database. SQL statements are executed and results are returned
     *  within the context of a connection.
     */
    static Connection conn;
    /**
     * Before you can use a Statement object to execute a SQL statement,
     * you need to create one using the Connection object's createStatement( ) method.
     * Once you've created a Statement object, you can then use it to execute an SQL statement
     * with one of its three execute methods: boolean execute (String SQL) ; int executeUpdate (String SQL);
     * ResultSet executeQuery (String SQL);
     */
    static Statement statement;
    /**
     * resultSet  was returned by using ResultSet executeQuery (String SQL) method on the Statement Object.
     * A ResultSet object maintains a cursor that points to the current row in the result set.
     * The term "result set" refers to the row and column data contained in a ResultSet object.
     * There are several methods that you can use from ResultSet Interface: navigational methods; get methods; update methods.
     */
    static ResultSet resultSet;
    /**
     * ResultSetMetaData is the Data about Data
     * An object that can be used to get information about the types and properties of the columns
     *  <PRE>
     *
     *       ResultSet rs = stmt.executeQuery("SELECT a, b, c FROM TABLE2");
     *       ResultSetMetaData rsmd = rs.getMetaData();
     *       int numberOfColumns = rsmd.getColumnCount();
     *       boolean b = rsmd.isSearchable(1);
     *
     *  </PRE>
     * This code example returns:
     * int: number of columns of specified table
     * boolean condition: can we use WHERE clause for this specific column number
     */
    static ResultSetMetaData metaData;

    /**
     * The static method to create a connection, where:
     * 1. Connection STR: The connection URL for the oracle database is jdbc:oracle:thin:@localhost:1521:xe
     * where jdbc is the API, oracle is the database, thin is the driver,
     * localhost is the server name on which oracle is running, we may also use IP address,
     * 1521 is the port number and XE is the Oracle service name. You may get all these information from the tnsnames.ora file.
     * 2. username and password: credentials to access the database
     * 3. SQL exception - provides error related to the database connection.
     */

    private final static  String connectionSTR = ConfigurationReader.getProperty("hr.database.url");
    private final static  String username = ConfigurationReader.getProperty("hr.database.username");
    private final static  String password = ConfigurationReader.getProperty("hr.database.password");

    public static void createConnection(){
        try {
            /**
             * The most common approach to register a driver is to use Java's Class.forName() method,
             * to dynamically load the driver's class file into memory, which automatically registers it.
             * This method is preferable because it allows you to make the driver registration configurable and portable.
             * You can use newInstance() method to work around non-compliant JVMs,
             * but then you'll have to code for two extra Exceptions as follows : IllegalAccessException / InstantiationException
             */
            Class.forName(ConfigurationReader.getProperty("hr.database.driver")).newInstance();
            conn = DriverManager.getConnection(connectionSTR,username,password);
            System.out.println("Connection successful ");
        } catch (SQLException e) {
            System.out.println("Connection has failed! " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class.forName not found */* createConnection method "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }catch (IllegalAccessException e){
            System.out.println("Error: access problem while loading! "+e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }catch (InstantiationException e){
            System.out.println("Error: unable to instantiate driver! " +e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
    }

    /**
     * This is used to establish the connection with the specified url, username and password.
     */
    public static void createConnection( String connectionSTR, String username, String password){
        try {
            Class.forName(ConfigurationReader.getProperty("hr.database.driver"));
            conn = DriverManager.getConnection(connectionSTR,username,password);
            System.out.println("Connection successful ");
        } catch (SQLException e) {
            System.out.println("Connection has failed! " + e.getMessage());
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            System.out.println("Class.forName not found */* createConnection method "+e.getMessage());
            e.printStackTrace();
        }
    }

    public static ResultSet runQuery(String query){

        try {
            /**
             * ResultSet.TYPE_SCROLL_INSENSITIVE : The cursor can scroll forward and backward, and the result set is not sensitive to changes made by others to the database that occur after the result set was created.
             * ResultSet.CONCUR_READ_ONLY : Creates a read-only result set. This is the default.
             * If you do not specify any ResultSet type, you will automatically get one that is TYPE_FORWARD_ONLY.
             * If you do not specify any Concurrency type, you will automatically get one that is CONCUR_READ_ONLY.
             */
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();
        } catch (SQLException e) {
            System.out.println("Error while getting ResultSet or Statement creation " + e.getMessage());
            e.printStackTrace();
        }

        return resultSet;
    }
    /**
     * This method is responsible for closing everything:
     * Is it necessary? no. Is it a good practice? yes.
     * Database has a connection limit and you don't want to reach this limit.
     */
    public static void tearDown(){
            try {
                if(conn!=null)conn.close();
                if(statement!=null)statement.close();
                if(resultSet!=null)resultSet.close();
                System.out.println("Connection is closed!!");
            } catch (SQLException e) {
                System.out.println("Error while closing ResultSet or Statement creation or Connection" + e.getMessage());
                e.printStackTrace();
            }

    }

    /**
     * Get the row count of the resultSet
     * @return the row number of the resultSet given
     * We need to navigate resultSet to last row to get rowCount
     * Than we move resultSet to beforeFirst - so it will not impact our future methods.
     */
    public static int getRowCount(){
        int rowCount =0;
        try {
            resultSet.last();
            rowCount = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (SQLException e) {
            System.out.println("Error while getting row count!! "+ e.getMessage());
            e.printStackTrace();
        }
        return rowCount;
    }

    /**
     * Get the column count by using the MetaData
     * @return number of columns
     * ResultSetMetaData  - An object that can be used to get information about the types
     * and properties of the columns
     */
    public static int getColumnCount(){

        try {
            return metaData.getColumnCount();
        } catch (SQLException e) {
            System.out.println("Error while counting the columns! "+ e.getMessage());
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * Method that will return column names as
     * @return List<String> columnNames
     * We're getting the columnCount from previous method in thi utility.
     */
    public static List<String> getColumnNames(){
        List<String> columnNameList = new ArrayList<>();
        try {
            int colCount = getColumnCount();
            for(int colNum =1;colNum<=colCount;colNum++){
                columnNameList.add(metaData.getColumnLabel(colNum));
            }
        } catch (SQLException e) {
            System.out.println("Error while getting List of column names! "+e.getMessage());
            e.printStackTrace();
        }
        return columnNameList;
    }

    /**
     * This method will return all of the data(from each cell) related to specified row
     * @param rowNum row number where you will take data from
     * @return return List of <String> including data from each cell from specified row.
     * In this method we're using the getColumnCount method that returns number of columns as int(we need it to specify loop condition)
     * resultSet.absolute(rowNum) - method that is setting our view on specific row number(@param rowNum)
     */
    public static List<String> getRowDataAsList(int rowNum){
        List<String> rowDataList = new ArrayList<>();
        try {
            resultSet.absolute(rowNum);
            int colCount = getColumnCount();
            for (int colNum = 1; colNum < colCount; colNum++) {
                rowDataList.add(resultSet.getString(colNum));
            }
        } catch (SQLException e) {
            System.out.println("Error while getting RowDataList! " +e.getMessage());
            e.printStackTrace();
        }
        return rowDataList;
    }










}




