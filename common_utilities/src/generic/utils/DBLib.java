/**
 * Last Changes Done on Jan 27, 2015 12:44:43 PM
 * Last Changes Done by Pankaj Katiyar

 * Purpose of change: 
 */
package generic.utils;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;



public class DBLib 
{
	Logger logger = LogManager.getLogger(DBLib.class.getName());

	private Object UNIQ_EXECUTION_ID = "";

	/** Need to have this
	 * 
	 */
	public DBLib(Object UNIQ_EXECUTION_ID)
	{
		this.UNIQ_EXECUTION_ID = UNIQ_EXECUTION_ID;
	}

	public DBLib(){

	}


	public static void main (String [] args) {

		String dburl="jdbc:mysql://13.235.186.44:3306/core";
		String dbuserName="admin";
		String dbpassword="Nykaa@123";

		String select_sql = "select _key, _value from data_engine where _key in ('sp_outoffstock_plp_url', 'sp_customer_also_viewed_pdp_url')"
				+ " and environment='prod' and channel='nykaa'";

		JSONArray jsonArray = new DBLib().getMultipleDBDataAsJsonArray(
				new DBLib().getMySQLConnection(dburl, dbuserName, dbpassword), 
				select_sql);

		System.out.println(jsonArray);

	}

	/** Returns the map of db information, for this, supplied select query should return only one row. 
	 * 
	 * @param NewCon
	 * @return
	 */
	public HashMap<String, String> getDBInformationMap(Connection NewCon, String sqlQuery) 
	{
		HashMap<String, String> hashmap = new HashMap<String, String>();

		try
		{
			logger.info(UNIQ_EXECUTION_ID+" : Running Query - " + sqlQuery);

			Statement NewStmt = (Statement) NewCon.createStatement();
			ResultSet recordSet = (ResultSet) NewStmt.executeQuery(sqlQuery);

			int columns = recordSet.getMetaData().getColumnCount();
			recordSet.beforeFirst();	// Setting the cursor at first line

			while(recordSet.next())
			{
				for(int i=1; i<=columns; i++)
				{
					String key = recordSet.getMetaData().getColumnLabel(i).toString().trim(); 
					String value = recordSet.getString(i).toString().trim();
					hashmap.put(key, value);
				}
			}
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception occurred while getting the information from db: ", e);
		}

		return hashmap;
	}


	/**
	 * get multiple data as json
	 * 
	 * @param NewCon
	 * @param sqlQuery
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getMultipleDBDataAsJsonArray(Connection NewCon, String sqlQuery) 
	{
		JSONArray jsonArray = new JSONArray();

		try
		{
			logger.info(UNIQ_EXECUTION_ID+" : Running Query - " + sqlQuery);

			Statement NewStmt = (Statement) NewCon.createStatement();
			ResultSet recordSet = (ResultSet) NewStmt.executeQuery(sqlQuery);

			int columns = recordSet.getMetaData().getColumnCount();
			recordSet.beforeFirst();	// Setting the cursor at first line

			while(recordSet.next())
			{
				JSONObject jsonObject = new JSONObject();

				for(int i=1; i<=columns; i++)
				{
					String key = recordSet.getMetaData().getColumnLabel(i).toString().trim(); 
					String value = recordSet.getString(i).toString().trim();
					jsonObject.put(key, value);
				}

				jsonArray.add(jsonObject);
			}
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception occurred while getting the information from db: ", e);
		}

		return jsonArray;
	}


	/** This method will execute the update / insert query.
	 * 
	 * @return
	 */
	public boolean executeUpdateInsertQuery(Connection connection, String sql)
	{
		boolean flag;
		try
		{
			if(connection != null) {
				logger.info(UNIQ_EXECUTION_ID+" : Running Query in DB : " + sql);
				Statement statement = (Statement) connection.createStatement();
				statement.executeUpdate(sql);
			}
			flag = true;
		}		
		catch(Exception e)
		{
			flag = false;
			logger.error(UNIQ_EXECUTION_ID+" : Exception occurred while executing query: "+sql, e);
		}
		return flag;
	}

