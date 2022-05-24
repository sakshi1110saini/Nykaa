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

public class ParentJsonValidationServiceImpl extends JsonValidationService {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isValid(JsonNode jsonNode) throws JsonValidationException {
        try {
            JsonSchema parentSchema = schemaLoader(Constant.PARENT_JSON);
            Set<ValidationMessage> parentvalidationResult = parentSchema.validate(jsonNode);
            if (parentvalidationResult.isEmpty()) {
                return true;
            }
            printErrorMessage(parentvalidationResult, jsonNode,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
