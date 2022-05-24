package main.java.schemavalidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.Connection;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import generic.utils.DBLib;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Test;
import org.testng.Reporter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class JsonValidatorCustom {
    /*private static String GET_URL = "https://nykaa-widgets.nykaa.com/inventory/data/json/";
    private static String queryParam="?page_type=men-test-native-lp&page_section=*&page_data=men-native";//System.getProperty("queryParam");*/
    //?page_type=app-homepage&page_section=*&page_data=default
    // main() method start


    public final static String HOME_DIR = System.getProperty("user.dir");
    @Test()
    public static void SchemaValidator() throws Exception {
        try {
            Reporter.log("Schema validation started");
            JsonValidatorCustom validator = new JsonValidatorCustom();
            // create instance of the ObjectMapper class

            PropertiesConfiguration propertiesMap = new PropertiesConfiguration();
            propertiesMap.load(HOME_DIR+"/dn.properties/dnConfig.properties");

            String env = System.getProperty("env");
           // env="prod";
            String url = propertiesMap.getProperty(env+"_inventory_url").toString();
            String queryParams = propertiesMap.getProperty(env+"_inventory_query_param").toString();
            String[] queryParamCases = queryParams.split(";");
            boolean mismatch = false;
            for(String queryParam : queryParamCases){
                String finalUrl = url+"?"+queryParam;
                Reporter.log(finalUrl);
                boolean mismatchLocal = validator.validatorCustom(finalUrl);
                mismatch=mismatch ? mismatch:mismatchLocal;
                if(!mismatchLocal){
                    Reporter.log("No mismatch found");
                }
            }
            Reporter.log("Schema validation ended");
            if (mismatch){
                throw new Exception("response schema mismatch");
            }
        }catch (Exception e){
          throw e;
        }

    }

    public boolean validatorCustom(String url) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // create an instance of the JsonSchemaFactory using version flag
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V201909 );

        // store the JSON data in InputStream
        InputStream schemaStream =null;
        boolean mismatch = false;
        try{

            File initialFile =new File(getClass().getClassLoader().getResource("schema.json").toURI());
            schemaStream = new FileInputStream(initialFile);

            String responseString =  sendGET(url);
            JsonNode json = objectMapper.readTree(new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8)));

            // get schema from the schemaStream and store it into JsonSchema
            JsonSchema schema = schemaFactory.getSchema(schemaStream);

            // create set of validation message and store result in it
            Set<ValidationMessage> validationResult = schema.validate( json );

            // show the validation errors
            if (validationResult.isEmpty()) {
                // show custom message if there is no validation error
                System.out.println( "There is no validation errors" );
            } else {
                // show all the validation error
                mismatch= printErrorMessage(validationResult,responseString,url);
            }
        }
        finally {
            try {
                schemaStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mismatch;
    }

    private boolean printErrorMessage(Set<ValidationMessage> validationResult,String responseString,String url) throws Exception {
            List<String> skipImageTypes = Arrays.asList("IMAGE","VIDEO" ,"RECOMMENDATION_WIDGET");
            //inputJson.available();
            Object obj = new JSONParser().parse(responseString);
            JSONObject jo = (JSONObject) obj;

            Map<String,List<String>> errorTrackMap = new HashMap<>();

            for(ValidationMessage vm : validationResult){
                String[] validationMessageSplitIntoDataAndError = vm.getMessage().split(":");
                String dataAddress = validationMessageSplitIntoDataAndError[0];
                String errorMessage = validationMessageSplitIntoDataAndError[1];
                String[] dataAddressArray = dataAddress.split("\\.");
                Integer indexInResult =  Integer.parseInt(dataAddressArray[1].split("\\[")[1].split("]")[0]);
                JSONObject parent = ((JSONObject)((JSONArray) jo.get("result")).get(indexInResult));
                String inventoryName = (String) parent.get("inventory_name");
               // System.out.println(inventoryName);
                if(null!=parent.get("personalization")){
                    continue;
                }
                JSONObject widgetData = (JSONObject) parent.get("widget_data");
                if(skipImageTypes.contains((String)widgetData.get("wtype"))){
                    if(dataAddressArray[2].equals("widget_data") && dataAddressArray[3].equals("children")){
                        continue;
                    }
                }
                if(errorTrackMap.get(inventoryName)==null){
                    errorTrackMap.put(inventoryName,new ArrayList<>());
                }
                errorTrackMap.get(inventoryName).add(String.join("->",dataAddressArray) +":"+ errorMessage);

            }
            Set<String> keys = errorTrackMap.keySet();

            for (String key : keys){
                Reporter.log("Following issues are there in Inventory : "+key);
                errorTrackMap.get(key).forEach(x-> Reporter.log(x));
            }
            pushDataToDB(errorTrackMap,url);
            if(!errorTrackMap.isEmpty()){
               return true;
            }
        return false;
    }

    private void pushDataToDB(Map<String, List<String>> errorTrackMap,String url) {
        DBLib dbLib = new DBLib();
        Connection con = dbLib.mysqlConnectionForReporting();
        try {
            String query = "insert into inventory_schema_error_log (URL,inventory_name,column_path,error_message) values " ;
            int counter=1;
            for(String key : errorTrackMap.keySet()){
                int counterMsg = 1;
                for(String pathWithMsg : errorTrackMap.get(key)) {
                  //  System.out.println("debugger");
                    String path = pathWithMsg.split(":")[0];
                    String msg = pathWithMsg.split(":")[1];
                    query = query + "('"+url+"','" + key.trim() + "','" +path.trim()+"','"+msg.trim()+"')";
                    if(counter>=errorTrackMap.keySet().size() && counterMsg>=errorTrackMap.get(key).size()){
                        query=query+";";
                    }else{
                        query=query+",";
                    }
                    counterMsg++;
                }
                counter++;


            }
            //System.out.println(query);v
            dbLib.executeUpdateInsertQuery(con, query);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String sendGET(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        StringBuffer response = new StringBuffer();
        InputStream input = null;
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            input = con.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } else {
           throw new Exception("HTTP status not OK");
        }
        if(response==null || response.toString().trim().equals("")){
            throw new Exception("response is null");
        }
        return response.toString();
    }
}
