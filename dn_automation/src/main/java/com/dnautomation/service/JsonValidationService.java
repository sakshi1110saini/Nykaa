package main.java.com.dnautomation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.*;
import com.relevantcodes.extentreports.LogStatus;
import main.java.com.dnautomation.exception.JsonValidationException;
import main.java.com.dnautomation.util.ReportUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public abstract class JsonValidationService {
    public abstract boolean isEnabled();
    public abstract boolean isValid(JsonNode jsonNode) throws JsonValidationException;

    protected String toString(JsonNode input){
        if(input!=null){
            String result = input.toString();
            return result.replace("\"","");
        }
        return null;
    }
    protected JsonSchema schemaLoader(String jsonPath) throws FileNotFoundException, URISyntaxException {
        JsonSchema schema = null;
        InputStream schemaStream = null;
        try {
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
            File file = new File(this.getClass().getClassLoader().getResource(jsonPath).toURI());
            schemaStream = new FileInputStream(file);
            schema = schemaFactory.getSchema(schemaStream);
        } finally {
            try {
                if (schemaStream != null)
                    schemaStream.close();
            } catch (IOException e) {
                throw new JsonSchemaException("error in parsing schema file with path : "+jsonPath);
            }
        }
        return schema;
    }

    protected void printErrorMessage(Set<ValidationMessage> validationResult, JsonNode jsonNode,List<String> skipError) throws JsonValidationException {
        Map<String, List<String>> errorTrackMap = new HashMap<>();

        for (ValidationMessage vm : validationResult) {
            boolean skipMessage = false;
            if(null!=skipError) {
                for (String error : skipError) {
                    if (vm.getMessage().contains(error)) {
                        skipMessage = true;
                        break;
                    }
                }
            }
            if(!skipMessage) {
                String[] validationMessageSplitIntoDataAndError = vm.getMessage().split(":");
                String dataAddress = validationMessageSplitIntoDataAndError[0];
                String errorMessage = validationMessageSplitIntoDataAndError[1];
                String[] dataAddressArray = dataAddress.split("\\.");

                String inventoryName = "Parent";
                if (null == jsonNode.get("result")) {
                    inventoryName = toString(jsonNode.get("inventory_name"));
                }

                if (errorTrackMap.get(inventoryName) == null) {
                    errorTrackMap.put(inventoryName, new ArrayList<>());
                }

                errorTrackMap.get(inventoryName).add(String.join("->", dataAddressArray) + ":" + errorMessage);
            }
        }
        Set<String> keys = errorTrackMap.keySet();

        for (String key : keys) {
            ReportUtil.test.log(LogStatus.FAIL, "Following issues in inventory : "+key);
            errorTrackMap.get(key).forEach(x ->  ReportUtil.test.log(LogStatus.FAIL, x));
        }
    }
}
