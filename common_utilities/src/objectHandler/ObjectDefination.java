package objectHandler;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ObjectDefination {
	
	@JsonIgnore
	private Object _id;
	
	private String objectName;
	
	private Map<String, List<Identifier>> platform;
	

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public Map<String, List<Identifier>> getPlatform() {
		return platform;
	}

	public void setPlatform(Map<String, List<Identifier>> platform) {
		this.platform = platform;
	}

	public Object get_id() {
		return _id;
	}

	public void set_id(Object _id) {
		this._id = _id;
	}
}
