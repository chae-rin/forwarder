package com.chaerin.forwarder;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.chaerin.forwarder.manager.Manager;

public class BootStrap {

	private Manager manager = null;
	public static String PROC_ID = null; 

	public static Logger logger = Logger.getLogger("myLog");
	
	
	/*********************************************
	 *	process start..
	 ********************************************/
	public static void main(String[] args)
	{
		BootStrap bs = null;

		try
		{
			bs = new BootStrap();
			bs.initService();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	


	/**
	 *	manager start
	 *	
	 * @ param 
	 * @ return boolean()
	 * @ exception Exception e
	 */
	private boolean initService()
	{
		boolean result = false;

		try
		{
			if( this.loadProperties() )
			{
				logger.info( "##############################################################" +
						     "\n#\tChaerin Forwarder Process Start..!" + 
						     "\n#\tInitializing & Startup. Ver . 1.0.0 " +
						     "\n##############################################################"
							);
			}


			if( this.manager == null )
				this.manager = new Manager();

			this.manager.start();
			result = true;

			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}




	/**
	 *	load properties 
	 *
	 * @ param 
	 * @ return boolean()
	 * @ exception Exception e
	 */
	private boolean loadProperties()
	{
		boolean result = false;
		
		FileHandler fhandler = null;
		Properties config = null;
		
		try
		{
			String tmpStr = null;
			String logPath = null;
			String logName = null;
			
			config = new Properties();
			config.load( new FileInputStream("./com.chaerin.forwarder.properties") );
			
			
			/**
			 * **********************************************
			 * loading config - system log file 
			 * **********************************************
			 */

			tmpStr = config.getProperty( "system.log.path" );
			if (tmpStr != null)
				logPath = tmpStr;

			tmpStr = config.getProperty( "system.log.file.prefix" );
			if (tmpStr != null)
			{
				if ( !tmpStr.endsWith( "_" ) )
					tmpStr += "_";

				logName = tmpStr + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".log";
			}

			
			fhandler = new FileHandler( logPath + logName, true );
			logger.addHandler(fhandler);
			
			result = true;
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			fhandler = null;
		}
		
	}

	
}
