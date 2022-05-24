package main.java.schemavalidator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.jdbc.Connection;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import generic.utils.DBLib;
import main.java.com.dnautomation.exception.JsonValidationException;
import main.java.com.dnautomation.util.Constant;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class JsonValidatorCustomDetailed {

    public final static String HOME_DIR = System.getProperty("user.dir");
    private static Instant startTime;
    private static Instant endTime;
    private static String jenkinsJobNamePrefix ="Live_";
    private static String jenkinsJobName="DN_API_InventorySchemaValidation";
    private static Map<String,String> paramValue = new HashMap<>();
    private static List<String> keys = new ArrayList<>();
    private static String env = System.getProperty("env");
    private List<String> skipImageTypes = Arrays.asList("IMAGE","VIDEO" ,"RECOMMENDATION_WIDGET","RECOMMENDATION_WIDGET_V2");

    static Logger logger = LogManager.getLogger(JsonValidatorCustomDetailed.class.getName());
    @Test()
    //entry point for schema validation  
    public static void SchemaValidator() throws Exception {
        try {
        	
        	Reporter.log(" Starting Schema validation --- ");
            String platform = System.getProperty(Constant.PLATFORM)+"_";
            JsonValidatorCustomDetailed validator = new JsonValidatorCustomDetailed();
            startTime = Instant.now();
            
            PropertiesConfiguration propertiesMap = new PropertiesConfiguration();
            propertiesMap.load(HOME_DIR+"/dn.properties/dnConfig.properties");

            String url = propertiesMap.getProperty(platform+env+"_inventory_url").toString();//beauty_preprod
            String queryParams = propertiesMap.getProperty(platform+env+"_inventory_query_param").toString();

            Reporter.log("Schema validation started for env: "+env +  " and query param - "+queryParams);
            
            int count = (int)queryParams.chars().filter(ch -> ch == ';').count();
            String[] queryParamCases = new String[count+1];

            if(count>0) {
                queryParamCases = queryParams.split(";");
            }else{
                queryParamCases[0]=queryParams;
            }

            boolean mismatch = false;
            for(String queryParam : queryParamCases){
                paramValue.clear();
                keys.clear();
                String[] queryParamKeyValue = queryParam.split("&");
                for(String kv : queryParamKeyValue) {
                    String[] queryParamValue = kv.split("=");
                    paramValue.put(queryParamValue[0],queryParamValue[1]);
                    keys.add(queryParamValue[0]);
                }
                String finalUrl = url+"?"+queryParam;
                //Reporter.log(finalUrl);
                System.out.println("url : "+finalUrl);
                boolean mismatchLocal = false;
                try {
                    mismatchLocal = validator.validateResponseSchema(finalUrl);
                }catch(Exception e){
                    Reporter.log(e.getMessage());
                    mismatch=true;
                    break;
                }
                mismatch=mismatch ? mismatch:mismatchLocal;
                if(!mismatchLocal){
                    Reporter.log("No mismatch found");
                }
            }
            endTime =Instant.now();

            Duration executionDuration = Duration.between(startTime, endTime);
            String executionDurationMili = Long.toString(executionDuration.toMillis());
            float duration = Float.parseFloat(executionDurationMili)/1000;
            int successFlag = 1;
            if(mismatch){
                successFlag=0;
            }
          if (env.equals("prod")){
                String query = "insert into live_monitoring_stats (jenkins_job_name, execution_duration_sec, result) values "
                        + " ('" + jenkinsJobNamePrefix+platform+jenkinsJobName + "',"
                        + duration + ",'" + successFlag + "') ";
                if (!validator.insertDataToDB(query)) {
                    throw new Exception("error in pushing data to db");
                }
                Reporter.log("Schema validation ended");
            }
            if (mismatch){
                logger.error("Schema validation failed.");
                throw new JsonValidationException("Schema validation failed.");
            }
        }catch (Exception e){
        	e.printStackTrace();
            throw e;
        }

    }

    //validate schema with response
    public boolean validateResponseSchema(String url) throws Exception {
        // create an instance of the JsonSchemaFactory using version flag
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V201909 );

        // store the JSON data in InputStream
        InputStream schemaStream =null;
        boolean mismatch = false;
        try{
            String responseString =  sendGET(url);

            JsonNode json = null;

            try{
                json = stringToJsonTree(responseString);
            }catch(Exception e) {
                return false;
                /*String query = "insert into inventory_schema_error_log (incomplete_response,URL) value (true,'" + url + "')";
                if (!insertDataToDB(query)) {
                    throw new Exception("incomplete response and error in pushing data to db");
                }
                throw new Exception(e.getMessage());*/
            }

         //  insertApiResponseToDB(json);

            //Reporter.log("parent verification start");
            //verifying parent (generic.json)
            File parentFile =new File(getClass().getClassLoader().getResource("generic.json").toURI());
            schemaStream = new FileInputStream(parentFile);
            JsonSchema parentSchema = schemaFactory.getSchema(schemaStream);
            //set with error messages
            Set<ValidationMessage> parentvalidationResult = parentSchema.validate(json);

            if (parentvalidationResult.isEmpty()) {
                //Reporter.log( "There is no validation errors in parent" );
            } else {
                // show all the validation error
                mismatch= printErrorMessageForResponse(parentvalidationResult,url,json);
            }
            //Reporter.log("Parent verification end");

            //Reporter.log("Child Verification start");
            int nodeCount = json.get("result").size();
            if(nodeCount==0){
                throw new Exception("result is empty");
            }

            //verifying all the children/inventoryData one by one
            for(int i=0;i<nodeCount;i++){
                JsonNode jsonNode = json.get("result").get(i);

                if(null!=jsonNode.get("personalization")){
                    continue;
                }
                String fileName = "test_get_inventory_"+ nodeValueToString(jsonNode.get("widget_data").get("wtype").toString()).toLowerCase()+".json";
                File initialFile = null;
                try{
                    initialFile = new File(getClass().getClassLoader().getResource(fileName).toURI());
                }catch( Exception no){
                    initialFile = new File(getClass().getClassLoader().getResource("schema.json").toURI());
                }
                try {
                    schemaStream = new FileInputStream(initialFile);
                }catch(Exception e){
                    throw new Exception("error in reading schema.json");
                }
                JsonSchema schema = schemaFactory.getSchema(schemaStream);
                Set<ValidationMessage> validationResult = schema .validate(jsonNode);

                if (!validationResult.isEmpty()) {
                    mismatch = printErrorMessageForResponse(validationResult,url,jsonNode) || mismatch ? true:false;
                }
            }
        }
        finally {
            try {
                if(schemaStream!=null)
                    schemaStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mismatch;
    }

    private boolean insertApiResponseToDB(JsonNode jsonTree) throws Exception {
        try {

            String responseStringForDb =  jsonTree.toString();//designJsonForDb(jsonTree);

            String selectQuery = "select id from inventory_api_response where ";
            List<String> paramValues = new ArrayList<>();
            for (String keyValue : keys) {
                paramValues.add(paramValue.get(keyValue));

            }

            int id = 0;
            String columnName = keys.stream().collect(Collectors.joining(","));
            String valueName = paramValues.stream().collect(Collectors.joining("\',\'"));
            String url = "nykaa.com/sp/"+paramValue.get("page_type");
            String preUrl="https://";

            if(env.equals("preprod")){
                preUrl=preUrl+env+".";
            }
            url=preUrl+url;

            String query = "insert into inventory_api_response (id,response,url,env," + columnName + ")" +
                    " values (" + id + ",'" + responseStringForDb + "','" + url +"','"+env+"','" + valueName + "') " ;
                   //+ "ON DUPLICATE KEY UPDATE response='" + responseStringForDb + "'";
            insertDataToDB(query);
            return true;
        }catch(Exception e){
            throw new Exception("error in creating node with error : "+e.getMessage());
        }
    }

    private void addChildrenNodes(JsonNode parent, ArrayNode newParentNode, String[] dataKeys) throws Exception{

        ObjectMapper mapper = new ObjectMapper();
        int size = parent.size();
        for (int j = 0; j < size; j++) {
            ObjectNode childLevel1 = mapper.createObjectNode();
            for (String path : dataKeys) {
                JsonNode js = parent.get(j);
                int i = 0;
                String[] nodes = new String[]{path};
                if (path.indexOf(".") != -1) {
                    nodes = path.split("\\.");
                    for (i = 0; i < nodes.length - 1; i++) {
                        if(null==js.get(nodes[i]))
                            break;
                        js = js.get(nodes[i]);
                    }
                }
                if(null!=js.get(nodes[i]))
                    childLevel1.put(nodes[i], nodeValueToString(js.get(nodes[i]).toString()));
            }
            newParentNode.add(childLevel1);
        }
    }

    //print all the error messages
    private boolean printErrorMessageForResponse(Set<ValidationMessage> validationResult, String url, JsonNode jsonNode) throws Exception {
        //Condition to skip wtypes


        Map<String,List<String>> errorTrackMap = new HashMap<>();

        for(ValidationMessage vm : validationResult){
            String[] validationMessageSplitIntoDataAndError = vm.getMessage().split(":");
            String dataAddress = validationMessageSplitIntoDataAndError[0];
            String errorMessage = validationMessageSplitIntoDataAndError[1];
            String[] dataAddressArray = dataAddress.split("\\.");

            if(null==jsonNode.get("result")){
                String inventoryName =  nodeValueToString(jsonNode.get("inventory_name").toString());

                if(skipImageTypes.contains(nodeValueToString(jsonNode.get("widget_data").get("wtype").toString()))){
                    if(dataAddressArray[1].equals("widget_data") && dataAddressArray[2].contains("children")){
                        continue;
                    }
                }
                if(errorTrackMap.get(inventoryName)==null){
                    errorTrackMap.put(inventoryName,new ArrayList<>());
                }
                errorTrackMap.get(inventoryName).add(String.join("->",dataAddressArray) +":"+ errorMessage);

            }else{
                if(errorTrackMap.get("Parent")==null){
                    errorTrackMap.put("Parent",new ArrayList<>());
                }
                errorTrackMap.get("Parent").add(String.join("->",dataAddressArray) +":"+ errorMessage);
            }

        }
        Set<String> keys = errorTrackMap.keySet();

        for (String key : keys){
            Reporter.log("Following issues are there in Inventory : "+key);
            errorTrackMap.get(key).forEach(x-> Reporter.log(x));
        }
       // insertErrorMessageToDb(errorTrackMap,url);
        if(!errorTrackMap.isEmpty()){
            return true;
        }
        return false;
    }

    private void insertErrorMessageToDb(Map<String, List<String>> errorTrackMap, String url) {

        try {
            String query = "insert into inventory_schema_error_log (URL,inventory_name,column_path,error_message) values " ;
            int counter=1;
            for(String key : errorTrackMap.keySet()){
                int counterMsg = 1;
                for(String pathWithMsg : errorTrackMap.get(key)) {
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
            if(counter==1){
                query="insert into inventory_schema_error_log (URL) values ('"+url+"')";
            }
            if(!insertDataToDB(query)){
                throw new Exception("error in push data to db");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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


    //get response form input url or API
    private String sendGET(String url) throws Exception {
        URL obj = new URL(url);
        Reporter.log(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        Reporter.log("GET Response Code :: " + responseCode);
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

    private String fetchDataForSingleColumn(String query) throws Exception {
        Connection con=null;
        String response = null;
        try{
            DBLib dbLib = new DBLib();
            con = dbLib.mysqlConnectionForReporting();
            response = dbLib.ExecuteMySQLQueryReturns1DArray(con, query)[0];
        }catch(Exception e){
            throw new Exception("Error in fetching data from db");
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    private String[][] fetchDataForMultiColumn(String query){
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

    private String nodeValueToString(String input){
        if(input!=null)
            return input.replace("\"","");
        return input;
    }

    private JsonNode stringToJsonTree(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(jsonString);
        JsonNode json = null;
        try {
            json= mapper.readTree(parser);
        }catch(Exception e){
            throw new Exception("incomplete response received from api");
        }
        return json;
    }
}
