package objectHandler;

import java.util.List;

public class ObjectValues {
	
	private String value;
	private String weightage;
	private List<String> supported_version;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getWeightage() {
		return weightage;
	}
	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}
	public List<String> getSupported_version() {
		return supported_version;
	}
	public void setSupported_version(List<String> supported_version) {
		this.supported_version = supported_version;
	}

}
