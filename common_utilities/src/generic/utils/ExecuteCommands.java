/**
 * Last Changes Done on Jan 19, 2015 12:39:24 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */
package generic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;



public class ExecuteCommands 
{

	Logger logger = LogManager.getLogger(ExecuteCommands.class.getName());

	private Object UNIQ_EXECUTION_ID = "";

	public ExecuteCommands() {
	}

	public ExecuteCommands(Object UNIQ_EXECUTION_ID) {
		this.UNIQ_EXECUTION_ID = UNIQ_EXECUTION_ID;
	}

	@SuppressWarnings("finally")
	public  String ExecuteCommand_ReturnsOutput(String inputCommand) 
	{	
		logger.info(UNIQ_EXECUTION_ID+" : Command: " + inputCommand + " is being executed:");
		StringBuffer output = new StringBuffer();
		try
		{
			Process p;
			p =  Runtime.getRuntime().exec(inputCommand);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while (reader.readLine()!= null) 
			{
				logger.info(UNIQ_EXECUTION_ID+" : Entered In While Loop: " +reader.readLine());
				output.append(reader.readLine()+"\n");
				logger.info(UNIQ_EXECUTION_ID+" : output is ="+ output);
			}

			logger.info(UNIQ_EXECUTION_ID+" : Command Output - " +output);
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteCommand_ReturnsOutput. ",e);
		}
		finally
		{
			return output.toString();
		}
	}


	//This is generic method to execute command array to return string
	@SuppressWarnings("finally")
	public  String ExecuteCommand_ReturnsOutput(String []inputCommand)  
	{	
		logger.info(UNIQ_EXECUTION_ID+" : Command: " + inputCommand.toString() + " is being executed:");
		StringBuffer output = new StringBuffer();
		try
		{
			logger.info(UNIQ_EXECUTION_ID+" : Reading Command Output....");
			ProcessBuilder builder = new ProcessBuilder( inputCommand);
			builder.redirectErrorStream(true);
			Process p = builder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while (reader.readLine()!= null) 
			{
				output.append(reader.readLine() + "\n");
			}

			logger.debug(UNIQ_EXECUTION_ID+" : Command Exit Status: "+p.exitValue());
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteCommand_ReturnsOutput. ", e);
		}
		finally
		{
			return output.toString();
		}
	}


	//This is generic method to execute command array and return exit status
	@SuppressWarnings("finally")
	public  int ExecuteCommand_ReturnsExitStatus(String []inputCommand)  
	{	
		logger.info(UNIQ_EXECUTION_ID+" : Command: " + inputCommand.toString() + " is being executed:");
		int exitStatus = 777777;
		StringBuffer output = new StringBuffer();
		try
		{
			ProcessBuilder builder = new ProcessBuilder( inputCommand);
			builder.redirectErrorStream(true);
			Process p = builder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while (reader.readLine()!= null) 
			{
				//logger.info(TestUNIQ_EXECUTION_ID+" : Entered In While Loop: " +reader.readLine());
				output.append(reader.readLine() + "\n");
			}

			exitStatus = p.exitValue();

			logger.debug(UNIQ_EXECUTION_ID+" : Command Exit Status: "+exitStatus);
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteCommand_ReturnsOutput. ", e);
		}
		finally
		{
			return exitStatus;
		}
	}


	@SuppressWarnings("finally")
	public  int ExecuteCommand_ReturnsExitStatus(String inputCommand)  
	{	
		logger.info(UNIQ_EXECUTION_ID+" : Command: " + inputCommand + " is being executed:");
		int exitStatus = 777777777;

		try
		{
			ProcessBuilder builder = new ProcessBuilder(inputCommand);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.waitFor();

			exitStatus = p.exitValue();
			logger.info(UNIQ_EXECUTION_ID+" : Exit Status: " +exitStatus);
		}
		catch(Exception e)
		{
			exitStatus = 777777777;
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteCommand_ReturnsExitStatus. ", e);
		}
		finally
		{
			return exitStatus;
		}
	}


