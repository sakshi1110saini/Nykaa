package main.java.com.dnautomation.runner;


import com.fasterxml.jackson.databind.JsonNode;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;
import main.java.com.dnautomation.service.JsonValidationService;
import main.java.com.dnautomation.service.impl.ChildJsonValidationServiceImpl;
import main.java.com.dnautomation.service.impl.ParentJsonValidationServiceImpl;
import main.java.com.dnautomation.util.Constant;
import main.java.com.dnautomation.util.PropertyLoader;
import main.java.com.dnautomation.util.ReportUtil;
import main.java.com.dnautomation.util.Util;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonValidationRunner {

    private JsonValidationService jsonValidationService;

    @BeforeClass
    public static void startTest()
    {
        ReportUtil.report = new ExtentReports(System.getProperty("user.dir")+"\\ExtentReportResults.html");
        System.out.println("ussr_dir "+System.getProperty("user.dir"));
        ReportUtil.test = ReportUtil.report.startTest("JsonSchemaValidation");
    }

    @AfterClass
    public static void endTest()
    {
        ReportUtil.report.endTest(ReportUtil.test);
        ReportUtil.report.flush();
    }

   @Test()
    public void jsonValidationRunner() throws Exception{
        Util util = new Util();
        boolean validationPassed = true;
        try {
            Instant startTime = Instant.now();
            Reporter.log("Schema validation started");

            List<String> urlList = prepareUrl();
            for(String url : urlList) {
                String apiResponse = util.sendGET(url);
                JsonNode apiResponseTree = util.convertStringResponseToTree(apiResponse);

                //validate response (call implementation)
                jsonValidationService = new ParentJsonValidationServiceImpl();
                validationPassed = jsonValidationService.isValid(apiResponseTree);

                jsonValidationService = new ChildJsonValidationServiceImpl();
                validationPassed = jsonValidationService.isValid(apiResponseTree);

                Instant endTime = Instant.now();

                Duration executionDuration = Duration.between(startTime, endTime);
                String executionDurationMili = Long.toString(executionDuration.toMillis());
                float duration = Float.parseFloat(executionDurationMili) / 1000;
                if(validationPassed){
                    ReportUtil.test.log(LogStatus.PASS,"No Mismatch found");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(!validationPassed){
            throw new Exception("validation failed");
        }
    }


    private List<String> prepareUrl() throws IOException {
        String platform = System.getProperty(Constant.PLATFORM);
        Map<String, String> propertiesMap = PropertyLoader.getPropertiesData(Constant.API_URL_CONFIG);
        String url = propertiesMap.get(platform+"_"+Constant.INVENTORY_URL);
        String queryParams = propertiesMap.get(platform+"_"+Constant.URL_PARAMS);
        String[] queryParamsArr = queryParams.split(";");
        List<String> result = new ArrayList<>();
        for(String queryParam : queryParamsArr){
            result.add(url +Constant.PARAM_SEPERATOR + queryParam);
        }
        return result;
    }



}
