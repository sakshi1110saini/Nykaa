package newman.reports;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Test;

import generic.utils.ExecuteCommands;
import generic.utils.HttpRequestHandler;


public class TestPostmanCollections {

	static String results;

	public static void main(String[] args) {

		try
		{
			String logs = System.getProperty("user.dir")+"/logs";
			results = System.getProperty("user.dir")+"/results";

			try{new File(logs).mkdirs();}catch (Exception e) {}
			try{new File(results).mkdirs();}catch (Exception e) {}

			String AUTOMATION_HOME=System.getProperty("user.dir");
			//PropertyConfigurator.configure(AUTOMATION_HOME.concat("/properties/general.properties/log4j.properties"));

			Object environment="";
			
			try {
				environment = getEnvironment(args[0]);
			}catch (ArrayIndexOutOfBoundsException e) {
				environment ="preprod";
				// TODO: handle exception
			}
			
			
			for(Entry<Object, PostmanCollectionsClass> entry : getCollectionMapping().entrySet()) {

				Object collectionId = entry.getKey();
				Object command = getCommand(collectionId, environment);

				String [] commands;

				if(System.getProperty("os.name").toLowerCase().contains("window")){
					commands = new String[]{"cmd", "/c", String.valueOf(command)};
				}else {
					commands = new String[] {"/bin/sh", "-c", String.valueOf(command)};
				}

				Object output = new ExecuteCommands().ExecuteCommand_ReturnsOutput(commands);
				System.out.println("Received O/P - "+output);
			}
			/**create index.html consolidated reports with links to individual html */
			//FormFinalHtml.test();

		}catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 * get a map containg collection id in a map
	 * @return
	 */
	public static ConcurrentHashMap<Object,PostmanCollectionsClass> getCollectionMapping() {

		/** store collection in an object map */
		ConcurrentHashMap<Object, PostmanCollectionsClass> collectionsMap = new ConcurrentHashMap<>();

		try
		{
			/** set auth header */
			HashMap<String, String> headers = new HashMap<>();
			headers.put("X-Api-Key", "b2a4b63d108044fbb37e76b6744851e4");

			JSONParser jp = new JSONParser();

			/** get collections */
			String strCollections = new HttpRequestHandler().sendGetRequest("https://api.getpostman.com/collections", headers);
			JSONObject jsonObjectCollections = (JSONObject)jp.parse(strCollections);
			JSONArray collections = (JSONArray) jsonObjectCollections.get("collections");

			for(int i=0; i<collections.size(); i++) {

				JSONObject jsonObject = (JSONObject)collections.get(i);

				if(jsonObject.get("name").toString().trim().toLowerCase().startsWith("test")) {

					Object collectionId = jsonObject.get("id");

					PostmanCollectionsClass postManCollectionObject = new PostmanCollectionsClass();
					postManCollectionObject.setCollectionId(collectionId);
					collectionsMap.put(collectionId, postManCollectionObject);

					System.out.println(jsonObject.get("id"));

				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return collectionsMap;
	}

	/**
	 * get environment 
	 * @return
	 */
	public static Object getEnvironment(String env) {

		Object environment = null;
		try {

			String postman_environment = env; //System.getProperty("postman_environment");
			if(postman_environment == null) {
				postman_environment = "preprod";
			}

			System.out.println("running on : "+postman_environment);

			/** set auth header */
			HashMap<String, String> headers = new HashMap<>();
			headers.put("X-Api-Key", "b2a4b63d108044fbb37e76b6744851e4");

			JSONParser jp = new JSONParser();

			/** get env */
			String strEnvironments = new HttpRequestHandler().sendGetRequest("https://api.getpostman.com/environments", headers);
			JSONObject jsonObjectEnvironments = (JSONObject)jp.parse(strEnvironments);
			JSONArray environments = (JSONArray) jsonObjectEnvironments.get("environments");

			for(int i=0; i<environments.size(); i++) {

				JSONObject jsonObject = (JSONObject)environments.get(i);

				if(jsonObject.get("name").toString().equalsIgnoreCase(postman_environment)) {

					environment = jsonObject.get("id");
					System.out.println("got environment - "+environment);

					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return environment;
	}


	/**logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+ " : Unable to get list of files ",e);
	 * get command to run collection
	 * 
	 * @param collectionId
	 * @param environment
	 * @return
	 */
	public static Object getCommand(Object collectionId, Object environment) {

		// /home/linuxbrew/.linuxbrew/bin/

		String command = "newman run \"https://api.getpostman.com/collections/"+collectionId+"?apikey=b2a4b63d108044fbb37e76b6744851e4\" "
				+ " --environment  \"https://api.getpostman.com/environments/"+environment+"?apikey=b2a4b63d108044fbb37e76b6744851e4\" "
				+ " --reporters junit,htmlextra --reporter-junit-export "+ results + " --reporter-htmlextra-export "+ results;

		System.out.println("returning command - "+command);
		return command;
	}
	
	@Test
	public static void generateFinalReport() {

		try
		{
			String logs = System.getProperty("user.dir")+"/logs";
			results = System.getProperty("user.dir")+"/results";

			try{new File(logs).mkdirs();}catch (Exception e) {}
			try{new File(results).mkdirs();}catch (Exception e) {}

			String AUTOMATION_HOME=System.getProperty("user.dir");
			//PropertyConfigurator.configure(AUTOMATION_HOME.concat("/properties/general.properties/log4j.properties"));

			Object environment="";
			
			try {
				environment = System.getProperty("test_env");
			}catch (NullPointerException e) {
				environment ="preprod";
				// TODO: handle exception
			}
			if(environment==null) {
				environment ="preprod";
			}
		
			for(Entry<Object, PostmanCollectionsClass> entry : getCollectionMapping().entrySet()) {

				Object collectionId = entry.getKey();
				Object command = getCommand(collectionId, environment);

				String [] commands;

				if(System.getProperty("os.name").toLowerCase().contains("window")){
					commands = new String[]{"cmd", "/c", String.valueOf(command)};
				}else {
					commands = new String[] {"/bin/sh", "-c", String.valueOf(command)};
				}

				Object output = new ExecuteCommands().ExecuteCommand_ReturnsOutput(commands);
				System.out.println("Received O/P - "+output);
			}
			/**create index.html consolidated reports with links to individual html */
			FormFinalHtml.test();

		}catch (Exception e) {
			e.printStackTrace();
		}

	}


}
