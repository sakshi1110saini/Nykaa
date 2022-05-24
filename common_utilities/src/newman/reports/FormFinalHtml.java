package newman.reports;


import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import generic.utils.FileLib;


public class FormFinalHtml {
	
	public static Document doc;

	public static void test() {
		// TODO Auto-generated method stub

		List<NewmanDataClass> newmanList = ParseNewmanHtmls.getNewmManData();

		if(!newmanList.isEmpty()) {

			String newmanSample = System.getProperty("user.dir")+"/newman-sample-results/newman.html";

			doc = Jsoup.parse(new FileLib(null).readContentOfFile(newmanSample).toString());
			Element e1 = doc.select("div[class=\"col-md-9 col-lg-12 main\"]").first();

			for(NewmanDataClass newman : newmanList) {
				
				System.out.println("Forming html from file name: "+newman.getNewmanFile());
				
				e1.append(formFinalHTML(newman.getCollectionName(), newman.getIterations(), newman.getAssertions(), 
						newman.getFailedTests(), newman.getSkippedTests(), newman.getFilePath(), newman.getNewmanFile()));

			}

			System.out.println("--------------------------------------------------------------");
			System.out.println(doc.toString());
			System.out.println("--------------------------------------------------------------");

			new FileLib(null).writeTextInFile(System.getProperty("user.dir")+"/results/newman-summary.html", doc.toString());
		}
		else {

			System.out.println("************************** NO RESULT FILE FOUND ********** ");
		}
	}


	public static String formFinalHTML(String collectionName, String iterations, String assertions,
			String failedTests, String skippedTests, String filePath, String newmanFile) {

		Element e0 = new Element("div");
		Element row = e0.appendElement("div").addClass("none");

		Element r1 = row.append(getCollectionRow(collectionName));
		Element r2= r1.appendElement("div").addClass("row");
		r1.select("span").tagName("a").attr("href", filePath);
		r2.append(getIterations(iterations, failedTests));
		r2.append(getAssertions(assertions));
		r2.append(getFailedTests(failedTests));
		r2.append(getSkippedTests(skippedTests));

		e0.appendElement("br");

		System.out.println(">>>>>>>>>>>>>>>>>>> FINAL >>>>>>>>>>>>>>>>>>>");
		System.out.println(e0.html());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		return e0.html();
	}

	public static String getCollectionRow(String collectionName) {

		Element element = new Element("div");
		Element e0 = element.appendElement("span").text("Collection: ").appendElement("strong");

		//add collection name
		e0.text(collectionName);

		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
		System.out.println(element.html());
		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");

		return element.html();
	}

	public static String getIterations(String iterations, String failedTest) {
		
		Element e1 = new Element("div");
		Element e1_1 = e1.appendElement("div").addClass("col-lg-3 col-md-6");

		Element e2 = e1_1.appendElement("div").addClass("card text-white card-success");
		Element e3;
		if(Integer.parseInt(failedTest)>0) {
			e3 = e2.appendElement("div").addClass("card-body bg-danger");
		}else {
			e3 = e2.appendElement("div").addClass("card-body bg-success");
		}
		Element e4 = e3.appendElement("div").addClass("rotate");
		e4.appendElement("i").addClass("fa fa-retweet fa-5x");

		Element e5 = e3.appendElement("h6").addClass("text-camelcase").text("Iterations: ");

		//add iteration count 
		e5.appendElement("span").text(iterations);

		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
		System.out.println(e1.html());
		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");

		return e1.html();
	}

	public static String getAssertions(String assertions) {

		Element e1 = new Element("div");
		Element e1_1 = e1.appendElement("div").addClass("col-lg-3 col-md-6");

		Element e2 = e1_1.appendElement("div").addClass("card text-white card-danger");
		Element e3 = e2.appendElement("div").addClass("card-body bg-success");
		Element e4 = e3.appendElement("div").addClass("rotate");
		e4.appendElement("i").addClass("fa fa-list fa-4x");

		Element e5 = e3.appendElement("h6").addClass("text-camelcase").text("Assertions: ");

		//add iteration count 
		e5.appendElement("span").text(assertions);

		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
		System.out.println(e1.html());
		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");

		return e1.html();
	}

	public static String getFailedTests(String failedTests) {

		Element e1 = new Element("div");
		Element e1_1 = e1.appendElement("div").addClass("col-lg-3 col-md-6");

		Element e2 = e1_1.appendElement("div").addClass("card text-white card-info");
		Element e3;
		if(Integer.parseInt(failedTests)>0) {
			e3 = e2.appendElement("div").addClass("card-body bg-danger");
		}else {
			e3 = e2.appendElement("div").addClass("card-body bg-success");
		}
		Element e4 = e3.appendElement("div").addClass("rotate");
		e4.appendElement("i").addClass("fa fa-plus-circle fa-5x");

		Element e5 = e3.appendElement("h6").addClass("text-camelcase").text("Failed Tests: ");
		
		

		//add iteration count 
		e5.appendElement("span").text(failedTests);

		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
		System.out.println(e1.html());
		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");

		return e1.html();
	}

	public static String getSkippedTests(String skippedTests) {

		Element e1 = new Element("div");
		Element e1_1 = e1.appendElement("div").addClass("col-lg-3 col-md-6");

		Element e2 = e1_1.appendElement("div").addClass("card text-white card-warning");
		Element e3;
		if(Integer.parseInt(skippedTests)>0) {
			e3 = e2.appendElement("div").addClass("card-body bg-warning");
		}else {
			e3 = e2.appendElement("div").addClass("card-body bg-success");
		}
		Element e4 = e3.appendElement("div").addClass("rotate");
		e4.appendElement("i").addClass("fa fa-share fa-5x");

		Element e5 = e3.appendElement("h6").addClass("text-camelcase").text("Skipped Tests: ");

		//add iteration count 
		e5.appendElement("span").text(skippedTests);

		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
		System.out.println(e1.html());
		System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");

		return e1.html();
	}
}
