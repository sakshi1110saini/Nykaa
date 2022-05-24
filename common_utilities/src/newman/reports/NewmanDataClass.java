package newman.reports;

public class NewmanDataClass {

	
	private String collectionName;
	private String iterations;
	private String assertions;
	private String failedTests;
	private String skippedTests;
	
	private String environment;
	
	private String newmanFile;
	
	private String filePath;
	
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getIterations() {
		return iterations;
	}
	public void setIterations(String iterations) {
		this.iterations = iterations;
	}
	public String getAssertions() {
		return assertions;
	}
	public void setAssertions(String assertions) {
		this.assertions = assertions;
	}
	public String getFailedTests() {
		return failedTests;
	}
	public void setFailedTests(String failedTests) {
		this.failedTests = failedTests;
	}
	public String getSkippedTests() {
		return skippedTests;
	}
	public void setSkippedTests(String skippedTests) {
		this.skippedTests = skippedTests;
	}
	public String getNewmanFile() {
		return newmanFile;
	}
	public void setNewmanFile(String newmanFile) {
		this.newmanFile = newmanFile;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
}
