package main.java.com.dnautomation.util;

import java.util.Arrays;
import java.util.List;

public class Constant {


    public static final String HOME_DIR = System.getProperty("user.dir");
    public static final String ENV = "preprod";//System.getProperty("test_env");
    public static final String PLATFORM="platform";

    public static final String API_URL_CONFIG =HOME_DIR+"\\src\\main\\resources\\dnConfig.properties";//HOME_DIR+"/properties/dn.properties/dnconfig.properties";
    public static final String INVENTORY_URL =ENV+"_inventory_url";
    public static final String PARAM_SEPERATOR = "?";
    public static final String PARENT_JSON="generic.json";
    public static final List<String> SKIP_ERROR_TYPE = Arrays.asList("IMAGE", "VIDEO", "RECOMMENDATION_WIDGET");
    public static final String INVENTORY_JSON_PREFIX = "test_get_inventory_";
    public static final String INVENTORY_JSON_SUFIX = ".json";
    public static final String UNI_SCHEMA_JSON="schema.json";
    public static final List<String> SKIP_NODE_IN_CHILD_VALIDATION=Arrays.asList("personalization");
    public static final String URL_PARAMS=ENV+"_inventory_query_param";
    public static final List<String> SKIP_CHILDREN_FOR_WTYPE = Arrays.asList("IMAGE","VIDEO" ,"RECOMMENDATION_WIDGET");
    public static final List<String> SKIP_CHILDREN_FOR_WTYPE_PATH=Arrays.asList("widget_data.children");

    private static final String jenkinsJobName="Live_DN_API_InventorySchemaValidation";

    private static final String[][] keyArr = {{"widget_data.wid"},
            {"inventory_name"},
            {"parameters.callout","parameters.tag_text","parameters.title","parameters.description"}};
}
