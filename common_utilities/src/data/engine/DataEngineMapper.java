package data.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataEngineMapper {
	
	private String platform;
	private  Map<String, Object> properties = new HashMap<String,Object>();
    private  Map<String, List<HashMap<String, String>>> login = new HashMap<String, List<HashMap<String, String>>>();
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	public Map<String, List<HashMap<String, String>>> getLogin() {
		return login;
	}
	public void setLogin(Map<String, List<HashMap<String, String>>> login) {
		this.login = login;
	}
    
}
