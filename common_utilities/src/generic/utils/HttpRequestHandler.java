/**
 * Last Changes Done on Jan 27, 2015 12:43:12 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */
package generic.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;


public class HttpRequestHandler 
{

	Logger logger = LogManager.getLogger(HttpRequestHandler.class.getName()); 

	private Object UNIQ_EXECUTION_ID = "";

	public HttpRequestHandler() {
	}

	public HttpRequestHandler(Object UNIQ_EXECUTION_ID) {
		this.UNIQ_EXECUTION_ID = UNIQ_EXECUTION_ID;
	}

	@SuppressWarnings("finally")
	public  String sendGetRequest(String ServerURL) 
	{
		// It may be more appropriate to use FileEntity class in this particular
		// instance but we are using a more generic InputStreamEntity to demonstrate
		// the capability to stream out data from any arbitrary source
		//
		// FileEntity entity = new FileEntity(file, "binary/octet-stream");


		// add request header
		//GetRequest.addHeader("User-Agent", USER_AGENT);

		ServerURL = ServerURL.replace("{", "");
		ServerURL = ServerURL.replace("}", "");
		ServerURL = ServerURL.trim();

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(ServerURL.isEmpty() || ServerURL.equalsIgnoreCase(""))
			{	
				logger.info(UNIQ_EXECUTION_ID+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+ServerURL);
			}
			else
			{
				httpclient = HttpClients	.createDefault();

				ServerURL = ServerURL.replace("%%", "").trim();
				HttpGet GetRequest = new HttpGet(ServerURL);
				try{
					response = httpclient.execute(GetRequest);
				}catch(HttpHostConnectException h){
					logger.error(UNIQ_EXECUTION_ID+" : Couldn't connect to host: "+ServerURL);
				}

				BufferedReader rd = null;
				try{
					logger.info(response.getStatusLine());
					rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(UNIQ_EXECUTION_ID+" : No RESPONSE RECEIVED.");
				}

				if(rd != null)
				{
					String line = "";
					while ((line = rd.readLine()) != null)
					{
						result.append(line);
					}

					logger.info(UNIQ_EXECUTION_ID + " - response from : "+ ServerURL + "  ==> "+result.toString());
				}
			}
		} 
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception occurred while getting response from url: "+ServerURL, e);
		}
		finally 
		{

			try{
				response.close();
				httpclient.close();
			}
			catch(NullPointerException n)
			{ logger.info(UNIQ_EXECUTION_ID+" : null pointer exception handled. ");}
			catch(Exception e){
				logger.error(UNIQ_EXECUTION_ID+" : Exception occurred while closing channel. ",e);
			}


			return result.toString();
		}
	}



	/** Send Get Request with custom headers
	 * 
	 * @param serverURL
	 * @param headers
	 * @return
	 */
	public  String sendGetRequest(String serverURL, HashMap<String, String> headers)  
	{
		serverURL = serverURL.replace("{", "");
		serverURL = serverURL.replace("}", "");
		serverURL = serverURL.trim();

		logger.info(UNIQ_EXECUTION_ID+" : Received URL: "+serverURL);

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(serverURL.isEmpty() || serverURL.equalsIgnoreCase(""))
			{	
				logger.info(UNIQ_EXECUTION_ID+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+serverURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();
				HttpGet GetRequest = new HttpGet(serverURL);

				logger.info(UNIQ_EXECUTION_ID+" : Adding custom headers... ");

				//Adding add custom headers in request
				for(Entry<String, String> map : headers.entrySet())
				{
					String name = map.getKey().trim();
					String value = map.getValue().trim();
					logger.error(name +" : "+value);
					GetRequest.addHeader(name, value);
				}

				response = httpclient.execute(GetRequest);

				logger.info(UNIQ_EXECUTION_ID+" : --------------------------RESPONSE ----------------------------");
				logger.debug(response.getStatusLine());

				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null)
				{
					result.append(line);
				}

				logger.info(result.toString());

				response.close();
				httpclient.close();
			}
		} 
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception occurred while getting response from url: "+serverURL, e);
		}
		//		finally 
		//		{
		return result.toString();
		//		}
	}

	/** Send Get Request with custom headers, return status code.
	 * 
	 * @param serverURL
	 * @param headers
	 * @return
	 */
	public  int getStatusCodeOfGetRequest(String serverURL, HashMap<String, String> headers)  
	{
		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		int statusCode = 0;
		try 
		{
			if(serverURL.isEmpty() || serverURL.equalsIgnoreCase(""))
			{	
				logger.error(UNIQ_EXECUTION_ID+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+serverURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();
				HttpGet GetRequest = new HttpGet(serverURL);

				/** Adding add custom headers in request */
				if(headers != null)
				{
					logger.info(UNIQ_EXECUTION_ID+" : Adding custom headers... ");
					for(Entry<String, String> map : headers.entrySet())
					{
						String name = map.getKey().trim();
						String value = map.getValue().trim();
						GetRequest.addHeader(name, value);
					}
				}

				response = httpclient.execute(GetRequest);
				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
				statusCode = response.getStatusLine().getStatusCode();

				String line = "";
				while ((line = rd.readLine()) != null)
				{
					result.append(line);
				}

				response.close();
				httpclient.close();

				logger.info(UNIQ_EXECUTION_ID + " supplied request - "+serverURL + " and  status code: " +statusCode + " and received response - "+result.toString());
			}
		} 
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : "+e+" occurred while getting response from url: "+serverURL, e);
		}
		return statusCode;
	}

	/** send get request with timeout param 
	 * 
	 * @param url
	 * @return
	 */
	public HashMap<Object, Object> sendGetRequest_GetResponseAsMap(String url, int timeout, HashMap<String, String> headers) {

		HashMap<Object, Object> responseMap = new HashMap<>();
		try
		{
			HttpResponse httpResponse = sendGetRequest(url, timeout, headers);

			if(httpResponse != null) {
				responseMap.put("response", EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
				responseMap.put("statuscode", httpResponse.getStatusLine().getStatusCode());
			}

		}catch (Exception e) {
			logger.error(UNIQ_EXECUTION_ID+" : Exception while sending get request - "+e.getMessage(), e);
		}

		return responseMap;
	}

	/** send GET request and receive json response.
	 * 
	 * @param url
	 * @param header
	 * @return
	 */
	public org.json.simple.JSONObject getRequest(String url, HashMap<String, String> header){

		org.json.simple.JSONObject jsonResultObject = new org.json.simple.JSONObject();

		try{
			HttpClient httpclient=HttpClientBuilder.create().build();
			HttpEntity responseEntity;
			String responseResult;
			HttpGet httpget=new HttpGet(url);

			if(header != null) {				
				for(Entry<String, String> en : header.entrySet()) {
					httpget.addHeader(en.getKey(), en.getValue());
				}
			}

			HttpResponse response=httpclient.execute(httpget);
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity);

			JSONParser jp = new JSONParser();
			jsonResultObject = (org.json.simple.JSONObject) jp.parse(responseResult);

			logger.info(UNIQ_EXECUTION_ID + " - request: "+url + " response: "+jsonResultObject);

		}catch(Exception e){
			logger.error(UNIQ_EXECUTION_ID + " - " +e.getMessage() + " with request: "+url, e);
		}

		return jsonResultObject; 
	}


	/**
	 * send get request
	 * @param apiUrl
	 * @param timeoutMilliSecond
	 * @return
	 */
	public HttpResponse sendGetRequest(String apiUrl, int timeoutMilliSecond, HashMap<String, String> headers) {
		HttpResponse response=null;

		try {
			if(timeoutMilliSecond == 0 ) {
				timeoutMilliSecond = 30 * 1000; 	
			}

			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(timeoutMilliSecond)
					.setConnectionRequestTimeout(timeoutMilliSecond)
					.setSocketTimeout(timeoutMilliSecond)
					.build();
			HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			HttpGet httpGet = new HttpGet(apiUrl);

			/** Adding add custom headers in request */
			/** Adding supplied custom headers in request */
			if(headers == null || headers.isEmpty()) {
				logger.debug("not setting headers.. ");
			}else {
				for(Entry<String, String> map : headers.entrySet())
				{
					String name = map.getKey().trim();
					String value = map.getValue().trim();
					httpGet.addHeader(name, value);
				}
			}
			response = httpClient.execute(httpGet);

		}catch(Exception e) {
			logger.error("Exception in getting response from url - " +apiUrl, e);
		}
		return response;
	}


	/**
	 * send post request with custom params and entities
	 * @param apiUrl
	 * @param requestEntity
	 * @param timeout
	 * @return
	 */
	public CloseableHttpResponse sendPostRequest(String apiUrl, Object requestEntity, int timeoutMilliSecond, 
			HashMap<String, String> headers) {

		CloseableHttpResponse response = null;
		try {
			if(timeoutMilliSecond == 0 ) {
				timeoutMilliSecond = 30 * 1000; 	
			}

			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(timeoutMilliSecond)
					.setConnectionRequestTimeout(timeoutMilliSecond)
					.setSocketTimeout(timeoutMilliSecond)
					.build();
			CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();;

			HttpPost httpPost = new HttpPost(apiUrl);

			/** Adding supplied custom headers in request */
			if(headers == null || headers.isEmpty()) {
				logger.debug("not setting headers.. ");
			}else {
				for(Entry<String, String> map : headers.entrySet())
				{
					String name = map.getKey().trim();
					String value = map.getValue().trim();
					httpPost.addHeader(name, value);
				}
			}

			if(requestEntity instanceof String) {

				httpPost.setEntity(new StringEntity((String) requestEntity,"UTF-8"));

			}else if (requestEntity instanceof HttpEntity) {

				httpPost.setEntity((HttpEntity) requestEntity);
			}

			response = client.execute(httpPost);

		}catch (Exception e) {
			logger.error("Exception occured while getting data from ES via API ", e);
		}
		return response;
	}


	/**
	 * send post request with List<Name,Value> params
	 * @param apiUrl
	 * @param NameValueParams
	 * @param timeoutMilliSecond
	 * @param headers
	 * @return
	 */
	public String sendPostRequest(String apiUrl, List<NameValuePair> nameValueParams, int timeoutMilliSecond, 
			HashMap<String, String> headers) {

		String response_data = "";

		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(nameValueParams);
			CloseableHttpResponse response = sendPostRequest(apiUrl,httpEntity,timeoutMilliSecond, headers);
			response_data = EntityUtils.toString(response.getEntity(), "UTF-8");

		}catch (Exception e) {
			logger.error("Exception occured while getting data from ES via API ", e);
		}
		return response_data;
	}


	/**
	 * get response as string of a post request
	 * @param apiUrl
	 * @param requestEntity
	 * @param headers
	 * @return
	 */
	public String sendPostRequest(String apiUrl, Object requestEntity, HashMap<String, String> headers) {

		String response = "";
		try {
			HttpEntity entity = sendPostRequest(apiUrl, requestEntity, 0, headers).getEntity();
			return EntityUtils.toString(entity, "UTF-8");
		}catch (Exception e) {
			logger.error(UNIQ_EXECUTION_ID + " error while getting response of received api- "+apiUrl, e);
			return response;
		}			
	}

	/**
	 * get generic json headers map
	 * @return
	 */
	public HashMap<String, String> getJsonGenericHeaders(){

		HashMap<String, String> headers = new HashMap<String, String>(); 
		headers.put("Content-Type", "application/json");
		headers.put("Accept","application/json");

		return headers;
	}

	/** send GET request and receive json response.
	 * 
	 * @param url
	 * @param header
	 * @return
	 */
	public org.json.simple.JSONObject getRequestnResponse(String url, HashMap<String, String> header){

		org.json.simple.JSONObject jsonResultObject = new org.json.simple.JSONObject();

		try{
			HttpClient httpclient=HttpClientBuilder.create().build();
			HttpEntity responseEntity;
			String responseResult;
			HttpGet httpget=new HttpGet(url);

			if(header != null) {				
				for(Entry<String, String> en : header.entrySet()) {
					httpget.addHeader(en.getKey(), en.getValue());
				}
			}
			HttpResponse response=httpclient.execute(httpget);
			responseEntity=response.getEntity();
			int statuscode = response.getStatusLine().getStatusCode();

			responseResult=EntityUtils.toString(responseEntity);	
			JSONParser jp = new JSONParser();
			jsonResultObject = (org.json.simple.JSONObject) jp.parse(responseResult);
			jsonResultObject.put("status",statuscode);
			logger.info(UNIQ_EXECUTION_ID + " - " + " response received: "+jsonResultObject + "  for supplied request: "+url);

		}catch(Exception e){
			logger.error(UNIQ_EXECUTION_ID + " - " +e.getMessage(), e);
		}

		return jsonResultObject; 
	}


	public org.json.simple.JSONObject postRequest(String url, HashMap<String, String> header ,String param)
	{
		org.json.simple.JSONObject jsonResultObject = new org.json.simple.JSONObject();
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpEntity responseEntity;
			String responseResult;
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(param);
			if(header != null) {				
				for(Entry<String, String> en : header.entrySet()) {
					request.addHeader(en.getKey(), en.getValue());
				}
			}
			request.setEntity(params);

			//org.apache.http.HttpResponse response = httpClient.execute(request);
			org.apache.http.HttpResponse response=httpClient.execute(request);
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity);

			JSONParser jp = new JSONParser();
			jsonResultObject = (org.json.simple.JSONObject) jp.parse(responseResult);
			logger.info(UNIQ_EXECUTION_ID + " - for request - "+url + " response received: "+jsonResultObject);
			return jsonResultObject;
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID + " - " +e.getMessage(), e);
			return jsonResultObject;
		}
	}


	/**
	 * get response entity and use other fields in client
	 * @param url
	 * @param header
	 * @param param
	 * @return
	 */
	public org.apache.http.HttpResponse sendPostRequest_GetResponseObject(String url, HashMap<String, String> header ,String param)
	{
		org.apache.http.HttpResponse response = null;

		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(param);
			if(header != null) {				
				for(Entry<String, String> en : header.entrySet()) {
					request.addHeader(en.getKey(), en.getValue());
				}
			}
			request.setEntity(params);

			response=httpClient.execute(request);

			logger.info(UNIQ_EXECUTION_ID + " - for request - "+url + " status code: "+response.getStatusLine().getStatusCode());

		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID + " - " +e.getMessage(), e);;
		}

		return response;
	}

	/**
	 * this method will check if impression request is ON or not. if on - execution won't be triggered for apps. 
	 * @return
	 */
	public boolean ifProceedAssumingImpressionRequestBlocked()
	{
		try {
			String url = "https://prod-events.nykaa.com/log_data/log";
			String param = "{\"common_fields\":{\"platform\":\"desktop\",\"vertical\":\"www.nykaa.com\",\"app_version\":\"\",\"device_model\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36\",\"session_id\":\"\",\"mc_id\":\"40568605954543053313066086497005998445\",\"timestamp\":1591950339},\"events\":[{\"targetId\":\"5d789a2525868f2eb1586e145ed3a7ba73ec190a7baf9447\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"godrej-professional\",\"position1\":\"12\",\"position2\":\"1\",\"transaction_id\":\"c6b4a6c46fa157fb8847dddc45d8f965\",\"event\":\"home:impressions\",\"impressions\":1},{\"targetId\":\"5d789a2525868f2eb1586e145eda46d2a77f74ee70d0be13\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"veggie-clean\",\"position1\":\"12\",\"position2\":\"2\",\"transaction_id\":\"ce9c2e6716953e1a713548ff7ffbfdce\",\"event\":\"home:impressions\",\"impressions\":1},{\"targetId\":\"5d789a2525868f2eb1586e145ee05f4d9d0359e0814d93bd\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"livon\",\"position1\":\"12\",\"position2\":\"3\",\"transaction_id\":\"bdf6915ac8363d22e89cc9f6f00e3842\",\"event\":\"home:impressions\",\"impressions\":1},{\"targetId\":\"5d789a2525868f2eb1586e145ec29d07526f0d85c3e85708\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"lotus-make-up\",\"position1\":\"12\",\"position2\":\"4\",\"transaction_id\":\"bcc3475c143b006378e56d6c373bb2ce\",\"event\":\"home:impressions\",\"impressions\":1},{\"targetId\":\"5d789a2525868f2eb1586e145ed3a7ba73ec190a7baf9447\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"godrej-professional\",\"position1\":\"12\",\"position2\":\"1\",\"transaction_id\":\"c6b4a6c46fa157fb8847dddc45d8f965\",\"event\":\"adplatform:impressions\",\"impressions\":1},{\"targetId\":\"5d789a2525868f2eb1586e145eda46d2a77f74ee70d0be13\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"veggie-clean\",\"position1\":\"12\",\"position2\":\"2\",\"transaction_id\":\"ce9c2e6716953e1a713548ff7ffbfdce\",\"event\":\"adplatform:impressions\",\"impressions\":1},{\"targetId\":\"5d789a2525868f2eb1586e145ee05f4d9d0359e0814d93bd\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"livon\",\"position1\":\"12\",\"position2\":\"3\",\"transaction_id\":\"bdf6915ac8363d22e89cc9f6f00e3842\",\"event\":\"adplatform:impressions\",\"impressions\":1},{\"targetId\":\"5d789a2525868f2eb1586e145ec29d07526f0d85c3e85708\",\"namespace0\":\"desktop-homepage\",\"namespace1\":\"more_offers_for_you\",\"namespace2\":\"lotus-make-up\",\"position1\":\"12\",\"position2\":\"4\",\"transaction_id\":\"bcc3475c143b006378e56d6c373bb2ce\",\"event\":\"adplatform:impressions\",\"impressions\":1}]}";

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(param);

			request.setEntity(params);

			HttpResponse response=httpClient.execute(request);

			int responseCode = response.getStatusLine().getStatusCode();

			logger.info(UNIQ_EXECUTION_ID + " - for request - "+url + " status code: "+response.getStatusLine().getStatusCode());
			if (String.valueOf(responseCode).startsWith("2") || String.valueOf(responseCode).startsWith("3"))
			{
				logger.trace(UNIQ_EXECUTION_ID + " impression request is not blocked .... "+responseCode);
				return false;
			}else
			{
				return true;
			}			
		}catch (Exception e) {
			logger.error(UNIQ_EXECUTION_ID+" : Exception Error occurred While checking impression request ", e);

			if (e instanceof  org.apache.http.conn.HttpHostConnectException) {
				logger.error(UNIQ_EXECUTION_ID+" : not able to connect to host. .. return true");
				return true;
			}else {
				logger.error(UNIQ_EXECUTION_ID+" : unknown error . .. return false "+e);
				return false;
			}
		}
	}
	
	/**
	 * Method to send post request with body as string form of a java object.
	 * @param apiUrl
	 * @param jsonString
	 * @return
	 */
	public JSONObject requesthandler_POST(String apiUrl, String json,HashMap<String,String> requestHeaders) {
		JSONObject jsonObject= new JSONObject();
		try {
			int timeout = 30000; //milisec 
			HashMap<String, String> headers = requestHeaders;
			CloseableHttpResponse response = new HttpRequestHandler().sendPostRequest(apiUrl, json, timeout, headers);

			if(response.getStatusLine().getStatusCode()==200) {
				HttpEntity responseEntity = response.getEntity();
				String responseString = EntityUtils.toString(responseEntity, "UTF-8");
				jsonObject = new JSONObject(responseString);
				logger.info("Got valid response from "+apiUrl);
			}
		}catch (Exception e) {
			logger.error("Exception occured while getting sending request to "+apiUrl+" via API ", e);
		}
		return jsonObject;
	}

}
