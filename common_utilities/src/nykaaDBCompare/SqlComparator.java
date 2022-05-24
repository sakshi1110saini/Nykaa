package nykaaDBCompare;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.json.simple.JSONObject;

public class SqlComparator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			System.out.println();
			System.out.println("***************  START  ****************************************");
			System.out.println();

			String env = System.getProperty("env");
			if(env!=null) {
				System.out.println("******** Checking Target ENV "+env + " *************");
				compareEnv(env);
			}else {
				System.out.println("ENV NOT SUPPLIED ");
			}


			System.out.println();
			System.out.println("*******************  END  ************************************");
			System.out.println();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static JSONObject getJsonFromDB(String userName, String password, String hostUrl ) {

		JSONObject obj1 = new JSONObject();
		try
		{
			String databaseName = "nykaalive1";
			String mySQLPort = "3306";
			Class.forName("com.mysql.jdbc.Driver");

			Connection conn = DriverManager.getConnection("jdbc:mysql://" + hostUrl
					+ ":" + mySQLPort, userName, password);
			ResultSet resultSet = conn.getMetaData().getCatalogs();
			ResultSet resultSet1 = conn.getMetaData().getCatalogs();

			String[] types = { "TABLE" };
			resultSet = conn.getMetaData()
					.getTables(databaseName, null, "%", types);
			String tableName = "";

			while (resultSet.next()) {
				tableName = resultSet.getString(3);
				ResultSetMetaData  metaData = resultSet.getMetaData();
				DatabaseMetaData meta = conn.getMetaData();
				resultSet1 = meta.getColumns(databaseName, null, tableName, "%");

				List<String> list = new ArrayList<>();
				while (resultSet1.next()) {
					list.add(resultSet1.getString(4));
				}
				obj1.put(tableName, list);
			}

		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error while connecting : "+userName + " / " +password + " - "+hostUrl);
			e.printStackTrace();
		}

		return obj1;
		//resultSet1.close();
	}

	public static void compareEnv(String env) {

		try {

			Ini environment= new Ini();
			environment.load(new File(System.getProperty("user.dir")+"/properties/config.ini"));

			Section environmentSection = environment.get(env);
			String userName = environmentSection.get("userName");
			String password = environmentSection.get("password");
			String hostUrl = environmentSection.get("hostUrl");
			JSONObject targetDB = getJsonFromDB(userName, password, hostUrl);

			environmentSection = environment.get("preprod");
			userName = environmentSection.get("userName");
			password = environmentSection.get("password");
			hostUrl = environmentSection.get("hostUrl");
			JSONObject preprod = getJsonFromDB(userName, password, hostUrl);

			//get all keys to iterate 
			Iterator<String> preprodIterator = preprod.keySet().iterator();

			while(preprodIterator.hasNext()){

				String preprodTable = preprodIterator.next();

				//check if this table is in target json object
				Object targetTable = targetDB.get(preprodTable);

				//first check if table is present 
				if(targetTable == null){
					System.out.println("this table : "+preprodTable + " doesn't exist in target db .. ");

				}else{

					//check the schema 
					Object preprodSchema = preprod.get(preprodTable);
					if(preprodSchema.equals(targetTable)) {
						//						System.out.println("All OK ...");
					} else{
						String targetCol = targetTable.toString();
						String preprodCol = preprodSchema.toString();
						targetCol = targetCol.replace("[", "");
						targetCol = targetCol.replace("]", "");

						preprodCol = preprodCol.replace("[", "");
						preprodCol = preprodCol.replace("]", "");

						String[] preCol = preprodCol.split(",");
						String[] tarCol = targetCol.split(",");

						Collection<String> col1 = Arrays.asList(preCol);
						Collection<String> col2 = Arrays.asList(tarCol);

						Collection<String> similar = new HashSet<String>(col1);
						Collection<String> different = new HashSet<String>();

						different.addAll(col1);
						different.addAll(col2);

						similar.retainAll(col2);
						different.removeAll(similar);

						System.out.println(different+" column is not present in "+preprodTable+" table.");				}
				}

			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}