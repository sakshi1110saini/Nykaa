/**
 * Last Changes Done on Jan 27, 2015 12:39:54 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package generic.utils;


import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class FileLib 
{		
	Logger logger = LogManager.getLogger(FileLib.class.getName());
	private Object UNIQ_EXECUTION_ID = "";

	public FileLib() {
	}

	public FileLib(Object UNIQ_EXECUTION_ID) {
		this.UNIQ_EXECUTION_ID = UNIQ_EXECUTION_ID;
	}

	//******************** Generic Method To Write String In File ***************************//
	public  boolean writeFile(String strContent, String testFileLocation, String fileName)
	{	
		boolean flag = false;
		try
		{
			File testDataFolderLocation = new File(testFileLocation);

			if(!(testDataFolderLocation.exists()))
			{
				logger.info(UNIQ_EXECUTION_ID+" : Test Data folder doesn't exist at " +testFileLocation);
				boolean b = testDataFolderLocation.mkdirs();

				if(b)
				{
					logger.info(UNIQ_EXECUTION_ID+" : Test data folder was created successfully "); 
				}
				else
				{
					logger.info(UNIQ_EXECUTION_ID+" : Test data folder wasn't created");
				}
			}

			String testFile = testFileLocation+"/"+fileName;
			File file = new File(testFile);

			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(strContent);
			bw.close();
			fw.close();

			flag = true;
		}catch(Exception t)
		{
			flag = false;
			logger.error(UNIQ_EXECUTION_ID+" : Error occurred while writing file. ", t);
		}
		return flag;
	}


	//******************** Generic Method To Write / Append String In File ***************************//
	@SuppressWarnings("finally")
	public  boolean writeFile(String strContent, String testFileLocation, String fileName, boolean append) 
	{	

		boolean flag = false;

		try{
			File testDataFolderLocation = new File(testFileLocation);

			if(!(testDataFolderLocation.exists()))
			{
				logger.info(UNIQ_EXECUTION_ID+" : Directory doesn't exist at " +testFileLocation);
				boolean b = testDataFolderLocation.mkdirs();

				if(b)
				{
					logger.info(UNIQ_EXECUTION_ID+" : Directory was created successfully "); 
				}
				else
				{
					logger.info(UNIQ_EXECUTION_ID+" : Directory was not created");
				}
			}

			String testFile = testFileLocation+"/"+fileName;
			File file = new File(testFile);

			FileWriter fw = new FileWriter(file, append);
			fw.write(strContent);
			fw.close();

			logger.info(UNIQ_EXECUTION_ID+" : File : " +testFile+ " is written successfully");

			flag = true;
		}catch(Exception e)
		{
			flag = false;
			logger.info(UNIQ_EXECUTION_ID+" : Exception occured while writing in file. ", e);
		}
		finally
		{
			return flag;
		}
	}


	public  boolean writeFile(String strContent, String testFileNameWithLocation, boolean append) 
	{	
		try{
			File file = new File(testFileNameWithLocation);

			FileWriter fw = new FileWriter(file, append);
			fw.write(strContent);
			fw.close();

			logger.info(UNIQ_EXECUTION_ID+" : File : " +testFileNameWithLocation+ " is written successfully");

			return true;
		}catch(Exception e)
		{
			logger.info(UNIQ_EXECUTION_ID+" : Exception occured while writing in file. ", e);
			return false;
		}

	}


	//******************** This Method Will Create the directory at the given location *************************************************//
	public  boolean createDirectory(String directoryLocation)
	{
		File checkLocation = new File(directoryLocation);
		boolean b = false;

		if(!(checkLocation.exists()))
		{
			//logger.info(TestUNIQ_EXECUTION_ID+" : Log folder doesn't exist at " +logFileLocation);
			b = checkLocation.mkdirs();

			if(b)
			{
				logger.info(UNIQ_EXECUTION_ID+" : Directory is created at: "+checkLocation);
				return b;
			}
			else
			{
				logger.info(UNIQ_EXECUTION_ID+" : Directory wasn't created at: "+checkLocation);
				return b;
			}
		}
		else
		{
			logger.info(UNIQ_EXECUTION_ID+" : Directory: " +checkLocation + " already exists.");
			return true;
		}
	}



	//******************** This Method Will Return All The Files Matching The Given Extension *************************************************//
	@SuppressWarnings("finally")
	public  String getFilesWithSpecificExtensionInDirectory(String directory, final String fileExtension)
	{
		String fileNames = "";
		String[] fileList = null;

		try
		{
			File file = new File(directory);

			fileList = file.list(new FilenameFilter()
			{	
				@Override
				public boolean accept(File dir, String name) 
				{
					return name.endsWith(fileExtension);
				}
			});

			if(fileList.length < 1)
			{
				logger.info(UNIQ_EXECUTION_ID+" : No File was found having extension: "+fileExtension);
			}
			else if(fileList.length == 1)
			{
				fileNames = fileList[0].toString();
			}
			else
			{
				//Print file list
				logger.info(UNIQ_EXECUTION_ID+" : Printing List Of Files Found Having Extension: "+fileExtension);

				for(int i=0; i<fileList.length; i++)
				{
					fileNames = fileNames + fileList[i].toString();

					if(i<fileList.length - 1)
					{
						fileNames = fileNames + ",";
					}

					logger.info(fileList[i].toString());
				}
			}
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: GetFileWithSpecificExtensionInDirectory. ", e);
		}
		finally
		{
			return fileNames;
		}

	}



	//*********** This method will be used to find a text in a file. ******************
	@SuppressWarnings({ "finally" })
	public  boolean findTextInFile(File desiredFile, String desiredText) throws IOException
	{
		boolean flag = false;
		try
		{
			logger.info(UNIQ_EXECUTION_ID+" : Text: "+desiredText + " is being searched in file: "+desiredFile);
			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			String line = "";

			while((line = reader.readLine()) != null)
			{
				if(line.contains(desiredText))
				{
					logger.info(UNIQ_EXECUTION_ID+" : desired text: "+ desiredText+ " is found in line: "+line);
					flag = true;
					break;
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled while searching text: "+desiredText+" in file: "+desiredFile.toString(), e);
		}
		finally
		{
			return flag;
		}
	}


	/** This method will be used to get the whole content of file into a string
	 * 
	 * @param desiredFile
	 * @return
	 */
	public  StringBuilder readContentOfFile(String desiredFile) 
	{
		StringBuilder content = new StringBuilder("");
		String line = "";

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			while((line = reader.readLine()) != null)
			{
				content = content.append(line); 
			}
			reader.close();
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception occured while reading file: "+desiredFile, e);
		}
		return content;
	}


	/*********** This method will be used to copy all (only) files (not sub directory) of source directory to destination dir
	 * 
	 * @param sourceFile
	 * @param destinationDir
	 * @return
	 */
	@SuppressWarnings("finally")
	public  boolean copyFilesFromSourceToDestinationDir(String sourceFile, String destinationDir)
	{
		boolean flag = false;
		try
		{
			File sourceDir = new File(sourceFile); 
			File copyDir = new File(destinationDir);

			//Getting all files located in source directory
			String []requiredFiles = sourceDir.list();

			//Copying all sub files 
			for(int i=0; i<requiredFiles.length; i++)
			{

				File tempFile = new File(sourceDir+"/"+requiredFiles[i]);

				if(!tempFile.isDirectory())
				{
					FileUtils.copyFileToDirectory(tempFile, copyDir);
				}
				logger.info(UNIQ_EXECUTION_ID+" : File: "+tempFile.toString() + " is copied successfully to directory: "+copyDir.toString());
			}

			flag = true;
			logger.info(UNIQ_EXECUTION_ID+" : All files are copied. ");
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: CopyAllFilesToDirectory. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will be used to check a specific file in a directory
	@SuppressWarnings("finally")
	public  boolean checkFileExistsInDirectory(String directory, String checkFileName)
	{
		boolean flag = false;
		try
		{
			File sourceDir = new File(directory); 

			//Getting all files located in source directory
			String []requiredFiles = sourceDir.list();

			//Copying all sub files 
			for(int i=0; i<requiredFiles.length; i++)
			{
				File tempFile = new File(sourceDir+"/"+requiredFiles[i]);

				if(!tempFile.isDirectory())
				{
					//Checking if desired file exists in given directory
					if(requiredFiles[i].equalsIgnoreCase(checkFileName))
					{
						flag = true;
						logger.info(UNIQ_EXECUTION_ID+" : File: "+checkFileName + " exists in directory: "+directory);
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exceptin handled by method: CheckFileInDirectory. ", e);
		}
		finally
		{
			return flag;
		}
	}


	/** This method will write the supplied text in given file in overwrite mode. 
	 * 
	 * @param fileNameWithLocation
	 * @param strText
	 * @return
	 */
	@SuppressWarnings("finally")
	public  boolean writeTextInFile(String fileNameWithLocation, String strText)
	{
		boolean flag =  false;

		try
		{
			logger.debug(UNIQ_EXECUTION_ID+" : Received file location: "+fileNameWithLocation);
			String dir = new StringLib().splitDirectoryFromFileLocation(fileNameWithLocation);
			logger.debug(UNIQ_EXECUTION_ID+" : Splitted directory location: "+dir);

			//Get the directory from file location 
			File directory = new File(dir);

			//and check if directory exists
			if(!(directory.exists()))
			{
				//If not then create directory
				if(directory.mkdirs())
				{
					logger.info(UNIQ_EXECUTION_ID+" : Directory: "+directory + " wasn't existed, its created now. ");
				}
			}

			File file = new File(fileNameWithLocation);

			//And then create file if it doesn't exist
			if(!(file.exists()))
			{
				if(file.createNewFile())
				{
					logger.info(UNIQ_EXECUTION_ID+" : File: " +fileNameWithLocation + " wasn't existed, its created now. ");
				}
			}


			//Write content in file
			FileWriter writer = new FileWriter(fileNameWithLocation);
			writer.write(strText);
			writer.close();

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(UNIQ_EXECUTION_ID+" : Exception occured while writing text in file. ", e);
		}
		finally
		{
			return flag;
		}
	}


	//*********** This method will create a new file at given location ******************
	@SuppressWarnings("finally")
	public  boolean createNewFile(String fileNameWithLocation)
	{
		boolean flag =  false;

		try
		{
			//Get the directory from file location 
			File directory = new File(new StringLib(UNIQ_EXECUTION_ID).splitDirectoryFromFileLocation(fileNameWithLocation));

			//and check if directory exists
			if(!(directory.exists()))
			{
				//If not then create directory
				if(directory.mkdirs())
				{
					logger.info(UNIQ_EXECUTION_ID+" : Directory: "+directory + " wasn't existed, its created now. ");
				}
			}

			File file = new File(fileNameWithLocation);

			//And then create file if it doesn't exist
			if(!(file.exists()))
			{
				if(file.createNewFile())
				{
					flag = true;
					logger.info(UNIQ_EXECUTION_ID+" : File: " +fileNameWithLocation + " wasn't existed, its created now. ");
				}
			}
			else
			{
				flag = true;
				logger.info(UNIQ_EXECUTION_ID+" : File: " +fileNameWithLocation + " already existed. ");
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: CreateNewFile. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will return the last modified file at the given directory ******************
	@SuppressWarnings("finally")
	public  String getLastModifiedFile(String directory, String fileExtension)
	{
		String modifiedFile = "NO_FILE"; 
		try
		{
			logger.info(UNIQ_EXECUTION_ID+" : Searching the last modified file at location: "+directory);

			File dir = new File(directory);

			FileFilter fileFilter = new WildcardFileFilter("*."+fileExtension);
			File[] files = dir.listFiles(fileFilter);

			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

			modifiedFile = files[0].toString();

			logger.info(UNIQ_EXECUTION_ID+" : Found the last modified file: " +modifiedFile);
		}
		catch(Exception e)
		{
			modifiedFile = "NO_FILE";
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: GetLastModifiedFile. ", e);
		}
		finally
		{
			return modifiedFile;
		}
	}

	/** Get clipboard text
	 * 
	 * @return
	 */
	public  String getClipBoardText()
	{
		String text= "";

		try {
			text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}	
		return text;
	}


	/**
	 * this method will download the given file.
	 * @param apiUrl
	 * @param filename
	 * @return
	 */
	public boolean downloadFile(String apiUrl, String location) {
		try {
			logger.info("***** downlaoding file - "+apiUrl);
			int timeout = 60 * 1000; //sec 

			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout)
					.setSocketTimeout(timeout)
					.build();
			HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

			HttpGet httpGet = new HttpGet(apiUrl);
			HttpResponse response = httpClient.execute(httpGet);	

			if(response.getStatusLine().getStatusCode()==200) {

				HttpEntity entity = response.getEntity();
				String response_data = EntityUtils.toString(entity, "UTF-8");
				StringEscapeUtils.escapeHtml(response_data);
				PrintWriter out = new PrintWriter(new File(location));
				out.print(response_data);
				out.close();
				logger.info("***** downlaoding file - "+apiUrl+ " -- completed.. ");
				return true;
			}else {
				logger.error("Api status code is not 200 ok >> status from: "+apiUrl + " -- " +response.getStatusLine().getStatusCode());
				return false;
			}
		}catch(Exception e) {

			logger.error("Unable to get the response from api: "+apiUrl, e);
			return false;
		}
	}


	/**
	 * this method will give the hashmap of all the files in a directory with file location as its value.
	 * @param directoryname
	 * @return HashMap 
	 * @throws IOException 
	 */
	public HashMap <String,String> listofallFiles(String directoryLocation) throws IOException 
	{	
		HashMap <String,String> allFiles = new HashMap<String,String>();
		try 
		{		
			Iterator files = Files.find(Paths.get(directoryLocation),
					Integer.MAX_VALUE,
					(filePath, fileAttr) -> fileAttr.isRegularFile()).iterator();

			while(files.hasNext())
			{
				String fileAndDirectoryName;
				try
				{
					fileAndDirectoryName = files.next().toString().replace("\\", "/");
				}
				catch(Exception e)
				{
				fileAndDirectoryName = files.next().toString();
				}
				if(fileAndDirectoryName.endsWith(".yaml"))
				{
					String[] getFileNamearray = fileAndDirectoryName.split("/");
					String fileName = getFileNamearray[getFileNamearray.length-1];
					allFiles.put(fileName,fileAndDirectoryName);		
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Exception while reading the filename from the given directory", e);
		}
		return allFiles;
	}
	
	/** getting a hashmap of file in a directory*/
	public HashMap<String, String> getAllFileInDirectory(String filePath){
		HashMap<String, String> directoryMap = new HashMap<String, String>();
		try {
			File directory = new File(filePath);
			File[] masterFileList = directory.listFiles();
			for(File file : masterFileList) {
				String fileName = file.getName();
				String path = file.getCanonicalPath();
				directoryMap.put(fileName, path);
			}
			
		}catch (Exception e) {
			logger.error("Exception while reading the filename from the given directory", e);
		}
		return directoryMap;
	}

}
