package utility;

import java.sql.*;
import java.util.*;

public class DB_Utility {
    /**
     *  Connection (session) with a specific database.
     *  SQL statements are executed and results are returned within the context of a connection.
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

    public static int rowCount(){
        int rowCount = 0;
            try {
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    rowCount++;
                }
                resultSet.beforeFirst();
            } catch (SQLException e) {
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


    /**
     * This method gets the String value of given cell from the ResultSet and take two parameters as follows
     * @param rowNum - int number of row with the cell that we want to extract value from
     * @param columnNum - int index of a column with the cell that we want to extract value from
     * @return String value of the given cell
     * This method checks whether given parameters is applicable to current ResultSet and
     * throws IllegalArgumentException() if not.
     */
    public static String getColumnDataAtRow(int rowNum, int columnNum){
            String result = null;
            if(rowNum <= getRowCount() && columnNum <= getColumnCount()){
                try {
                    resultSet.absolute(rowNum);
                    result = resultSet.getString(columnNum);
                    resultSet.beforeFirst();

                } catch (SQLException e) {
                    System.out.println("Error while getting cell value " + e.getMessage());
                }
            }else{
                throw new IllegalArgumentException();
            }
            return result;
    }

    /**
     * This method gets the String value of given cell from the ResultSet and take two parameters as follows
     * @param rowNum - int number of row with the cell that we want to extract value from
     * @param columnName - String name of a column with the cell that we want to extract value from
     * @return String value of the given cell
     * This method checks whether given parameters is applicable to current ResultSet and
     * throws IllegalArgumentException() if not.
     */
    public static String getColumnDataAtRow(int rowNum, String columnName){
        String result = null;

        if(rowNum <= getRowCount() && getColumnNames().contains(columnName)){
            try {
                resultSet.absolute(rowNum);
                result = resultSet.getString(columnName);
                resultSet.beforeFirst();

            } catch (SQLException e) {
                System.out.println("Error while getting cell value " + e.getMessage());
            }
        }else{
            throw new IllegalArgumentException();
        }
        return result;
    }

    /**
     * This method gets all the cell values from every row for the given column
     * @param columnIndex - int index of the column the we want to get cell values for
     * @return List<String> where every list element is a cell String value for given column
     * This method checks whether given parameter is applicable to current ResultSet and
     * throws IllegalArgumentException() if not.
     */
    public static List<String> getColumnAsList(int columnIndex){

        List<String > columnValues = new ArrayList<>();

        if(columnIndex <= getColumnCount()){
            try {
                while (resultSet.next()) {
                    columnValues.add(resultSet.getString(columnIndex));
                }
                resultSet.beforeFirst();
            } catch (SQLException e){
                System.out.println("Error while getting column as list " + e.getMessage());
            }
        } else{
            throw new IllegalArgumentException();
        }
        return columnValues;

    }

    /**
     * This method gets all the cell values from every row for the given column
     * @param columnName - String name of the column the we want to get cell values for
     * @return List<String> where every list element is a cell String value for given column
     * This method checks whether given parameter is applicable to current ResultSet and
     * throws IllegalArgumentException() if not.
     */
    public static List<String> getColumnAsList(String columnName){
        List<String > columnValues = new ArrayList<>();
        if(getColumnNames().contains(columnName)){
            try {
                while (resultSet.next()) {
                    columnValues.add(resultSet.getString(columnName));
                }
                resultSet.beforeFirst();
            } catch (SQLException e){
                System.out.println("Error while getting column as list " + e.getMessage());
            }
        }else{
            throw new IllegalArgumentException();
        }
        return columnValues;
    }

    /**
     * This method simply prints all the cell values from every row and column
     * for the current ResultSet
     */
    public static void displayAllData(){
        try {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                for (int column = 1; column <= getColumnCount(); column++){
                    System.out.print(resultSet.getString(column) + "  ");
                }
                System.out.println();
            }
            resultSet.beforeFirst();
        } catch (SQLException e){
            System.out.println("Error while getting column as list " + e.getMessage());
        }


    }

    /**
     * This method gets all the cell values from the given row in the key-value format
     * @param rowNum - int number of the row, that we want to get values from
     * @return Map<String, String> which is LinkedHashMap<>() where every element is a set of key and value
     * where every key is String name of the column and every value is String value of the cell for that column
     * This method uses getColumnNames() method which allows as to add key column names to the map
     */
    public static Map<String, String> getRowDataAsMap(int rowNum){

        Map<String, String> rowAsMap = new LinkedHashMap<>();
        try {
            resultSet.absolute(rowNum);
            for (int column = 1; column <= getColumnCount(); column++) {
                rowAsMap.put(getColumnNames().get(column-1), resultSet.getString(column));
            }
            resultSet.beforeFirst();
        }catch (SQLException e){
            System.out.println("Error while getting row data as map " + e.getMessage());
        }
        return rowAsMap;
    }

    /**
     * This method gets all the data from current ResultSet and returns it as follows
     * @return List<Map<String, String>> where every element of the list is a row content represented as a Map
     * where every element of the map is a set of key and value
     * where every key is String name of the column and every value is String value of the cell for that column
     * This method uses getColumnNames() method which allows as to add key column names to the maps
     */
    public static List<Map<String, String>> tableRowsAsList(){
        List<Map<String, String>> tableRowsAsList = new ArrayList<>();
        try {
            resultSet.beforeFirst();
            while(resultSet.next()) {
                Map<String, String> rowAsMap = new LinkedHashMap<>();
                for (int column = 1; column <= getColumnCount(); column++) {
                    rowAsMap.put(getColumnNames().get(column - 1), resultSet.getString(column));
                }
                tableRowsAsList.add(rowAsMap);
            }
            resultSet.beforeFirst();
        }catch (SQLException e){
            System.out.println("Error while getting all data as list of maps " + e.getMessage());
        }
        return tableRowsAsList;
    }


}