	@SuppressWarnings("finally")
	public  String ExecuteNonWindowsCommand_ReturnsOutput(String inputCommand)  
	{	
		logger.info(UNIQ_EXECUTION_ID+" : Executing Command: " + inputCommand);
		String line = "";
		String output = "";

		try
		{
			ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", inputCommand);
			builder.redirectErrorStream(true);
			Process p = builder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			logger.info(UNIQ_EXECUTION_ID+" : Reading Command Output....");

			while ((line = reader.readLine())!= null) 
			{
				output = output + line + "\n";
			}
		}
		catch(Exception e)
		{
			output = "";
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteMacCommand_ReturnsOutput. ", e);
		}
		finally
		{
			return output.trim();
		}
	}


	@SuppressWarnings("finally")
	public  int ExecuteNonWindowscCommand_ReturnsExitStatus(String inputCommand)  
	{	
		logger.info(UNIQ_EXECUTION_ID+" : Command: " + inputCommand + " is being executed:");		
		int output = 777777777;

		try
		{
			ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", inputCommand);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.waitFor();

			output = p.exitValue();
			logger.info(UNIQ_EXECUTION_ID+" : Exit Status: " +output);
		}
		catch(Exception e)
		{			
			output = 007;
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteMacCommand_ReturnsExitStatus. ", e);
		}
		finally
		{
			return output;
		}
	}


	//This method create session with server using host name and user name and password
	public  Session createSessionWithPassword(String userName, String password, String host) throws JSchException, InterruptedException
	{
		JSch jsch = new JSch();
		Session session = null;

		try
		{
			session = jsch.getSession(userName, host);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(15000);

			if(session.isConnected())
			{
				logger.info(UNIQ_EXECUTION_ID+" : Session is established with host: " +host);
			}
			else
			{
				logger.info(UNIQ_EXECUTION_ID+" : ******* Session is not established with host: " +host+ " ************");
			}
		}
		catch(JSchException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : There was a problem while establishing connection with host: " +host, e);
		}

		return session;
	}


	//This method create session with server using host name and user name and private key
	@SuppressWarnings("unused")
	public  Session createSessionWithPrivateKey(String userName, String privateKeyLocation, String host)
	{
		JSch jsch = new JSch();
		Session session = null;
		int i = 0;
		try
		{
			//Use private key
			//jsch.addIdentity(privateKeyLocation);
			jsch.addIdentity(privateKeyLocation, "!nopassword!");
			session = jsch.getSession(userName, host);

			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(15000);

			if(session.isConnected())
			{
				logger.info(UNIQ_EXECUTION_ID+" : Session is established with host: " +host);
			}
			else
			{
				logger.info(UNIQ_EXECUTION_ID+" : ******* Session is not established with host: " +host+ " ************");

				if(session == null && i < 5)
				{
					i++;
					/** retry to get session with serve machine */
					session = new ExecuteCommands().createSessionWithPrivateKey(userName,privateKeyLocation,host);
				}
				
			}
		}
		catch(JSchException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : There was a problem while establishing connection with host: " +host, e);
			if(session == null && i < 5)
			{
				i++;
				/** retry to get session with serve machine */
				session = new ExecuteCommands().createSessionWithPrivateKey(userName,privateKeyLocation,host);
			}

		}

		return session;
	}


	public  void EndSession(Session session) throws JSchException, InterruptedException
	{
		session.disconnect();
		logger.info(UNIQ_EXECUTION_ID+" : session with " +session.getHost() + " is terminated");
	}


