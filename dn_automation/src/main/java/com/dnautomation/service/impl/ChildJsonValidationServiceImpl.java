package main.java.com.dnautomation.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import main.java.com.dnautomation.exception.JsonValidationException;
import main.java.com.dnautomation.service.JsonValidationService;
import main.java.com.dnautomation.util.Constant;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Set;

public class ChildJsonValidationServiceImpl extends JsonValidationService {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isValid(JsonNode jsonNode) throws JsonValidationException {
        try {
            int nodeCount = jsonNode.get("result").size();
            if(nodeCount==0){
                throw new JsonValidationException("result is empty");
            }
            for (int i = 0; i < nodeCount; i++) {
                JsonNode childNode = jsonNode.get("result").get(i);
                boolean skipNode = false;

                for(String skipKeys : Constant.SKIP_NODE_IN_CHILD_VALIDATION){
                    if (null != childNode.get(skipKeys)) {
                        skipNode = true;
                    }
                }
                if(!skipNode) {
                    JsonSchema schema = null;
                    String fileName = Constant.INVENTORY_JSON_PREFIX + toString(childNode.get("widget_data").get("wtype")).toLowerCase() + Constant.INVENTORY_JSON_SUFIX;
                    try {
                        schema = this.schemaLoader(fileName);
                    } catch (Exception e) {
                        schema = this.schemaLoader(Constant.UNI_SCHEMA_JSON);
                    }

                    Set<ValidationMessage> validationResult = schema.validate(childNode);

                    if (validationResult.isEmpty()) {
                        return true;
                    }
                    if (Constant.SKIP_CHILDREN_FOR_WTYPE.contains(toString(childNode.get("widget_data").get("wtype")))) {
                        printErrorMessage(validationResult, childNode, Constant.SKIP_CHILDREN_FOR_WTYPE_PATH);
                    }else{
                        printErrorMessage(validationResult, childNode, null);
                    }
                }
            }
        } catch (URISyntaxException | FileNotFoundException fe){
            throw new JsonValidationException("Error in validating Children");
        }
        return false;
    }
}
