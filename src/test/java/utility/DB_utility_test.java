package utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static utility.DB_Utility.*;

public class DB_utility_test {

    public static void main(String[] args) throws SQLException {
   createConnection(ConfigurationReader.getProperty("hr.database.url"),ConfigurationReader.getProperty("hr.database.username"),ConfigurationReader.getProperty("hr.database.password"));
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
        System.out.println(rowDataAsList);

        tearDown();

    }




}
