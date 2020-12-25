package utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static utility.DB_Utility.*;

public class DB_utility_test {

    public static void main(String[] args) throws SQLException {
        //createConnection(ConfigurationReader.getProperty("hr.database.url"),ConfigurationReader.getProperty("hr.database.username"),ConfigurationReader.getProperty("hr.database.password"));
        createConnection();
        ResultSet result = runQuery("SELECT * FROM EMPLOYEES");
//        System.out.println("result = " + result);
//        resultSet.next();
//        System.out.println("rs = " + resultSet.getString(1));
//
//        System.out.println(getRowCount());
//        System.out.println(getColumnCount());
        //ArrayList<String> labels = new ArrayList<String>(getColumnNames());
        //labels.forEach(System.out::println);
        System.out.println(getColumnCount());
        resultSet.beforeFirst();
        List<String> rowDataAsList = getRowDataAsList(46);
        //System.out.println(rowDataAsList);
        //System.out.println("rowCount() = " + rowCount());
        //System.out.println("getRowCount() = " + getRowCount());
        //System.out.println("getColumnDataAtRow(67, 2) = " + getColumnDataAtRow(67, 2));
        //System.out.println("getColumnDataAtRow(67, name) = " + getColumnDataAtRow(67, "FIRST_NAME"));
        //getColumnAsList(2).forEach(System.out::println);
        //getColumnAsList("FIRST_NAME").forEach(System.out::println);
        //displayAllData();
        //System.out.println("getRowDataAsMap(2) = " + getRowDataAsMap(2));
        tableRowsAsList().forEach(System.out::println);
        tearDown();







    }




}