	//********** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name *********************************************//
	public  String [] ExecuteMySQLQueryReturns1DArray(Connection con, String sqlQuery) 
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			if(rs !=null)
			{
				int columns = rs.getMetaData().getColumnCount();
				//logger.info(TestUNIQ_EXECUTION_ID+" :  Column Count: "+columns);

				arrayRecords = new String[columns];

				rs.beforeFirst();	// Setting the cursor at first line	
				while (rs.next())
				{
					for(int i=1;i<=columns;i++)
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[i-1] = strRecord;
						//logger.info(TestUNIQ_EXECUTION_ID+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturns1DArray: " +arrayRecords[i-1]);
					}
				}	
			}
			else
			{
				logger.error(UNIQ_EXECUTION_ID+" : Received NULL record set for the supplied query: "+sqlQuery);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception Handled By: ExecuteMySQLQueryReturns1DArray. ", e);
		}

		return arrayRecords;
	}

	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set without Column Name) *********************************************//
	public  String [][] ExecuteMySQLQueryReturnsArray(Connection con, String sqlQuery) 
	{		

		String [][]arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(TestUNIQ_EXECUTION_ID+" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestUNIQ_EXECUTION_ID+" :  Column Count: "+columns);

			arrayRecords = new String[rows][columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString();
					arrayRecords[rs.getRow()-1][i-1] = strRecord;
					//logger.info(TestUNIQ_EXECUTION_ID+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsArray: " +strRecord);
					//}
				}
				//logger.info(TestUNIQ_EXECUTION_ID+" : ");
			}			
			//logger.info(TestUNIQ_EXECUTION_ID+" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsArray", e);
			logger.error(UNIQ_EXECUTION_ID+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Expection Handled By: ExecuteMySQLQueryReturnsArray", e);
		}

		return arrayRecords;
	}

	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set with Column Name) *********************************************//
	public  String [][] ExecuteMySQLQueryReturnsArrayWithColumnName(Connection con, String sqlQuery) 
	{		
		String [][]arrayRecords = null;
		logger.info(UNIQ_EXECUTION_ID+" : Running this query: "+sqlQuery);

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);
			/*
				//Un-comment this for debugging
				while (rs.next())
				{
					for(int i=1;i<=rs.getMetaData().getColumnCount();i++)
					{
						String strRecord = rs.getString(i).toString();
						System.out.print(" : "+strRecord);
					}
					logger.info();
				}
			 */
			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(TestUNIQ_EXECUTION_ID+" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestUNIQ_EXECUTION_ID+" :  Column Count: "+columns);

			arrayRecords = new String[rows+1][columns];

			rs.beforeFirst();	// Setting the cursor at first line

			while (rs.next())
			{
				int currentRow = rs.getRow();

				for(int i=1;i<=columns;i++)
				{
					if(currentRow == 1)
					{
						String strRecord = rs.getMetaData().getColumnLabel(i).toString();
						arrayRecords[currentRow-1][i-1] = strRecord;
						//logger.info(TestUNIQ_EXECUTION_ID+" : Column Label: " +strRecord);

						String strRecord_1 = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord_1;
						//logger.info(TestUNIQ_EXECUTION_ID+" : Record: " +strRecord_1);
					}
					else
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord;
						//logger.info(TestUNIQ_EXECUTION_ID+" : record in result set: " +strRecord);
					}
				}

			}					
			//logger.info(TestUNIQ_EXECUTION_ID+" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);

		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception Handled by Method : ExecuteMySQLQueryReturnsArray: ",e);
		}
		return arrayRecords;
	}

	/** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name 
	 * 
	 * @param con
	 * @param sqlQuery
	 * @return
	 */
	@SuppressWarnings("finally")
	public  List<String> ExecuteMySQLQueryReturnsList(Connection con, String sqlQuery)
	{		
		List<String> recordList = new ArrayList<String>();

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString().trim();
					recordList.add(strRecord);
				}
			}		
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturns1DArray", e);
			logger.error(UNIQ_EXECUTION_ID+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Expection Handled By: ExecuteMySQLQueryReturnsList. " ,e);
		}
		finally
		{
			return recordList;
		}
	}

	//********** Executing MySQL Query and Returning 1 D Array containing the Only Column Name Of Result Set *********************************************//
	public  String [] ExecuteMySQLQueryReturnsOnlyColumnNames(Connection con, String sqlQuery) throws SQLException
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestUNIQ_EXECUTION_ID+" :  Column Count: "+columns);

			arrayRecords = new String[columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getMetaData().getColumnLabel(i).toString();
					arrayRecords[i-1] = strRecord;
					//logger.info(TestUNIQ_EXECUTION_ID+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsOnlyColumnNames: " +strRecord);
				}
			}		
			con.close();			
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsOnlyColumnNames:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames", e);
			logger.error(UNIQ_EXECUTION_ID+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.",e);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Expection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames. ", e);
		}

		return arrayRecords;
	}

	//********** Executing MySQL Query and Returning Result Set: *********************************************//
	public  ResultSet ExecuteMySQLQueryReturnsResultSet(Connection con, String sqlQuery) throws SQLException 
	{		
		try{
			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sqlQuery);
			return rs;
		}catch(MySQLSyntaxErrorException m){
			logger.error(m.getMessage());
			return null;
		}
	}

	/**
	 * get mysqlConnection
	 * @param dburl
	 * @param dbuserName
	 * @param dbpassword
	 * @return
	 */
	public Connection getMySQLConnection(String dburl, String dbuserName, String dbpassword)  
	{
		Connection qaConnection = null;
		try
		{
			String dbClass = "com.mysql.jdbc.Driver";	
			Class.forName(dbClass);

			qaConnection = (Connection) DriverManager.getConnection (dburl,dbuserName,dbpassword);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Error occurred while creating sql connection. ", e);
		}
		return qaConnection;
	}

	/**
	 * get preprod db mysql connection
	 * @return
	 */
	public Connection getPreprodMysqlDBConnection() {
		
		Connection qaConnection = null;
		String dburl = "jdbc:mysql://magento-rds-master.preprod-mumbai-nyk.internal/nykaalive1";
		String dbuserName = "nykaalive";
		String dbpassword = "oh1ued3phi0uh8ooPh6";
		
		try
		{
			String dbClass = "com.mysql.jdbc.Driver";
			Class.forName(dbClass);

			qaConnection = (Connection) DriverManager.getConnection(dburl, dbuserName, dbpassword);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Error occurred while creating preprod sql connection. dbUrl: "+dburl + " username: "+dbuserName + " dbPassword: "+dbpassword , e);
		}
		return qaConnection;
	}

	/**
	 * this code will return the mysql connection for reporting
	 * @return
	 */
	public Connection mysqlConnectionForReporting()  
	{
		Connection qaConnection = null;
		try
		{
			String dbClass = "com.mysql.jdbc.Driver";	
			Class.forName(dbClass);

			/** Getting Values for dburl,dbUsername and dbPassword from configuration file */
			String dburl = "jdbc:mysql://13.235.186.44:3306/core";
			String dbuserName = "admin";
			String dbpassword = "Nykaa@123";

			qaConnection = (Connection) DriverManager.getConnection (dburl,dbuserName,dbpassword);
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Error occurred while creating reporting sql connection. ", e);
		}
		return qaConnection;
	}

}