	public  void ExecuteCommandUsingJsch(String Command) throws JSchException, IOException, InterruptedException
	{
		JSch jsch = new JSch();

		Session session = jsch.getSession("~~~", "~~~");
		session.setPassword("~~~");
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect(15000);

		if(session.isConnected())
		{
			logger.info(UNIQ_EXECUTION_ID+" : Session is Connected");
		}

		Thread.sleep(1000);

		Channel channel = session.openChannel("exec");

		((ChannelExec)channel).setCommand(Command);

		logger.info(UNIQ_EXECUTION_ID+" : COMMAND  : " + Command);

		channel.connect(5000);

		channel.setOutputStream(System.out);

		BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));

		String msg=null;
		while((msg=in.readLine())!=null)
		{
			logger.info(msg);
		} 

		channel.disconnect();
		session.disconnect();

		logger.info(UNIQ_EXECUTION_ID+" : Command Is executed: " );

	}


	public  void ExecuteCommandUsingJsch(Session session, String Command) 
	{
		try
		{
			Channel channel = session.openChannel("exec");

			((ChannelExec)channel).setCommand(Command);

			logger.info(UNIQ_EXECUTION_ID+" : Command is being executed : ");
			logger.info(Command);

			channel.connect(5000);

			logger.info(UNIQ_EXECUTION_ID+" : Exit Status: " +channel.getExitStatus() + " For Executed Command. ");

			channel.disconnect();
		}
		catch(JSchException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : JSchException handled by method: ExecuteTerminalCommandUsingJsch. ", e);
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteTerminalCommandUsingJsch. ", e);
		}
	}


	@SuppressWarnings("finally")
	public  String ExecuteCommandUsingJschReturnsOutput(Session session, String Command)
	{
		String msg="";
		String output = "";

		try
		{
			//Channel channel = session.openChannel("exec");
			//((ChannelExec)channel).setCommand(Command);

			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(Command);

			InputStream is = channel.getInputStream();

			//logger.info(TestUNIQ_EXECUTION_ID+" : Command is being executed : " + Command);

			channel.connect(5000);

			BufferedReader in=new BufferedReader(new InputStreamReader(is));

			while((msg=in.readLine())!=null)
			{
				output = output + msg + "\n";
				//logger.info(msg);
			} 

			in.close();
			is.close();
			channel.disconnect();
		}
		catch(JSchException e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : JSchException handled by method: ExecuteTerminalCommandUsingJschReturnsOutput. ", e);
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteTerminalCommandUsingJschReturnsOutput. ", e);
		}
		finally
		{
			return output;
		}
	}


	@SuppressWarnings("finally")
	/**
	 * Return the exit status of an executed command.
	 * @param session
	 * @param Command
	 * @return
	 */
	public Object ExecuteCommandUsingJschReturnsExitStatus(Session session, String Command)
	{
		Object exitStatus = null;
		try
		{
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(Command);

			InputStream is = channel.getInputStream();
			channel.connect(5000);

			exitStatus = channel.getExitStatus();
			is.close();
			channel.disconnect();
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception: ", e);
		}
		finally
		{
			return exitStatus;
		}
	}


	/**
	 *  kill a service running on a specific port.
	 * @param port
	 */
	public void killProcess (int port)
	{
		try{

			if(System.getProperty("os.name").contains("Window")){
				String command = "FOR /F \"usebackq tokens=5\" %a in (`netstat -nao ^| findstr /R /C:\""+port+" \"`) do (FOR /F \"usebackq\" %b in (`TASKLIST /FI \"PID eq %a\" ^| findstr /I chromedriver.exe`) do (IF NOT %b==\"\" TASKKILL /F /PID %a))";
				try{Runtime.getRuntime().exec(command);}catch (Exception e) {}
			}
			else{
				try{Runtime.getRuntime().exec("lsof -ti:"+port+" | xargs kill");}catch (Exception e) {}
			}

			logger.info(UNIQ_EXECUTION_ID + " : " + " killed process running on port:  "+port);
		}
		catch(Exception e){
			logger.info(UNIQ_EXECUTION_ID+" -- " + e.getMessage(), e);
		}
	}


	public  boolean GetIphoneConnectionStatus()
	{
		boolean stat = false;
		Process process = null;
		try {
			if(!(System.getProperty("os.name").matches("^Windows.*")))
			{
				process = Runtime.getRuntime().exec("system_profiler SPUSBDataType");
			}
			else
			{
				logger.info(UNIQ_EXECUTION_ID+" : Command yet to coded.");
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) 
			{
				if (line.toLowerCase().contains("iphone"))
				{
					stat= true;
					break;
				}
			}
		}
		catch (IOException e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : Error in running command ' system_profiler SPUSBDataType ' to get iphone status.Error is: ", e);
		}

		return stat;		
	}


	public  ArrayList<String> GetConnectedAndroidDeviceList(String strAndroidHome)
	{
		ArrayList<String> list = new ArrayList<String>();
		Process process = null;
		Matcher matcher;

		try 
		{
			//Granting permission to execute adb on mac machine
			if(!(System.getProperty("os.name").matches("^Windows.*")))
			{
				process = Runtime.getRuntime().exec("chmod 777 "+ strAndroidHome);
			}

			process = Runtime.getRuntime().exec(strAndroidHome + " devices");
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			Pattern pattern = Pattern.compile("^([a-zA-Z0-9\\-]+)(\\s+)(device)");

			while ((line = in.readLine()) != null) {
				if (line.matches(pattern.pattern())) {
					matcher = pattern.matcher(line);
					if (matcher.find())
					{
						list.add(matcher.group(1));
					}
				}
			}
		}
		catch (IOException e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : IOException Handled By Method: GetConnectedAndroidDeviceList ", e);
		}
		return list;
	}


	@SuppressWarnings("finally")
	public  boolean UninstallAppCommand_AndroidDevice(String strAndroidHome, String packageName, String androidDeviceID)
	{
		boolean flag = false;
		Process process = null;
		try 
		{
			//Granting permission to execute adb on mac machine
			if(!(System.getProperty("os.name").matches("^Windows.*")))
			{
				process = Runtime.getRuntime().exec("chmod 777 "+ strAndroidHome);
			}

			process = Runtime.getRuntime().exec(strAndroidHome + " -s "+ androidDeviceID +" uninstall " +packageName);
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;

			while ((line = in.readLine()) != null) 
			{
				if (line.toLowerCase().contains("success"))
				{
					flag = true;
				}
			}
		}
		catch (Exception e) 
		{
			logger.error(UNIQ_EXECUTION_ID+" : IOException Handled By Method: UninstallAppCommand_AndroidDevice ", e);
		}
		finally
		{
			return flag;
		}
	}

	public  void executeShellScript(String command) {
		Process p;
		try {
			List<String> cmdList = new ArrayList<String>();
			cmdList.add("sh");
			cmdList.add(command);

			ProcessBuilder pb = new ProcessBuilder(cmdList);
			p = pb.start();

			p.waitFor();

			BufferedReader reader=new BufferedReader(new InputStreamReader(
					p.getInputStream())); 
			String line; 
			while((line = reader.readLine()) != null) { 
				System.out.println(">>>>>> "+line);
			} 

		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}


	@SuppressWarnings("finally")
	public  String ExecuteCommand_GetChromeVersion(String inputCommand) 
	{	
		logger.info(UNIQ_EXECUTION_ID+" : Command: " + inputCommand + " is being executed:");
		String output = null;
		Matcher matcher;
		try
		{
			Process p;
			p =  Runtime.getRuntime().exec(inputCommand);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = null;
			Pattern pattern = Pattern.compile("^(Version=)([^:]*)");
			while ((line = reader.readLine()) != null) {
				if (line.matches(pattern.pattern())) {
					matcher = pattern.matcher(line);
					if (matcher.find())
					{
						output = matcher.group().toString();
					}
				}
			}
			logger.info(UNIQ_EXECUTION_ID+" : Command Output - " +output);
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: ExecuteCommand_ReturnsOutput. ",e);
		}
		finally
		{
			return output.toString();
		}
	}

}
