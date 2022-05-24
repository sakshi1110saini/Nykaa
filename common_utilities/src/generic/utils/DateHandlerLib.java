/**
 * Last Changes Done on 24 Feb, 2015 11:19:44 AM
 * Last Changes Done by Pankaj Katiyar

 * Purpose of change: This class contains the code to pick the desired date from calendar
 */
package generic.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class DateHandlerLib 
{

	private Object UNIQ_EXECUTION_ID = "";
	Logger logger = LogManager.getLogger(DateHandlerLib.class.getName());

	public DateHandlerLib() {

	}

	public DateHandlerLib(Object UNIQ_EXECUTION_ID) {
		this.UNIQ_EXECUTION_ID = UNIQ_EXECUTION_ID;
	}

	/** This method will be used to convert the supplied date format to dd/MM/yy
	 * 
	 * @param suppliedDate
	 * @param suppliedDateFormat
	 * @return
	 * @throws ParseException
	 */
	public String formatDate(String suppliedDate, String suppliedDateFormat) throws ParseException
	{
		/** String dMMMMyy = "February 4, 2015" == "MMMM dd, yyyy"
		 */

		String convertedDate = null;

		try
		{
			SimpleDateFormat suppliedFormat = new SimpleDateFormat(suppliedDateFormat);
			Date date = suppliedFormat.parse(suppliedDate);

			SimpleDateFormat convertedFormat = new SimpleDateFormat("dd/MM/yy");
			convertedDate = convertedFormat.format(date);
			logger.debug(UNIQ_EXECUTION_ID+" : Converted Date: "+convertedDate);
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Couldn't convert the supplied date: "+suppliedDate);
		}
		return convertedDate;
	}


	/** This is a generic method to parse the given string and return the desired date value.
	 * 
	 * @param strDate
	 * @param strDesiredValue
	 * @param returnMonthName
	 * @return
	 */
	public String getDateValues(String strDate, String strDesiredValue, boolean returnMonthName)
	{
		String desiredValue = "";
		try
		{
			/** Sample Date Format:
			 * dd/MM/yy = 29/03/15
			 * MMMM dd, yyyy = February 4, 2015
			 */

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");

			Date date = format.parse(strDate);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			if(strDesiredValue.equalsIgnoreCase("month"))
			{
				if(returnMonthName)
				{
					desiredValue = calendar.getDisplayName(calendar.MONTH, calendar.SHORT, Locale.getDefault());
				}
				else
				{
					desiredValue = String.valueOf(calendar.get(Calendar.MONTH));
				}
			}
			else if(strDesiredValue.equalsIgnoreCase("year"))
			{
				desiredValue =  String.valueOf(calendar.get(Calendar.YEAR));
			}
			else if(strDesiredValue.equalsIgnoreCase("date"))
			{
				desiredValue = String.valueOf(calendar.get(Calendar.DATE));
			}

			logger.info(strDesiredValue+" value: "+desiredValue);
		}
		catch(Exception e)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception occured while parsing supplied date: "+strDate, e);
		}

		return desiredValue;
	}

	public String getCurrentDateTime() {
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new java.util.Date(System.currentTimeMillis());

		return formatter.format(date);
	}


	public String getDateInString(String data){
		String date=null;
		Date currentDate=null;
		String daysToAdd=null;
		try{
			daysToAdd=data.substring(data.indexOf("add")+3, data.indexOf("days")).trim();
			DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
			currentDate= new Date();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(currentDate);
			calendar.add(Calendar.DATE, Integer.parseInt(daysToAdd));
			Date nextdate=calendar.getTime();
			date=dateFormat.format(nextdate);
			logger.info(UNIQ_EXECUTION_ID+" : Get the new date as per the input data :"+date);
		}catch(Exception e){
			logger.error(UNIQ_EXECUTION_ID+" : Unable to get the date", e);
		}
		return date;
	}


	//******************** Get Current Date Time Stamp *************************************************//
	public String getDateTimeStamp(String dateStampFormat)
	{
		try
		{
			//Sample: MMddyyyy_hhmmss
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Please check the supplied date format. " , n);
			return null;
		}
	}


	//******************** Writing The Date Time Stamp *************************************************//
	public String getDateTimeStampWithMiliSecond()
	{
		try
		{
			String dateStampFormat = "MMddyyyy_hhmmss_ms";
			//logger.info(TestUNIQ_EXECUTION_ID+" : Date Time Stamp Format will be:" +dateStampFormat);

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(UNIQ_EXECUTION_ID+" : Exception handled by method: DateTimeStampWithMiliSecond. ", n);
			return null;
		}
	}
}
