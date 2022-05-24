package newman.reports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class HTMLFileGenerator {
	
	
	
	/**
	 * method to get all the files from result folder
	 * @param path
	 * @return
	 */
	public File[] getListOfFiles() {
		File[] listofFiles =null;
		String folderPath="";
		try {
			String jenkins_url = System.getProperty("JENKINS_URL");
			
			if(jenkins_url ==null) {
				folderPath = System.getProperty("user.dir")+"/results";
			}else {
				folderPath = jenkins_url+"/results";
			}
			File folder = new File(folderPath);
			listofFiles = folder.listFiles();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return listofFiles;
	}
	
	
	public void generateReport() {
		try {
			/** Creating a html template */
			Document doc = Jsoup.parse("<html></html>");
			doc.body().addClass("body-styles-cls");
			doc.title("Postman Aggregated report");
			doc.body().appendElement("div");
			File[] listOfFiles = getListOfFiles();
			
			for (File file : listOfFiles) {
			    if (file.isFile() && file.getName().contains(".html")) {
			    	String path = file.getCanonicalPath();
			    	Element element = doc.body().appendElement("ul").appendElement("li");
			    	element.tagName("a").attr("href", path).text(file.getName());
			    }
			}
			
			//Writer into the html file
			File file = new File(System.getProperty("user.dir")+"/results/index.html");
			FileWriter writer = new FileWriter(file);
			try {
				writer.write(doc.toString());
			}catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			finally {
				try {
					writer.close();
				}catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new HTMLFileGenerator().generateReport();
	}

}
