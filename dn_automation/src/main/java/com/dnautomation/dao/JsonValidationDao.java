package main.java.com.dnautomation.dao;

import com.mysql.jdbc.Connection;
import generic.utils.DBLib;

import java.sql.SQLException;

public class JsonValidationDao {

    public String[][] fetchDataForMultiColumn(String query){
        Connection con=null;
        String[][] response = null;
        try{
            DBLib dbLib = new DBLib();
            con = dbLib.mysqlConnectionForReporting();
            response = dbLib.ExecuteMySQLQueryReturnsArray(con, query);
        }catch(Exception e){
            System.out.println("error in fetching data from db.");
            //return "";
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    private boolean insertDataToDB(String query) throws Exception{

        Connection con=null;
        try{
            DBLib dbLib = new DBLib();
            con = dbLib.mysqlConnectionForReporting();
            dbLib.executeUpdateInsertQuery(con, query);

        }catch(Exception e){
            throw new Exception("error in inserting data to db");
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
