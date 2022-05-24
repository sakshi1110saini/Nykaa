package newman.reports;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import generic.utils.FileLib;


public class ParseNewmanHtmls {

	public static void main(String[] args) {

		String content = new FileLib(null).readContentOfFile("/home/abc/Documents/workspace/nykaa_automation/results/newman-run-report-htmlextra-2019-02-18-11-54-21-204-0.html").toString();

		Document document = Jsoup.parse(content);

		String collectionName = document.select("div[class='card-body']").first().text();
		
		System.out.println(collectionName);

	}

	public static List<NewmanDataClass> getNewmManData() {

		//get all html files
		File file = new File(System.getProperty("user.dir")+"/results");
		
		File[] files = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));

		List<NewmanDataClass> list = new ArrayList<>();		

		//read files
		for(File f : files) {

			String content = new FileLib(null).readContentOfFile(f.getAbsolutePath()).toString();
			System.out.println(f.getName());
			
			Document document = Jsoup.parse(content);

			// get the collection name and environment
			Element element = document.select("div[class='row']").first();

			if(element != null) {

				NewmanDataClass newman = new NewmanDataClass();

				try {
					String iterations = document.select("div.card-body h6:contains(Total Iterations) + h1").first().text();
					String assertions = document.select("div.card-body h6:contains(Total Assertions) + h1").first().text();
					String skippedTests = document.select("div.card-body h6:contains(Total Skipped Tests) + h1").first().text();
					String failedTests = document.select("div.card-body h6:contains(Total Failed Tests) + h1").first().text();
					String collectionName = document.select("div[class='card-body']").first().text();
					collectionName = collectionName.substring(collectionName.indexOf("Collection:")+11, collectionName.indexOf("Environment"));

					newman.setIterations(iterations);
					newman.setSkippedTests(skippedTests);
					newman.setAssertions(assertions);
					newman.setFailedTests(failedTests);
					newman.setNewmanFile(f.getName());
					newman.setCollectionName(collectionName);
					newman.setFilePath(f.getAbsolutePath());

					System.out.println(collectionName + " -" + iterations + " ,"+assertions + " , "+failedTests+ " , "+skippedTests);

					// added objects in list 
					list.add(newman);

				}catch (NullPointerException e) {
					System.out.println("null exception ");
				}

			}	
		}

		return list;
	}

}
