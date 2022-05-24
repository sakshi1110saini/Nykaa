package main.java.com.dnautomation.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relevantcodes.extentreports.LogStatus;
import main.java.com.dnautomation.exception.JsonValidationException;
import org.testng.Reporter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {
    //get response form input url or API
    public String sendGET(String url) throws Exception {
        URL obj = new URL(url);
        ReportUtil.test.log(LogStatus.INFO, "Hitting url : "+url);
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

    public JsonNode convertStringResponseToTree(String jsonString) throws JsonValidationException {
        JsonNode json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(jsonString);
            json = mapper.readTree(parser);
        } catch (Exception e) {
            throw new JsonValidationException("incomplete response received from api");
        }
        return json;
    }





}
